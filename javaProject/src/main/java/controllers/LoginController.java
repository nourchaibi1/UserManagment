package controllers;
import com.google.api.client.auth.oauth2.Credential;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import models.User;  
import utils.GoogleSignIn;


import java.security.NoSuchAlgorithmException;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import models.FaceRecognition;
import models.UserSession;
import org.opencv.utils.Converters;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.face.FaceRecognizer;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.scene.image.*;

import javafx.scene.image.ImageView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.event.ActionEvent;
import  models.User;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import service.UserServiceImpl;
import utils.AuthUtils;


import org.opencv.videoio.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.face.LBPHFaceRecognizer;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import controllers.ProfileController;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.List;
import javafx.scene.canvas.Canvas;

import javafx.scene.control.Button;

import javafx.scene.text.Font;
import java.util.Random;
import java.util.Properties;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import javafx.scene.control.Label;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import utils.MaConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;



public class LoginController {
    private User currentUser;
    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Hyperlink registerLink;
    // Convert OpenCV Mat to JavaFX Image

    @FXML
    private void handleFaceIdLogin() {try {
        // registered_faces directory if it doesn't exist
        File registeredFacesDir = new File("registered_faces");
        if (!registeredFacesDir.exists()) {
            registeredFacesDir.mkdir();
        }

        // Check if there are any registered faces
        File[] registeredFaces = registeredFacesDir.listFiles((dir, name) -> name.endsWith(".jpg"));
        if (registeredFaces == null || registeredFaces.length == 0) {
            showAlert("Warning", "No registered faces found. Please register your face first.", Alert.AlertType.WARNING);
            return;
        }

        // Initialize face recognition with callback
        FaceRecognition faceRecognition = new FaceRecognition(matchedUsername -> {
            try {
                // Get the authenticated user
                UserServiceImpl service = new UserServiceImpl();
                User user = service.searchByName(matchedUsername);
                String emailText = email.getText();
                User userr = service.searchByEmail(emailText);
                if (user != null) {
                    UserSession session = UserSession.getInstance();
                    session.startSession(user.getId(),user.getPrenom(),user.getNom(), user.getRole()); // Pass user ID, name, and role
                    session.setUserData("role", user.getRole());
                    session.setUserData("userEmail", user.getEmail());
                    // Store the entire user object in the session if that's helpful
                    session.setUserData("user", user);



                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
                            Parent root = loader.load();

                            MenuPageController controller = loader.getController();
                            controller.setUserRole(user.getRole());
                            controller.setUser(user);

                            // Get the current stage from any control in the scene
                            Stage stage = (Stage) email.getScene().getWindow();
                            stage.setScene(new Scene(root));
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showAlert("Error", "Failed to load menu page: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    });
                } else {
                    showAlert("Error", "User not found in database", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to authenticate user: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Start face recognition
        faceRecognition.startFaceRecognition();
    } catch (UnsatisfiedLinkError e) {
        showAlert("Error", "OpenCV library not found. Please ensure OpenCV is properly installed and the DLL is in the system path.", Alert.AlertType.ERROR);
        e.printStackTrace();
    } catch (Exception e) {
        showAlert("Error", "Failed to start face recognition: " + e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }

}
    // Helper method to get the username of the recognized face
    private String getRecognizedUsername() {

        // Return the username that was stored during face recognition
        if (recognizedUser != null && !recognizedUser.isEmpty()) {
            return recognizedUser;
        }

        // Fallback: Check if there are any registered faces
        File registeredFacesDir = new File("registered_faces");
        File[] registeredFaces = registeredFacesDir.listFiles((dir, name) -> name.endsWith(".jpg"));

        if (registeredFaces != null && registeredFaces.length > 0) {
            // Return the first registered username as a fallback
            return registeredFaces[0].getName().replace(".jpg", "");
        }

        return "default@email.com"; // Fallback - replace with actual logic


        //System.out.println("Login successful - navigating to main screen");
    }


  

   
    private String recognizedUser;
    // Helper method to navigate to main screen after successful login
    private void navigateToMainScreen() {
        try {
            // Get the authenticated user
            UserServiceImpl service = new UserServiceImpl();
            // You need to store and retrieve the username from the recognized face
            // This could be extracted from the matched image filename or stored during authentication
            String recognizedUsername = getRecognizedUsername();
            //User user = service.searchByEmail(recognizedUsername);
            User user = service.searchByName(recognizedUsername);
            if (user != null) {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
                        Parent root = loader.load();

                        MenuPageController controller = loader.getController();
                        controller.setUserRole(user.getRole());
                        controller.setUser(user);

                        // Get the current stage from any control in the scene
                        Stage stage = (Stage) email.getScene().getWindow(); // Assuming 'email' is an accessible control
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error loading menu page: " + e.getMessage());
                    }
                });
            } else {
                showAlert("Erreur", "Utilisateur non trouvé dans la base de données", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during navigation: " + e.getMessage());
        }
    }

  

    private boolean validateCredentials(String email, String password) {
        String storedPasswordHash = userDatabase.get(email);
        if (storedPasswordHash == null) {
            System.out.println("No user found with email: " + email);
            return false;
        }

        // This is important - BCrypt needs to use compare, not equals
        boolean passwordMatch = AuthUtils.checkPassword(password, storedPasswordHash);
        System.out.println("Password verification result: " + passwordMatch);

        return passwordMatch;
    }
    // Handle Login action
    @FXML
    private void handleLogin(ActionEvent event) {
        if (!validateCaptcha()) {
            showError("Invalid verification code");
            generateCaptcha(); // Generate new CAPTCHA after failed attempt
            return;
        }
        String userEmail = email.getText();
        String userPassword = password.getText();
        // Debug statements
        System.out.println("Login attempt for: " + email);
        System.out.println("Stored password hash in DB: " + userDatabase.get(email));
        System.out.println("Attempted password: " + password);
        System.out.println("Hash of attempted password: " + AuthUtils.hashPassword(userPassword));

        // Check if credentials are valid
        boolean isValid = validateCredentials(userEmail,userPassword);

        System.out.println("Login validation result: " + isValid);
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
            return;
        }

        UserServiceImpl service = new UserServiceImpl();
        User user = service.searchByEmail(userEmail);
        //User user = service.searchByEmailAndPassword(userEmail, userPassword);

        if (user != null) {
            if (user.getBanStatus().equals("Ban")) {
                showAlert("Error", "You are banned and cannot log in", Alert.AlertType.INFORMATION);

                return;
            }
            // Compare the entered password with the stored encrypted password
            if (AuthUtils.checkPassword(userPassword, user.getMotdepasse()))
                /* if (AuthUtils.checkPassword(userPassword, user.getMotdepasse()))*/ {
                showAlert("Succès", "Connexion réussie", Alert.AlertType.INFORMATION);
                //showAlert("Succès", "Connexion réussie", Alert.AlertType.INFORMATION);
                UserSession session = UserSession.getInstance();
                session.startSession(user.getId(),user.getPrenom(),user.getNom(), user.getRole()); // Pass user ID, name, and role
                session.setUserData("role", user.getRole());
                session.setUserData("userEmail", user.getEmail());
                // Store the entire user object in the session if that's helpful
                session.setUserData("user", user);
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
                    Parent root = loader.load();

                    MenuPageController controller = loader.getController();
                    controller.setUserRole(user.getRole());
                    controller.setUser(user);

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
            }
        }}

    // Go to Registration page
    @FXML
    void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterpers.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)email.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to load the registration page.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Scene alertScene = alert.getDialogPane().getScene();
        alertScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        alert.showAndWait();
    }
    //******************************fogot pass

    // Simulated database of users (email -> password)
    private static Map<String, String> userDatabase = new HashMap<>();
    // Store reset tokens (token -> email)
    private static Map<String, String> resetTokens = new HashMap<>();


     private static final String EMAIL_USERNAME = "chaibinour899@gmail.com";
    private static final String EMAIL_PASSWORD = "tnaliskwnntccfys"; // passcode


    static {
        // Add some users to our simulated database
        userDatabase.put("chaibinour899@gmail.com", AuthUtils.hashPassword("tnaliskwnntccfys"));
        //userDatabase.put("test@example.com", "test123");
        // Add a test token that doesn't expire (for debugging)
        resetTokens.put("test123", "chaibinour899@gmail.com");
    }

    private boolean validateEmail(String email) {
        // Check if email exists in our database
        return userDatabase.containsKey(email);
    }

    private String generateResetToken(String email) {
        // Generate a unique token
        String token = UUID.randomUUID().toString();
        // Store the token with the associated email
        resetTokens.put(token, "chaibinour899@gmail.com");

        return token;
    }


    private boolean sendResetEmail(String toEmail, String resetToken) {
        if (EMAIL_PASSWORD == null || EMAIL_PASSWORD.isEmpty()) {
            System.err.println("Email password not set. Set the EMAIL_PASSWORD environment variable.");
            return false;
        }

        List<Map<String, Object>> serverConfigs = new ArrayList<>();


        Map<String, Object> gmail = new HashMap<>();
        gmail.put("host", "smtp.gmail.com");
        gmail.put("port", "587");
        gmail.put("ssl", false);
        gmail.put("starttls", true);
        serverConfigs.add(gmail);
// *** IMPORTANT: This disables certificate validation (for development only) ***


        for (Map<String, Object> config : serverConfigs) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", config.get("starttls").toString());
                props.put("mail.smtp.ssl.enable", config.get("ssl").toString());
                props.put("mail.smtp.host", config.get("host").toString());
                props.put("mail.smtp.port", config.get("port").toString());
                props.put("mail.smtp.timeout", "5000");
                props.put("mail.smtp.connectiontimeout", "5000");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                    }
                });

                session.setDebug(true);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_USERNAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Password Reset Request");

                String resetLink = "TOKEN: " + resetToken +
                        "\n\nEnter this token in the password reset screen of the application.";

                message.setText("Dear User,\n\n" +
                        "You requested a password reset. Here is your password reset token:\n\n" +
                        resetLink + "\n\n" +
                        "This token will expire in 24 hours.\n\n" +
                        "If you did not request a password reset, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Your Application Team");

                Transport transport = session.getTransport("smtp");
                try {
                    transport.connect(
                            config.get("host").toString(),
                            Integer.parseInt(config.get("port").toString()),
                            EMAIL_USERNAME,
                            EMAIL_PASSWORD
                    );
                    transport.sendMessage(message, message.getAllRecipients());
                    System.out.println("Password reset email sent successfully to: " + toEmail +
                            " using " + config.get("host"));
                    return true;
                } finally {
                    transport.close();
                }
            } catch (MessagingException e) {
                String errorMsg = "Failed with config " + config.get("host") + ":" + config.get("port") +
                        " - " + e.getMessage();
                System.err.println(errorMsg);

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                System.err.println(sw.toString());
            }
        }

        System.err.println("All email sending attempts failed for: " + toEmail);
        return false;
    }

    @FXML
    private void handleForgotPassword() {
        System.out.println("Forgot Password clicked!");
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Reset");
        dialog.setHeaderText("Enter your email address to reset your password");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Email address");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return emailField.getText().trim();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(email -> {
            try {
                if (validateEmail(email)) {
                    showAlert(Alert.AlertType.INFORMATION,
                            "Sending Email",
                            "Attempting to send password reset email...");

                    String resetToken = generateResetToken(email);

                    boolean emailSent = sendResetEmail(email, resetToken);

                    if (emailSent) {
                        showAlert(Alert.AlertType.INFORMATION,
                                "Password Reset",
                                "A password reset link has been sent to your email address.");
                        handlePasswordReset();
                    } else {
                        showAlert(Alert.AlertType.ERROR,
                                "Email Sending Failed",
                                "We were unable to send the password reset email. Please check your internet connection and try again later.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING,
                            "Email Not Found",
                            "No account found with that email address.");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR,
                        "System Error",
                        "An unexpected error occurred. Please try again later.");
                ex.printStackTrace();
            }
        });
    }
    private ProfileController profileController;

    public void setProfileController(ProfileController controller) {
        this.profileController = controller;
    }
  
