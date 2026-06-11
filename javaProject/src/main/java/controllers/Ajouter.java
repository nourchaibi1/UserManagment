package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.User;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import service.UserServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class Ajouter {
    private UserServiceImpl service = new UserServiceImpl();

    @FXML
    private TextField datedenaissance;

    @FXML
    private TextField email;


    @FXML
    private TextField motdepasse;

    @FXML
    private TextField nom;

    @FXML
    private TextField numerotelephone;

    @FXML
    private TextField prenom;

    @FXML
    private TextField role;

    @FXML
    private TextField statut;

    @FXML
    void submit(ActionEvent event) {
        String nomValue = nom.getText();
        String prenomValue = prenom.getText();
        String emailValue = email.getText();
        String motdepasseValue = motdepasse.getText();
        String numerotelephoneValue = numerotelephone.getText();
        String datedenaissanceValue = datedenaissance.getText(); // format: yyyy-MM-dd
        String statutValue = statut.getText();

        //String faceidValue = faceid.getText();

        // Check if any required field is empty
        if (nomValue.isEmpty() || prenomValue.isEmpty() || emailValue.isEmpty() ||
                motdepasseValue.isEmpty() || numerotelephoneValue.isEmpty() ||
                datedenaissanceValue.isEmpty() || statutValue.isEmpty() ) {

            // Show an error alert if any field is empty
            showAlert("Error", "Please fill in all required fields!", AlertType.ERROR);
            return; // Exit the method early if validation fails
        }

        User user = new User();
        user.setNom(nomValue);
        user.setPrenom(prenomValue);
        user.setEmail(emailValue);
        user.setMotdepasse(motdepasseValue);
        user.setNumerotelephone(numerotelephoneValue);

        // Convert date string to LocalDate
        try {
            user.setDatedenaissance(LocalDate.parse(datedenaissanceValue)); // make sure input format is correct
        } catch (Exception e) {
            showAlert("Error", "Please enter a valid date in the format yyyy-MM-dd.", AlertType.ERROR);
            return;
        }
        user.setStatut(statutValue);
        user.setRole("traveler"); // Always set role as traveler
        //user.setFaceid(faceidValue);
user.setBanStatus("Unban");
        // Add user using service
        service.add(user);
        showAlert("Success", "User added successfully!", AlertType.INFORMATION);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml")); // Specify the correct path to your login.fxml
            Parent root = loader.load();

            // Get the current stage (window) and set the new scene to the login page
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Switch to the new scene (login page)
            stage.show(); // Show the login page
        } catch (IOException e) {
            e.printStackTrace(); // Print the error for debugging purposes
        }
    }


    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optional: Set a header or leave null for no header
        alert.setContentText(content);
        Scene alertScene = alert.getDialogPane().getScene();
        alertScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        alert.showAndWait(); // Show the alert and wait for user to close it

    }
    @FXML
    private Button openCameraButton;

    @FXML
    private Label faceStatusLabel;
    @FXML
    private ImageView profileImageView;

    @FXML
    private void openCamera() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("OpenCV loaded successfully: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV library: " + e.getMessage());
            System.err.println("Make sure OpenCV is properly installed and in the path");
            System.exit(1);
        }

        System.out.println(Core.VERSION);

        // Load the Haar Cascade Classifier for face detection
        String cascadePath = "C:\\opencv\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml";
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);

        if (faceDetector.empty()) {
            System.out.println("Error: Could not load cascade classifier");
            return;
        }

        // Open default camera (index 0)
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not detected");
            return;
        }

        // directory for registered faces if it doesn't exist
        File registeredFacesDir = new File("registered_faces");
        if (!registeredFacesDir.exists()) {
            registeredFacesDir.mkdirs();
        }

        // Get username for registration
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Face Registration");
        dialog.setHeaderText("Enter your username");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            camera.release();
            return;
        }

        String username = result.get().trim();

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        long startTime = System.currentTimeMillis(); // Start time
        boolean imageSaved = false;
        int photoCount = 0;
        Mat resizedFace = new Mat();
        while (true) {
            if (camera.read(frame)) {
                // Convert the captured frame to grayscale
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces in the grayscale image
                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faces);

                // Get the list of faces detected
                Rect[] facesArray = faces.toArray();

                // Draw rectangles around detected faces
                for (Rect rect : facesArray) {
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
                }

                // If faces are detected, save the face image
                if (!imageSaved && System.currentTimeMillis() - startTime > 3000 && facesArray.length > 0) {
                    Rect faceRect = facesArray[0];
                    Mat faceROI = new Mat(frame, faceRect); // Save in COLOR
                    //Mat resizedFace = new Mat();
                    Imgproc.resize(faceROI, resizedFace, new Size(100, 100));
       /* if (facesArray.length > 0) {
            // Extract the face ROI (Region of Interest)
            Rect faceRect = facesArray[0];
            Mat faceROI = new Mat(grayFrame, faceRect);

            // Resize to standard size
            Mat resizedFace = new Mat();
            Imgproc.resize(faceROI, resizedFace, new Size(100, 100));*/

                    // Save the processed face
                    String filename = "registered_faces/" + username + ".jpg";
                    Imgcodecs.imwrite(filename, resizedFace);
                    System.out.println("Face registered as: " + filename);

                    showAlert("Success", "Face registered successfully for user: " + username);
                    photoCount++;
                    break;
                }

                HighGui.imshow("Face Registration", frame);
                if (HighGui.waitKey(30) == 27 || imageSaved) { // Exit on ESC key
                    break;
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();

        // Now display in JavaFX ImageView
        if (!resizedFace.empty()) {
            Image fxImage = matToImage(resizedFace);
            profileImageView.setImage(fxImage);
        }
    }
    private Image matToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    // Helper method to display alerts
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
   /* @FXML
    private void openCamera() {

        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("OpenCV loaded successfully: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV library: " + e.getMessage());
            System.err.println("Make sure OpenCV is properly installed and in the path");
            System.exit(1);
        }

        // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.VERSION);
        //*****************************
        // Load the Haar Cascade Classifier for face detection
        String cascadePath = "C:\\opencv\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml"; // Replace with the actual path
        CascadeClassifier faceDetector = new CascadeClassifier(cascadePath);

        if (faceDetector.empty()) {
            System.out.println("Error: Could not load cascade classifier");
            return;
        }
        //*****************
        // Open default camera (index 0)
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not detected");
            return;
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        int photoCount = 0; // Counter for saving images

        while (true) {
            if (camera.read(frame)) {
                //***
                // Convert the captured frame to grayscale
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces in the grayscale image
                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faces);

                // Get the list of faces detected
                Rect[] facesArray = faces.toArray();

                // Draw rectangles around detected faces
                for (Rect rect : facesArray) {
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
                }
// If faces are detected, save the frame as a photo
                if (facesArray.length > 0) {
                    // Save the frame to a file
                    String filename = "face_detected_" + photoCount + ".jpg";
                    Imgcodecs.imwrite(filename, frame); // Save the image
                    System.out.println("Photo saved as: " + filename);
                    photoCount++; // Increment photo count to avoid overwriting
                    break;
                    // Optionally, you can stop after saving the first photo
                    // break;
                }

                //**
                HighGui.imshow("Camera Feed", frame);
                if (HighGui.waitKey(30) == 27) { // Exit on ESC key
                    break;
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();}
    // Method to detect faces in a frame
    private List<Rect> detectFaces(CascadeClassifier faceDetector, Mat grayFrame) {
        // Detect faces in the grayscale image
        MatOfRect facesDetected = new MatOfRect();
        faceDetector.detectMultiScale(grayFrame, facesDetected);

        // Return the list of detected faces (Rectangles)
        return facesDetected.toList();
    }*/
//************************faceid*****************************
/*
    @FXML
private Button openCameraButton;

    @FXML
    private Label faceStatusLabel;



    private VideoCapture capture;
    private boolean cameraActive = false;
    private CascadeClassifier faceDetector;

    public void initialize() {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        faceDetector = new CascadeClassifier("resources/haarcascade_frontalface_alt.xml");
    }


    @FXML
    private void openCamera() {
        if (cameraActive) return;

        capture = new VideoCapture(0);
        if (capture.isOpened()) {
            Mat frame = new Mat();
            capture.read(frame);

            if (!frame.empty()) {
                // Convert to grayscale
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces
                var faceDetections = new org.opencv.core.MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faceDetections);

                if (!faceDetections.empty()) {
                    // Save image of the first face
                    Imgcodecs.imwrite("user_face.png", frame); // You could encode instead
                    faceStatusLabel.setText("Face captured successfully!");
                } else {
                    faceStatusLabel.setText("No face detected. Try again.");
                }
            } else {
                faceStatusLabel.setText("Failed to read from camera.");
            }

            capture.release();
        } else {
            faceStatusLabel.setText("Camera not detected.");
        }

        cameraActive = false;
    }*/
}
