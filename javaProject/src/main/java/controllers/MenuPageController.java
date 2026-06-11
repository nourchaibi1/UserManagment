package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.User;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.Node;
import models.UserSession;

import java.io.IOException;
import java.net.URL;
import javafx.scene.control.Label;
import service.UserServiceImpl;

public class MenuPageController {

    @FXML
    private Button logoutButton;  // Logout button fx:id

    @FXML
    private BorderPane rootPane;  // BorderPane fx:id in FXML

    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button settingsButton;
    @FXML private Button dashboardButton;
    @FXML private HBox navBar;
    @FXML private VBox mainContent;

    private String userRole;
     // Store the current user object

    // Set the user role (e.g., Admin)
    public void setUserRole(String role) {
        this.userRole = role;

        if (dashboardButton != null) {
            boolean isAdmin = "admin".equalsIgnoreCase(userRole);
            dashboardButton.setVisible(isAdmin);
            dashboardButton.setManaged(isAdmin); // Hides the space too
        } else {
            System.out.println("dashboardButton is null");
        }
    }


    // Method to set the user object from another controller (LoginController)
    public void setUser(User user) {
        this.currentUser = user;
        // Optionally, update UI or perform other actions based on the user
        System.out.println("User set: " + currentUser.getNom()); // For debugging
    }

    private void loadDashboard() {
        try {
            System.out.println("Loading dashboard...");

            // 1. Verify resource exists (debug)
            URL url = getClass().getResource("/dashboard.fxml");
            if (url == null) {
                throw new IOException("Cannot find /dashboard.fxml in resources");
            }

            // 2. Load with explicit path
            FXMLLoader loader = new FXMLLoader(url);
            Parent dashboardView = loader.load();

            // 3. Get controller (optional)
            DashboardCont controller = loader.getController();

            // 4. Set view
            rootPane.setCenter(dashboardView);
            System.out.println("Dashboard loaded successfully!");

        } catch (IOException e) {
            System.err.println("Failed to load dashboard:");
            e.printStackTrace();

            // Visual error feedback
            Label errorLabel = new Label("Dashboard Error:\n" + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
            rootPane.setCenter(errorLabel);
        }
    }

    // Handle the Logout action
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Logout Failed");
            alert.setContentText("Could not return to login page. Please try again.");
            alert.showAndWait();
        }
    }
   /* @FXML
    private void handleLogout() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) mainContent.getScene().getWindow();
            
            // Set the new scene
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
    }*/
   private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    // Handle the Profile menu item
    @FXML
    private void handleProfile() {
        loadProfile();
    }

    // Method to load the Profile FXML into the center of the BorderPane
    private void loadProfile() {
        try {
            System.out.println("Attempting to load profile page...");
            System.out.println("Current user: " + (currentUser != null ? currentUser.getNom() : "null"));
            
            URL url = getClass().getResource("/profile.fxml");
            if (url == null) {
                System.err.println("Cannot find profile.fxml in resources!");
                showAlert("Error", "Profile page not found");
                return;
            }
            System.out.println("Found profile.fxml at: " + url);
            
            FXMLLoader loader = new FXMLLoader(url);
            Parent profileView = loader.load();
            System.out.println("Successfully loaded profile.fxml");

            // Get the controller for Profile page
            ProfileController controller = loader.getController();
            System.out.println("Got ProfileController: " + (controller != null ? "OK" : "NULL"));
            User currentUser = (User) UserSession.getInstance().getUserData("user");

            // Pass the current user to ProfileController
            controller.setCurrentUser(currentUser);
            System.out.println("Set current user in ProfileController");

            // Load the Profile view into the center of the BorderPane
            rootPane.setCenter(profileView);
            System.out.println("Set profile view in rootPane");

        } catch (IOException e) {
            System.err.println("Failed to load profile page:");
            e.printStackTrace();
            showAlert("Error", "Failed to load profile page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML private Label welcomeLabel;
    @FXML private Label userRoleLabel;
    @FXML  private Label  lastLoginLabel;
    //instencae
    UserSession session = UserSession.getInstance();
    @FXML
    public void initialize() {
            System.out.println("Controller initialized!");
            System.out.println("NavBar: " + (navBar != null ? "OK" : "NULL"));
            System.out.println("RootPane: " + (rootPane != null ? "OK" : "NULL"));

            UserSession session = UserSession.getInstance();
            System.out.println("Username from session: " + session.getUsername());
            System.out.println("Is logged in: " + session.isLoggedIn());

            if (session.isLoggedIn()) {
                welcomeLabel.setText(session.getUserlastname() + "\n" + session.getUsername());

                String role = session.getUserRole();
                System.out.println("Role from session: " + role);
                // You need a UserService to fetch the full user object
                UserServiceImpl userService = new UserServiceImpl();
                currentUser = userService.searchByEmail(session.getUsername());

                if (currentUser == null) {
                    System.err.println("Failed to retrieve user data for: " + session.getUsername());
                } else {
                    System.out.println("Current user set to: " + currentUser.getNom());
                }
                if (dashboardButton != null) {
                    dashboardButton.setVisible("admin".equalsIgnoreCase(role));
                }
            } else {
                welcomeLabel.setText("Please log in.");
                if (dashboardButton != null) {
                    dashboardButton.setVisible(false);
                }
            }

            if (mainContent != null) {
                playEntranceAnimation(mainContent);
            }
        }


        /*System.out.println("Controller initialized!");
        System.out.println("NavBar: " + (navBar != null ? "OK" : "NULL"));
        System.out.println("RootPane: " + (rootPane != null ? "OK" : "NULL"));
        if (dashboardButton != null) {
            dashboardButton.setVisible("admin".equalsIgnoreCase(userRole));
        }
        // Play entrance animation for main content
        if (mainContent != null) {
            playEntranceAnimation(mainContent);
        }
        // Retrieve data from the session and set text to labels
        UserSession session = UserSession.getInstance();
        System.out.println("Username from session: " + session.getUsername());
        System.out.println("Is logged in: " + session.isLoggedIn());

        if (session.isLoggedIn()) {
            welcomeLabel.setText( session.getUserlastname()  +"\n "+ session.getUsername() );
            //userRoleLabel.setText("Role: " + session.getUserData("role"));
            //String lastLogin = session.getFormattedLoginTime();
            // Display the login start time

            //lastLoginLabel.setText("Start time: " + (lastLogin != null ? lastLogin : "N/A"));
        } else {
            welcomeLabel.setText("Please log in.");
            //userRoleLabel.setText("Role: N/A");
            //lastLoginLabel.setText("Start time: N/A");
        }
    }
*/


    @FXML
    private void handleHome() {
        // Implement navigation to home or main content
        System.out.println("Home button clicked");
        // Example: rootPane.setCenter(new Label("Welcome Home!"));
    }

    @FXML
    private void handleSettings() {

    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Switch the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playEntranceAnimation(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), node);
        scaleIn.setFromX(0.95);
        scaleIn.setFromY(0.95);
        scaleIn.setToX(1);
        scaleIn.setToY(1);

        TranslateTransition translateIn = new TranslateTransition(Duration.millis(800), node);
        translateIn.setFromY(10);
        translateIn.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn, translateIn);
        parallelTransition.play();
    }



}

