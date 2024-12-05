package App;

import Model.User;
import Model.Admin;
import Database.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.List;

public class deleteUserController {

    @FXML
    private ListView<String> usersListView;

    private Admin admin; // Store the admin object
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    // Initialize the controller with the admin object
    public void initialize(Admin admin) {
        this.admin = admin;
        loadUsers();
    }

    // Load users from the database
    private void loadUsers() {
        List<User> users = dbHandler.getAllUsers();
        usersListView.getItems().clear();
        for (User user : users) {
            usersListView.getItems().add(user.getUserName() + " - ID: " + user.getUserID());
        }
    }

    // Handle the delete user action
    @FXML
    private void handleDeleteUser() {
        String selectedItem = usersListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] parts = selectedItem.split(" - ID: ");
            if (parts.length == 2) {
                int userId = Integer.parseInt(parts[1]);
                admin.deleteUser(userId, dbHandler); // Call the delete method from the Admin class
                loadUsers(); // Refresh the list view
                showAlert(Alert.AlertType.INFORMATION, "User Deleted", "The user has been successfully deleted.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Please select a user to delete.");
        }
    }

    // Method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