@FXML
private void handlePasswordReset() {
    System.out.println("Current reset tokens in system: " + resetTokens);
    Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle("Reset Password");
    dialog.setHeaderText("Enter your reset token and new password");

    ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));

    TextField tokenField = new TextField();
    tokenField.setPromptText("Reset token");
    PasswordField passwordField = new PasswordField();
    passwordField.setPromptText("New password");
    PasswordField confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm password");

    grid.add(new Label("Token:"), 0, 0);
    grid.add(tokenField, 1, 0);
    grid.add(new Label("New Password:"), 0, 1);
    grid.add(passwordField, 1, 1);
    grid.add(new Label("Confirm Password:"), 0, 2);
    grid.add(confirmPasswordField, 1, 2);

    Node resetButton = dialog.getDialogPane().lookupButton(resetButtonType);
    resetButton.setDisable(true);

    BooleanBinding isValid = Bindings.createBooleanBinding(() ->
                    !tokenField.getText().trim().isEmpty() &&
                            !passwordField.getText().trim().isEmpty() &&
                            passwordField.getText().equals(confirmPasswordField.getText()),
            tokenField.textProperty(),
            passwordField.textProperty(),
            confirmPasswordField.textProperty());

    resetButton.disableProperty().bind(isValid.not());

    dialog.getDialogPane().setContent(grid);

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == resetButtonType) {
            return new Pair<>(tokenField.getText(), passwordField.getText());
        }
        return null;
    });

    Optional<Pair<String, String>> result = dialog.showAndWait();
    result.ifPresent(tokenPassword -> {
        String token = tokenPassword.getKey();
        String newPassword = tokenPassword.getValue();

        // Verify the token and reset the password
        String email = resetTokens.get(token);
        if (email == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Token",
                    "This password reset token is invalid or has expired.");
            return;
        }

        System.out.println("Entered Token: " + token);
        System.out.println("Associated Email: " + email);
        System.out.println("New Password (before hashing): " + newPassword);

        String hashedPassword = AuthUtils.hashPassword(newPassword);
        System.out.println("Hashed Password: " + hashedPassword);

        try (Connection conn = MaConnection.getConnection()) {
            // Update password in database
            String updateQuery = "UPDATE user SET motDePasse = ? WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, hashedPassword);
                stmt.setString(2, email);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Password updated successfully.");

                    if (userDatabase != null) {
                        userDatabase.put(email, hashedPassword);
                    }

                    resetTokens.remove(token);

                    showAlert(Alert.AlertType.INFORMATION, "Password Reset",
                            "Your password has been successfully reset. You can now log in with your new password.");

                    try (Connection conn2 = MaConnection.getConnection();
                         PreparedStatement stmtFetch = conn2.prepareStatement("SELECT * FROM user WHERE email = ?")) {
                        stmtFetch.setString(1, email);
                        ResultSet rs = stmtFetch.executeQuery();

                        if (rs.next()) {
                            User updatedUser = new User();
                            updatedUser.setEmail(rs.getString("email"));
                            updatedUser.setMotdepasse(rs.getString("motDePasse"));
                            updatedUser.setNom(rs.getString("nom"));
                            updatedUser.setPrenom(rs.getString("prenom"));
                            updatedUser.setNumerotelephone(rs.getString("numTelephone"));

                            // Handle potential null date safely
                            java.sql.Date birthDate = rs.getDate("dateNaissance");
                            if (birthDate != null) {
                                updatedUser.setDatedenaissance(birthDate.toLocalDate());
                            }

                            updatedUser.setStatut(rs.getString("statut"));
                            updatedUser.setRole(rs.getString("role"));

                            try {
                                int id = rs.getInt("id");
                                if (!rs.wasNull()) {
                                    updatedUser.setId(id);
                                }
                            } catch (SQLException e) {
                                System.out.println("Note: Could not set user ID - column may not exist: " + e.getMessage());
                            }

                            UserSession.getInstance().updateUserInfo(updatedUser);

                            if (profileController != null) {
                                profileController.setCurrentUser(updatedUser);
                            } else {
                                System.out.println("Profile controller not set");
                            }

                            final User finalUpdatedUser = updatedUser; 
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
                                    Parent root = loader.load();

                                    ProfileController controller = loader.getController();
                                    if (controller != null) {
                                        controller.setCurrentUser(finalUpdatedUser);
                                    }

                                    Stage currentStage = null;
                                    for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                                        if (window instanceof Stage && window.isShowing()) {
                                            currentStage = (Stage) window;
                                            break;
                                        }
                                    }

                                    if (currentStage != null) {
                                        currentStage.setScene(new Scene(root));
                                        currentStage.show();
                                    } else {
                                        Stage newStage = new Stage();
                                        newStage.setScene(new Scene(root));
                                        newStage.show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showAlert(Alert.AlertType.ERROR, "Navigation Error",
                                            "Failed to load profile screen.");
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("Failed to update password. No matching email found.");
                    showAlert(Alert.AlertType.ERROR, "Update Failed",
                            "Failed to update password. Please try again later.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to update password in the database: " + e.getMessage());
        }
    });
}

    private void showProgressDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void closeProgressDialog() {

    }
    //captchaaaaaaaaaaaaaaaaaaa
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField captchaInput;
    @FXML private Pane captchaContainer;
    @FXML private Button refreshCaptchaButton;
    @FXML private Button loginButton;
    @FXML
    private ImageView logoImageView;
    private Canvas captchaCanvas;
    private String captchaCode;
    @FXML
    public void initialize() {
        //image
        logoImageView.setImage(new Image(getClass().getResourceAsStream("/icons/travelpro_logo.png")));

        double radius = 55; // half of 110

        Circle clip = new Circle(radius, radius, radius);
        logoImageView.setClip(clip);

        // Optional: add a border circle for better visual feedback
        Circle border = new Circle(radius, radius, radius);
        border.setStroke(Color.BLACK); // border color
        border.setFill(Color.TRANSPARENT);
        border.setStrokeWidth(2);
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), border);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        ((StackPane) logoImageView.getParent()).getChildren().add(border);
        captchaCanvas = new Canvas(200, 80);
        captchaContainer.getChildren().add(captchaCanvas);

        generateCaptcha();
    }

    @FXML
    private void refreshCaptcha() {
        generateCaptcha();
    }
    private void generateCaptcha() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

        // Generate random code
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        captchaCode = sb.toString();

        GraphicsContext gc = captchaCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());

        gc.setFill(Color.rgb(240, 240, 240));
        gc.fillRect(0, 0, captchaCanvas.getWidth(), captchaCanvas.getHeight());

        // Noise lines
        for (int i = 0; i < 20; i++) {
            gc.setStroke(Color.rgb(random.nextInt(200), random.nextInt(200), random.nextInt(200), 0.5));
            gc.strokeLine(
                    random.nextDouble() * captchaCanvas.getWidth(),
                    random.nextDouble() * captchaCanvas.getHeight(),
                    random.nextDouble() * captchaCanvas.getWidth(),
                    random.nextDouble() * captchaCanvas.getHeight()
            );
        }

        // Draw text
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 30));
        double x=20;
        for (char c : captchaCode.toCharArray()) {
            gc.save();
            gc.translate(x, 40 + random.nextDouble() * 10);
            gc.rotate(random.nextDouble() * 30 - 15);
            gc.fillText(String.valueOf(c), 0, 0);
            gc.restore();
            x += 25 + random.nextDouble() * 10;
        }

        captchaInput.clear();
    }
    private boolean validateCaptcha() {
        return captchaInput.getText().equals(captchaCode);
    }


        private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //**********************************googlesign
    @FXML
    void signUpWithGoogle(ActionEvent event) {
        try {
            // Step 1: Authenticate with Google
            Credential credential = GoogleSignIn.authorize();

            if (credential != null) {
                // Step 2: Get user info from Google
                GoogleSignIn.GoogleUserInfo userInfo = GoogleSignIn.getUserInfo(credential);

                // Step 3: Check if user exists in your database
                UserServiceImpl userService = new UserServiceImpl(); // your service class
                User existingUser = userService.searchByEmail(userInfo.getEmail());

                if (existingUser != null) {
                    User loggedUser = existingUser != null ? existingUser : existingUser;
                    // Step 4A: If user exists, log them in
                    UserSession.getInstance().startSession(
                            existingUser.getId(),
                            existingUser.getNom(),
                            existingUser.getPrenom(),
                            existingUser.getRole()
                    );
                    // Also store the full User object for easy access later
                    UserSession.getInstance().setUserData("user", loggedUser);

                    this.currentUser = loggedUser;
                } else {
                    // Step 4B: If not, register a new user
                    String password = generateRandomPassword(); 
                    User newUser = new User(
                            userInfo.getGivenName(),      
                            userInfo.getFamilyName(),      
                            "[\"ROLE_CLIENT\"]",           
                            password,                     
                            userInfo.getEmail(),          
                            "",
                            new java.util.Date(),
                            "actif",
                            "NOT_BANNED",
                            true

                    );
                    newUser.setVerified(true);
                    userService.add(newUser);
                    User userr = userService.searchByEmail(newUser.getEmail());
                    UserSession.getInstance().startSession(
                            userr.getId(),
                            userr.getNom(),
                            userr.getPrenom(),
                            userr.getRole()
                             );
                }

                // Step 5: Redirect to dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("MENU");
                stage.setScene(new Scene(root));
                stage.show();

                ((javafx.scene.Node)(event.getSource())).getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Google Sign-In failed: " + e.getMessage());
        }
    }
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    }


