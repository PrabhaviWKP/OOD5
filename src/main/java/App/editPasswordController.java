package App;

import Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class editPasswordController {

    @FXML
    private PasswordField txtPassword;

    private User user; // Store the user object

    // Initialize the controller with the current user
    public void initialize(User user) {
        this.user = user;
    }

    // Handle the save password action
    @FXML
    private void handleSavePassword() {
        String newPassword = txtPassword.getText();
        try {
            user.updatePassword(newPassword);
            showAlert(Alert.AlertType.INFORMATION, "Password Saved", "Your password has been successfully saved.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Password", e.getMessage());
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
