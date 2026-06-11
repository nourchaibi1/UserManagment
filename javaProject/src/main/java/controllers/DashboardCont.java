package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import models.User;

import service.UserServiceImpl;
import utils.MaConnection;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.scene.control.TableView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

public class DashboardCont {

    @FXML
    private TableColumn<User, String> telephoneCol;
    @FXML
    private TableColumn<User, String> dateNaissanceCol;
    @FXML
    private TableColumn<User, String> statutCol;
    @FXML
    private TableColumn<User, String> roleCol;
    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox navBar;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> nomCol;
    @FXML
    private TableColumn<User, String> prenomCol;
    @FXML
    private TableColumn<User, String> emailCol;
    @FXML
    private TableColumn<User, Void> modifierCol; // Column for the modify button
    @FXML
    private TableColumn<User, Void> supprimerCol; // Column for the delete button

    private UserServiceImpl userService = new UserServiceImpl();

    @FXML
    private VBox editForm; // VBox for editing user details form
    @FXML
    private TextField nomField, prenomField,
            emailField, telephoneField, dateNaissanceField,
            statutField, roleField;
    @FXML
    private TextField searchEmailField;
    private User selectedUser;
    @FXML
    private TableColumn<User, String> banUnbanCol;
    @FXML
    private BarChart<String, Number> registrationBarChart;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private TableColumn<User, String> colRegistrationDate;

    @FXML
    private NumberAxis yAxis;

