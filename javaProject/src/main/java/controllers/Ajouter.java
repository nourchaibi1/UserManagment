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

    private final UserServiceImpl service = new UserServiceImpl();

    // Chemin OpenCV externalisé — à mettre dans un fichier config ou variable d'env
    private static final String CASCADE_PATH =
            System.getProperty("opencv.cascade.path",
            "C:\\opencv\\opencv\\build\\etc\\haarcascades\\haarcascade_frontalface_default.xml");

    @FXML private TextField nom;
    @FXML private TextField prenom;
    @FXML private TextField email;
    @FXML private TextField motdepasse;
    @FXML private TextField numerotelephone;
    @FXML private TextField datedenaissance;
    @FXML private TextField statut;
    @FXML private Button openCameraButton;
    @FXML private Label faceStatusLabel;
    @FXML private ImageView profileImageView;

    // ─── Inscription ──────────────────────────────────────────────────────────

    @FXML
    void submit(ActionEvent event) {
        String nomVal    = nom.getText();
        String prenomVal = prenom.getText();
        String emailVal  = email.getText();
        String mdpVal    = motdepasse.getText();
        String telVal    = numerotelephone.getText();
        String dateVal   = datedenaissance.getText();
        String statutVal = statut.getText();

        if (nomVal.isEmpty() || prenomVal.isEmpty() || emailVal.isEmpty() ||
            mdpVal.isEmpty() || telVal.isEmpty() || dateVal.isEmpty() || statutVal.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", AlertType.ERROR);
            return;
        }

        User user = new User();
        user.setNom(nomVal);
        user.setPrenom(prenomVal);
        user.setEmail(emailVal);
        user.setMotdepasse(mdpVal);
        user.setNumerotelephone(telVal);
        user.setStatut(statutVal);
        user.setRole("traveler");
        user.setBanStatus("Unban");

        try {
            user.setDatedenaissance(LocalDate.parse(dateVal));
        } catch (Exception e) {
            showAlert("Erreur", "Format de date invalide (yyyy-MM-dd).", AlertType.ERROR);
            return;
        }

        service.add(user);
        showAlert("Succès", "Utilisateur ajouté avec succès.", AlertType.INFORMATION);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ─── Face ID ──────────────────────────────────────────────────────────────

    @FXML
    private void openCamera() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            showAlert("Erreur", "OpenCV introuvable : " + e.getMessage(), AlertType.ERROR);
            return;
        }

        CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_PATH);
        if (faceDetector.empty()) {
            showAlert("Erreur", "Impossible de charger le classificateur.", AlertType.ERROR);
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            showAlert("Erreur", "Caméra non détectée.", AlertType.ERROR);
            return;
        }

        new File("registered_faces").mkdirs();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enregistrement du visage");
        dialog.setHeaderText("Entrez votre nom d'utilisateur");
        dialog.setContentText("Nom :");
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent() || result.get().trim().isEmpty()) {
            camera.release();
            return;
        }

        String username = result.get().trim();
        Mat frame = new Mat();
        Mat resizedFace = new Mat();
        long startTime = System.currentTimeMillis();
        boolean imageSaved = false;

        while (!imageSaved) {
            if (camera.read(frame)) {
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                MatOfRect faces = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faces);
                Rect[] facesArray = faces.toArray();

                for (Rect rect : facesArray) {
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
                }

                if (System.currentTimeMillis() - startTime > 3000 && facesArray.length > 0) {
                    Mat faceROI = new Mat(frame, facesArray[0]);
                    Imgproc.resize(faceROI, resizedFace, new Size(100, 100));
                    String filename = "registered_faces/" + username + ".jpg";
                    Imgcodecs.imwrite(filename, resizedFace);
                    imageSaved = true;
                    showAlertAsync("Succès", "Visage enregistré pour : " + username);
                }

                HighGui.imshow("Enregistrement du visage", frame);
                if (HighGui.waitKey(30) == 27) break; // ESC pour quitter
            }
        }

        camera.release();
        HighGui.destroyAllWindows();

        if (!resizedFace.empty()) {
            Image fxImage = matToImage(resizedFace);
            profileImageView.setImage(fxImage);
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Image matToImage(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getScene().getStylesheets()
             .add(getClass().getResource("/style.css").toExternalForm());
        alert.showAndWait();
    }

    private void showAlertAsync(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
