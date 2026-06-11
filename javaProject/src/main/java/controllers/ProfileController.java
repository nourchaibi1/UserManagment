package controllers;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
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

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import models.User;
import models.UserSession;
import service.UserServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.application.Platform;
import javafx.stage.FileChooser;


public class ProfileController {
    @FXML private HBox navBar;
    @FXML private VBox mainContent;
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button dashboardButton;
    @FXML private Button logoutButton;

    @FXML
    private Button saveButton;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField statutField;
    @FXML private TextField roleField;
    @FXML
    private VBox profileForm;
    @FXML
    private ImageView logoImage;
    private User currentUser;
    public void setCurrentUser(User user) {
        this.currentUser = user;

        nomField.setText(currentUser.getNom());
        prenomField.setText(currentUser.getPrenom());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getNumerotelephone());

        if (currentUser.getDatedenaissance() != null) {
            birthDatePicker.setValue(currentUser.getDatedenaissance());
        }

        statutField.setText(currentUser.getStatut());
        roleField.setText(currentUser.getRole()); 


        profileForm.setVisible(true);
    }


    private boolean isEditMode = false;

    @FXML
    private void handleEditProfile() {
        System.out.println("handleEditProfile() triggered");
        if (currentUser == null) {
            System.out.println("currentUser is null!");
            showAlert("Error", "User is not logged in or currentUser is not set.");
            return;
        }

        isEditMode = !isEditMode;

        nomField.setEditable(isEditMode);
        prenomField.setEditable(isEditMode);
        emailField.setEditable(isEditMode);
        phoneField.setEditable(isEditMode);
        birthDatePicker.setDisable(!isEditMode); 
        statutField.setEditable(isEditMode);
        roleField.setEditable(isEditMode); 
        saveButton.setVisible(isEditMode);
        // Show or hide the form (optional)
        profileForm.setVisible(true);
    }
    @FXML
    private void handleSaveChanges() {
        if (currentUser == null) return;

        // Update currentUser fields from UI
        currentUser.setNom(nomField.getText());
        currentUser.setPrenom(prenomField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setNumerotelephone(phoneField.getText());
        currentUser.setDatedenaissance(birthDatePicker.getValue());
        currentUser.setStatut(statutField.getText());
        currentUser.setRole(roleField.getText());

        // Update user in your service
        userService.modify(currentUser);
        showAlert("Success", "Profile updated successfully.");


        // Exit edit mode
        isEditMode = false;
        nomField.setEditable(false);
        prenomField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        birthDatePicker.setDisable(true);
        statutField.setEditable(false);
        roleField.setEditable(false);

        saveButton.setVisible(false);  }


    private UserServiceImpl userService = new UserServiceImpl (); // or inject it



    private UserServiceImpl passwordService = new UserServiceImpl(); // Or use dependency injection
    @FXML
    private void handleChangePassword() {
        //  the password change dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");

        // Set the button types
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
        //  the password fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password (minimum 8 characters)");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPassword, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node changeButton = dialog.getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);

        newPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            changeButton.setDisable(!validatePasswords(newPassword.getText(), confirmPassword.getText()));
        });

        confirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            changeButton.setDisable(!validatePasswords(newPassword.getText(), confirmPassword.getText()));
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                return new Pair<>(currentPassword.getText(), newPassword.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(passwords -> {
            String currentPwd = passwords.getKey();
            String newPwd = passwords.getValue();

            try {
                boolean success = passwordService.changerMotDePasse(
                        currentUser.getId(),
                        currentPwd,  
                        newPwd     
                );

                if (success) {
                    showAlert("Success", "Password changed successfully!");
                } else {
                    showAlert("Error", "Failed to change password. Check your current password.");
                }
            } catch (Exception e) {
                showAlert("Error", "An error occurred: " + e.getMessage());
            }
        });
    }

 
    private boolean validatePasswords(String newPwd, String confirmPwd) {
        return newPwd.equals(confirmPwd) && newPwd.length() >= 6;
    }



    @FXML
    private void handleDeleteAccount() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                    try {
                        Stage stage = (Stage) profileForm.getScene().getWindow();

                        userService.delete(currentUser.getId()); 

                        Alert info = new Alert(AlertType.INFORMATION);
                        info.setTitle("Account Deleted");
                        info.setHeaderText(null);
                        info.setContentText("Your account has been successfully deleted.");
                        info.showAndWait();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
                        Parent root = loader.load();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (Exception e) {
                        showAlert("Error", "Failed to delete account: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
    }





    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button uploadPhotoButton;
    @FXML
    private Button takePhotoButton;
    @FXML
    private Label welcomeLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private void initialize() {
        mainBorderPane.setTranslateX(-300);
        mainBorderPane.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(800), mainBorderPane);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), mainBorderPane);
        slide.setFromX(-300);
        slide.setToX(0);

        ParallelTransition animation = new ParallelTransition(fade, slide);
        animation.play();

        profileImageView.setFitWidth(150);
        profileImageView.setFitHeight(150);
        profileImageView.setPreserveRatio(true);
        
        loadProfileImage();
        UserSession session = UserSession.getInstance();
        if (dashboardButton != null) {
            dashboardButton.setVisible("admin".equalsIgnoreCase(session.getUserRole()));
        }
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + session.getUsername());
        }
        User currentUser = (User) session.getUserData("userObject");
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getNumerotelephone()); 

            if (currentUser.getDatedenaissance() != null) {
                birthDatePicker.setValue(currentUser.getDatedenaissance()); 
            }

            statutField.setText(currentUser.getStatut());
            roleField.setText(currentUser.getRole());
        } else {
            System.out.println("No user found in session data");
        }
        nomField.setEditable(false);
        prenomField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
        birthDatePicker.setDisable(true);
        statutField.setEditable(false);
        roleField.setEditable(false);
        saveButton.setVisible(false);
    }

    private void loadProfileImage() {
        try {
            String imagePath = "profile_images/" + currentUser.getId() + ".jpg";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Error loading profile image: " + e.getMessage());
        }
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                //  profile_images directory if it doesn't exist
                File profileDir = new File("profile_images");
                if (!profileDir.exists()) {
                    profileDir.mkdir();
                }

                String newPath = "profile_images/" + currentUser.getId() + ".jpg";
                Files.copy(selectedFile.toPath(), new File(newPath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                Image image = new Image(new File(newPath).toURI().toString());
                profileImageView.setImage(image);

                showAlert("Success", "Profile photo updated successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save profile photo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleTakePhoto() {
        try {
            // Initialize OpenCV
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            //  a new window for camera capture
            Stage cameraStage = new Stage();
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));

            //  image view for camera feed
            ImageView cameraView = new ImageView();
            cameraView.setFitWidth(640);
            cameraView.setFitHeight(480);
            cameraView.setPreserveRatio(true);

            Button captureButton = new Button("Capture Photo");

            root.getChildren().addAll(cameraView, captureButton);
            Scene scene = new Scene(root);
            cameraStage.setScene(scene);
            cameraStage.setTitle("Take Photo");

            VideoCapture camera = new VideoCapture(0);
            if (!camera.isOpened()) {
                showAlert("Error", "Could not open camera");
                return;
            }

            Thread cameraThread = new Thread(() -> {
                Mat frame = new Mat();
                while (cameraStage.isShowing()) {
                    if (camera.read(frame)) {
                        Image image = mat2Image(frame);
                        Platform.runLater(() -> cameraView.setImage(image));
                    }
                }
                camera.release();
            });
            cameraThread.setDaemon(true);
            cameraThread.start();

            captureButton.setOnAction(e -> {
                Mat frame = new Mat();
                if (camera.read(frame)) {
                    try {
                        File profileDir = new File("profile_images");
                        if (!profileDir.exists()) {
                            profileDir.mkdir();
                        }

                        String imagePath = "profile_images/" + currentUser.getId() + ".jpg";
                        Imgcodecs.imwrite(imagePath, frame);

                        Image image = mat2Image(frame);
                        Platform.runLater(() -> {
                            profileImageView.setImage(image);
                            showAlert("Success", "Photo captured successfully!");
                            cameraStage.close();
                        });
                    } catch (Exception ex) {
                        showAlert("Error", "Failed to save captured photo: " + ex.getMessage());
                    }
                }
            });

            cameraStage.show();

        } catch (Exception e) {
            showAlert("Error", "Failed to initialize camera: " + e.getMessage());
        }
    }

    private Image mat2Image(Mat frame) {
        try {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".jpg", frame, buffer);
            return new Image(new ByteArrayInputStream(buffer.toArray()));
        } catch (Exception e) {
            System.err.println("Error converting Mat to Image: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Logout Failed");
            alert.setContentText("Could not return to login page. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleMenuPage(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not load the home page.");
            alert.setContentText("Please try again.");
            alert.showAndWait();
        }
    }

  
    @FXML
    private void handleDashboard() {
    }
    }

