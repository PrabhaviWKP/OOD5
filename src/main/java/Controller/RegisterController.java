package Controller;

import Model.User;
import Service.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static Model.User.isValidName;

public class RegisterController extends BaseController {

    @FXML
    private TextField txtUserID;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;

    private userService userService = new userService();

    @FXML
    public void initialize() {
        int nextUserId = userService.getNextUserId();
        txtUserID.setText(String.valueOf(nextUserId));
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String userName = txtUsername.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (userName.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill all the fields.");
            return;
        }
        if (!isValidName(firstName) || !isValidName(lastName)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Names must contain only letters.");
            return;
        }

        if (!User.isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        if (userService.isUsernameTaken(userName)) {
            showAlert(Alert.AlertType.ERROR, "Username Taken", "Username is taken.");
            return;
        }

        User newUser = new User(userService.getNextUserId(), userName, firstName, lastName, password);

        if (userService.registerUser(newUser)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Welcome " + userName + "!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Something went wrong!");
        }
    }

    private void clearForm() {
        txtUserID.clear();
        txtUsername.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
    }
}
