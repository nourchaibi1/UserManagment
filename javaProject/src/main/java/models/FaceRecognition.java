package models;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FaceRecognition {
    private static final String CASCADE_PATH = "C:\\opencv\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml";
    private static final int FACE_SIZE = 200;
    private static final double MATCH_THRESHOLD = 0.4; // Lower threshold for more strict matching

    private CascadeClassifier faceDetector;
    private VideoCapture camera;
    private boolean isRunning;
    private List<String> knownFaces;
    private Consumer<String> onFaceRecognized;
    private int recognitionCount = 0;
    private static final int RECOGNITION_THRESHOLD = 10;

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            try {
                String opencvPath = "C:\\opencv\\opencv\\build\\java\\x64\\opencv_java455.dll";
                System.load(opencvPath);
            } catch (UnsatisfiedLinkError e2) {
                try {
                    System.load(new File("opencv_java455.dll").getAbsolutePath());
                } catch (UnsatisfiedLinkError e3) {
                    System.err.println("Failed to load OpenCV library. Please ensure OpenCV is properly installed.");
                    System.err.println("Error 1: " + e.getMessage());
                    System.err.println("Error 2: " + e2.getMessage());
                    System.err.println("Error 3: " + e3.getMessage());
                    throw new RuntimeException("Failed to load OpenCV library", e3);
                }
            }
        }
    }

    public FaceRecognition(Consumer<String> onFaceRecognized) {
        this.onFaceRecognized = onFaceRecognized;
        try {
            System.out.println("OpenCV loaded successfully: " + Core.VERSION);

            faceDetector = new CascadeClassifier(CASCADE_PATH);
            if (faceDetector.empty()) {
                throw new RuntimeException("Error: Could not load face detector cascade classifier");
            }

            knownFaces = new ArrayList<>();
            loadKnownFaces();

            // Create registered_faces directory if it doesn't exist
            File registeredFacesDir = new File("registered_faces");
            if (!registeredFacesDir.exists()) {
                registeredFacesDir.mkdir();
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to initialize face recognition: " + e.getMessage());
            throw new RuntimeException("Failed to initialize face recognition", e);
        }
    }

    private void loadKnownFaces() {
        File registeredFacesDir = new File("registered_faces");
        if (registeredFacesDir.exists()) {
            File[] files = registeredFacesDir.listFiles((dir, name) -> name.endsWith(".jpg"));
            if (files != null) {
                for (File file : files) {
                    knownFaces.add(file.getName().replace(".jpg", ""));
                    System.out.println("Loaded registered face: " + file.getName());
                }
            }
        }
        System.out.println("Total registered faces: " + knownFaces.size());
    }

    public void startFaceRecognition() {
        isRunning = true;
        camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            showAlert("Error", "Could not open camera");
            return;
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        HighGui.namedWindow("Authentication", HighGui.WINDOW_AUTOSIZE);

        while (isRunning) {
            if (camera.read(frame)) {
                processFrame(frame, grayFrame);
                if (HighGui.waitKey(30) >= 0) break;
            }
        }

        cleanup();
    }

    private void processFrame(Mat frame, Mat grayFrame) {
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayFrame, faces, 1.1, 5, 0, new Size(30, 30));
        Rect[] facesArray = faces.toArray();

        if (facesArray.length == 0) {
            Imgproc.putText(frame, "No face detected",
                    new Point(10, 30),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 0, 255), 2);
            recognitionCount = 0;
        }

        for (Rect faceRect : facesArray) {
            Mat faceROI = new Mat(grayFrame, faceRect);
            Mat resizedFace = new Mat();
            Imgproc.resize(faceROI, resizedFace, new Size(FACE_SIZE, FACE_SIZE));

            String matchedUser = findMatchingFace(resizedFace);
            if (matchedUser != null) {
                recognitionCount++;
                Imgproc.putText(frame, "Authenticated: " + matchedUser + " (" + recognitionCount + "/" + RECOGNITION_THRESHOLD + ")",
                        new Point(faceRect.x, faceRect.y - 10),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 255, 0), 2);
                System.out.println("Face recognized as: " + matchedUser + " (" + recognitionCount + "/" + RECOGNITION_THRESHOLD + ")");

                if (recognitionCount >= RECOGNITION_THRESHOLD) {
                    if (onFaceRecognized != null) {
                        Platform.runLater(() -> onFaceRecognized.accept(matchedUser));
                    }
                    stop();
                    return;
                }
            } else {
                recognitionCount = 0;
                Imgproc.putText(frame, "Not recognized",
                        new Point(faceRect.x, faceRect.y - 10),
                        Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(0, 0, 255), 2);
                System.out.println("Face not recognized");
            }

            Imgproc.rectangle(frame, faceRect.tl(), faceRect.br(), new Scalar(0, 255, 0), 3);
        }

        HighGui.imshow("Authentication", frame);
    }

    private String findMatchingFace(Mat faceImage) {
        if (knownFaces.isEmpty()) {
            System.out.println("No registered faces found");
            return null;
        }

        double bestMatch = 0.0;
        String matchedUser = null;

        for (String username : knownFaces) {
            String facePath = "registered_faces/" + username + ".jpg";
            Mat registeredFace = Imgcodecs.imread(facePath, Imgcodecs.IMREAD_GRAYSCALE);

            if (!registeredFace.empty()) {
                Mat resizedRegFace = new Mat();
                Imgproc.resize(registeredFace, resizedRegFace, new Size(FACE_SIZE, FACE_SIZE));

                // Apply histogram equalization to both images
                Imgproc.equalizeHist(faceImage, faceImage);
                Imgproc.equalizeHist(resizedRegFace, resizedRegFace);

                Mat result = new Mat();
                Imgproc.matchTemplate(faceImage, resizedRegFace, result, Imgproc.TM_CCOEFF_NORMED);

                Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                double similarity = mmr.maxVal;

                System.out.println("Similarity with " + username + ": " + similarity);

                if (similarity > bestMatch) {
                    bestMatch = similarity;
                    matchedUser = username;
                }
            }
        }

        System.out.println("Best match: " + bestMatch + " with user: " + matchedUser);
        return bestMatch > MATCH_THRESHOLD ? matchedUser : null;
    }

    public void registerFace(String username, Mat faceImage) {
        try {
            // Convert to grayscale if not already
            Mat grayFace = new Mat();
            if (faceImage.channels() > 1) {
                Imgproc.cvtColor(faceImage, grayFace, Imgproc.COLOR_BGR2GRAY);
            } else {
                faceImage.copyTo(grayFace);
            }

            Imgproc.equalizeHist(grayFace, grayFace);

            Mat resizedFace = new Mat();
            Imgproc.resize(grayFace, resizedFace, new Size(FACE_SIZE, FACE_SIZE));

            String facePath = "registered_faces/" + username + ".jpg";
            boolean saved = Imgcodecs.imwrite(facePath, resizedFace);

            if (!saved) {
                throw new RuntimeException("Failed to save face image");
            }

            if (!knownFaces.contains(username)) {
                knownFaces.add(username);
                System.out.println("Registered new face for: " + username);
            }

            showAlert("Success", "Face registered successfully for " + username);
        } catch (Exception e) {
            showAlert("Error", "Failed to register face: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanup() {
        isRunning = false;
        if (camera != null) {
            camera.release();
        }
        HighGui.destroyAllWindows();
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void stop() {
        isRunning = false;
    }
} 