    @FXML
    public void initialize() {
        //BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        initTableColumns();
        loadUserData();
        /* loadUsersFromDatabase();*/
        updateChartFromTableData();
        // Initialize columns and table data


        // Set custom cell factory for ban/unban column
        banUnbanCol.setCellFactory(col -> new TableCell<User, String>() {
            private final ComboBox<String> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll("Ban", "Unban");
                comboBox.setOnAction(event -> {
                    String selected = comboBox.getValue();
                    User user = getTableRow().getItem();
                    if (user != null) {
                        // Update the ban status in the model
                        user.setBanStatus(selected);
                        // Optionally, update the database or do additional actions here
                        updateBanStatusInDatabase(user);
                    }
                });
                // Apply chart styling after the chart is fully loaded using Platform.runLater
                Platform.runLater(() -> {
                    // Apply CSS styling to chart elements
                    registrationBarChart.setStyle("-fx-font-family: 'Arial'; -fx-background-color: transparent;");

                    // Style the legend
                    Node legend = registrationBarChart.lookup(".chart-legend");
                    if (legend != null) {
                        legend.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                    }

                    // Style the chart title
                    Node chartTitle = registrationBarChart.lookup(".chart-title");
                    if (chartTitle != null) {
                        chartTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                    }

                    // Style the X-axis (dates)
                    xAxis.setTickLabelFont(new javafx.scene.text.Font("Arial", 14));
                    xAxis.setTickLabelRotation(45);  // Rotate labels for better visibility
                    xAxis.setLabel("Registration Period");
                    xAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: black;");

                    // Style the Y-axis (counts)
                    yAxis.setTickLabelFont(new javafx.scene.text.Font("Arial", 12));
                    yAxis.setLabel("Number of Users");
                    yAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: black;");

                    // Style the bars
                    for (Node n : registrationBarChart.lookupAll(".chart-bar")) {
                        n.setStyle("-fx-bar-fill: #4682B4;");
                    }

                    // Force refresh of the chart
                    registrationBarChart.layout();
                });
            }

            @FXML
            private void handleCancelEdit() {
                editForm.setVisible(false);
                selectedUser = null;
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(comboBox);
                    comboBox.setValue(item); // Set the current status for Ban/Unban
                }
            }
        });

        // Set custom cell factories for buttons (Modifier and Supprimer)
        modifierCol.setCellFactory(getButtonCellFactory("Modifier"));
        supprimerCol.setCellFactory(getButtonCellFactory("Supprimer"));
    }

    private void updateBanStatusInDatabase(User user) {
        String sql = "UPDATE user SET banStatus = ? WHERE nom = ?";

        try (Connection conn = MaConnection.getConnection();  // Use your MaConnection class
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the parameters for the query
            stmt.setString(1, user.getBanStatus());  // Set the new ban status
            stmt.setString(2, user.getNom());  // Use the username (or other unique identifier)

            // Execute the update
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ban status updated successfully");
            } else {
                System.out.println("User not found in database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Initialize TableView columns
    private void initTableColumns() {
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        prenomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        telephoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumerotelephone()));
        dateNaissanceCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDatedenaissance().toString()));
        statutCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        colRegistrationDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRegistrationDate().toString()));
    }


    // Add buttons to the Table (for modify and delete)
    private Callback<TableColumn<User, Void>, TableCell<User, Void>> getButtonCellFactory(String buttonType) {
        return column -> new TableCell<User, Void>() {
            private final Button button = new Button(buttonType);

            {
                button.setOnAction(event -> {
                    // Get the selected user from the table
                    selectedUser = getTableView().getItems().get(getIndex());

                    if (selectedUser != null) {
                        if (buttonType.equals("Modifier")) {
                            handleModifierAction(selectedUser); // Handle modification
                        } else if (buttonType.equals("Supprimer")) {
                            handleDeleteAction(selectedUser); // Handle deletion
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        };
    }

    // Handle the action for modifying a user
    @FXML
    private void handleModifierAction(User user) {
        selectedUser = user;
        if (selectedUser != null) {
            // Fill the fields with the selected user's data
            nomField.setText(selectedUser.getNom());
            prenomField.setText(selectedUser.getPrenom());
            emailField.setText(selectedUser.getEmail());
            telephoneField.setText(selectedUser.getNumerotelephone());
            dateNaissanceField.setText(selectedUser.getDatedenaissance().toString());  // Adjust to your date format if needed
            statutField.setText(selectedUser.getStatut());
            roleField.setText(selectedUser.getRole());

            // Show the form for editing
            editForm.setVisible(true);
        } else {
            // Handle case where no user is selected
            System.out.println("No user selected");
        }
    }

    // Handle the action for deleting a user
    private void handleDeleteAction(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Voulez-vous vraiment supprimer cet utilisateur ?");
        alert.setContentText("Cette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userService.delete(user.getId());
            userTableView.getItems().remove(user);
        } else {
            System.out.println("Suppression annulée par l'utilisateur.");
        }
    }


    private void loadUserData() {
        // Reload the users from the database and update the ObservableList
        ObservableList<User> users = FXCollections.observableArrayList(userService.display());
        userTableView.setItems(users); // Update the TableView's items with the new list
    }

    // Handle the save action after editing a user
    @FXML
    private void handleSaveChanges() {
        if (selectedUser != null) {
            try {
                // Validate input fields
                if (nomField.getText().isEmpty() || prenomField.getText().isEmpty() ||
                        emailField.getText().isEmpty() || telephoneField.getText().isEmpty() ||
                        dateNaissanceField.getText().isEmpty() || statutField.getText().isEmpty() ||
                        roleField.getText().isEmpty()) {
                    showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                    return;
                }

                // Show confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Save Changes");
                alert.setContentText("Are you sure you want to save these changes?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Update user fields
                    selectedUser.setNom(nomField.getText());
                    selectedUser.setPrenom(prenomField.getText());
                    selectedUser.setEmail(emailField.getText());
                    selectedUser.setNumerotelephone(telephoneField.getText());

                    // Parse and set date
                    try {
                        LocalDate date = LocalDate.parse(dateNaissanceField.getText());
                        selectedUser.setDatedenaissance(date);
                    } catch (Exception e) {
                        showAlert("Error", "Invalid date format. Please use YYYY-MM-DD", Alert.AlertType.ERROR);
                        return;
                    }

                    selectedUser.setStatut(statutField.getText());
                    selectedUser.setRole(roleField.getText());

                    // Update in database
                    userService.modify(selectedUser);

                    // Refresh the table
                    loadUserData();
                    userTableView.refresh();

                    // Hide the edit form
                    editForm.setVisible(false);

                    // Show success message
                    showAlert("Success", "User information updated successfully", Alert.AlertType.INFORMATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update user information: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No user selected for editing", Alert.AlertType.ERROR);
        }
    }


    // Handle the cancel action (hide the edit form)
    @FXML
    private void handleCancelEdit() {
        editForm.setVisible(false);
    }


    @FXML
    private TextArea adminMessageTextArea;


    @FXML
    private ListView<String> messagesListView;

    // List to store messages
    private List<String> messages = new ArrayList<>();

    // Method to update ListView with the new messages

    public void updateMessages() {
        messagesListView.getItems().clear();  // Clear current list
        messagesListView.getItems().addAll(messages);  // Add all messages from the list
    }

    // Method to add a new message
    public void addMessage(String message) {
        messages.add(message);
        updateMessages();  // Update the UI with the new list of messages
    }

    @FXML
    private void handleSearchByEmail() {
        String email = searchEmailField.getText();

        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un email", Alert.AlertType.ERROR);
            return;
        }

        // Search the user by email
        User user = userService.searchByEmail(email);  // Assuming searchByEmail method exists in your service class

        if (user != null) {
            // Display the user details in the table
            userTableView.getItems().clear();  // Clear any previous data
            userTableView.getItems().add(user);
        } else {
            showAlert("Erreur", "Aucun utilisateur trouvé avec cet email", Alert.AlertType.ERROR);
        }

    }

    // Display alert messages
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    private void handleLogout() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpage.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) userTableView.getScene().getWindow();

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
    }

    @FXML
    private void handleHomeNavigation() {
        try {
            // Load the menu page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPage.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) rootPane.getScene().getWindow();

            // Create new scene and set it to the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load menu page", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            // Load the edit profile page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) rootPane.getScene().getWindow();

            // Create new scene and set it to the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load edit profile page", Alert.AlertType.ERROR);
        }
    }

    public void updateChartFromTableData() {
        try { registrationBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Users Registered");

        // Group the users by month-year of registration
        Map<String, Long> registrationsByMonth = userTableView.getItems().stream()
                .filter(user -> user.getRegistrationDate() != null)
                .collect(Collectors.groupingBy(
                        user -> {
                            LocalDate date = user.getRegistrationDate();
                            return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                    + " " + date.getYear();
                        },
                        Collectors.counting()
                ));

        // Add the data to the series
        registrationsByMonth.forEach((month, count) -> {
            series.getData().add(new XYChart.Data<>(month, count));
        });

        // Add the series to the chart
        registrationBarChart.getData().add(series);

        // Apply animations to the chart bars
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // Apply a fade transition
                    FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
                    ft.setFromValue(0);
                    ft.setToValue(1);

                    // Apply a scale transition for emphasis
                    ScaleTransition st = new ScaleTransition(Duration.millis(1000), node);
                    st.setFromX(0);
                    st.setFromY(0);
                    st.setToX(1);
                    st.setToY(1);

                    // Run both animations together
                    ParallelTransition pt = new ParallelTransition(ft, st);
                    pt.play();

                    // Add hover effect
                    node.setOnMouseEntered(e -> {
                        node.setStyle("-fx-bar-fill: #FF7F50;"); // Coral color on hover
                        node.setScaleX(1.05);
                        node.setScaleY(1.05);
                    });

                    node.setOnMouseExited(e -> {
                        node.setStyle("-fx-bar-fill: #4682B4;"); // Return to original color
                        node.setScaleX(1.0);
                        node.setScaleY(1.0);
                    });
                }
            }

            // Style the chart after data is loaded
            registrationBarChart.setStyle("-fx-font-family: 'Arial'; -fx-background-color: transparent;");

            // Make the month-year labels (x-axis) bigger and rotated for better visibility
            xAxis.setTickLabelFont(new javafx.scene.text.Font("Arial", 30));
            xAxis.setTickLabelRotation(45);

            // Add some padding to ensure labels don't get cut off
            registrationBarChart.setPadding(new javafx.geometry.Insets(10, 20, 50, 20));
        });

    } catch(Exception e)

    {
        System.err.println("Error updating chart: " + e.getMessage());
        e.printStackTrace();
    }
}

            /* registrationBarChart.getData().clear();

            Map<String, Long> registrationsByMonth = userTableView.getItems().stream()
                    .collect(Collectors.groupingBy(
                            user -> {
                                LocalDate date = user.getRegistrationDate();
                                return date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + date.getYear();
                            },
                            Collectors.counting()
                    ));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Users Registered");

            registrationsByMonth.forEach((month, count) -> {
                series.getData().add(new XYChart.Data<>(month, count));
            });

            registrationBarChart.getData().add(series);

            // Add animation and value labels
            for (XYChart.Data<String, Number> data : series.getData()) {
                Platform.runLater(() -> {
                    Node node = data.getNode();
                    if (node != null) {
                        FadeTransition ft = new FadeTransition(Duration.millis(700), node);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        ft.play();

                        Text valueText = new Text(data.getYValue().toString());
                        valueText.setStyle("-fx-font-size: 16px; -fx-fill: black; -fx-font-weight: bold;");
                        StackPane stackPane = (StackPane) node;
                        stackPane.getChildren().add(valueText);
                        StackPane.setAlignment(valueText, Pos.TOP_CENTER);
                    }
                });
            }

            // Apply axis and legend styling
            Platform.runLater(() -> {
                Node legend = registrationBarChart.lookup(".chart-legend");
                if (legend != null)
                    legend.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");

                Node xLabel = registrationBarChart.getXAxis().lookup(".axis-label");
                if (xLabel != null)
                    xLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

                Node yLabel = registrationBarChart.getYAxis().lookup(".axis-label");
                if (yLabel != null)
                    yLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");

                registrationBarChart.getXAxis().lookupAll(".axis-tick-label").forEach(node ->
                        node.setStyle("-fx-font-size: 8px; -fx-text-fill: black;")
                );
                registrationBarChart.getYAxis().lookupAll(".axis-tick-label").forEach(node ->
                        node.setStyle("-fx-font-size: 16px; -fx-text-fill: black;")
                );
            });
        */

   /* private void loadUsersFromDatabase() {
        ObservableList<User> users = FXCollections.observableArrayList();

        String query = "SELECT nom, registration_da FROM user";

        try (Connection conn = MaConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("nom");
                LocalDate registrationDate = rs.getDate("registration_da").toLocalDate();
                users.add(new User(name, registrationDate));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        userTableView.setItems(users);
    }*/


}


