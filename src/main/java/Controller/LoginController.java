package Controller;

import Service.userService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private userService userService = new userService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = this.username.getText();
        String password = this.password.getText();



        if(userService.validateCredentials(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, " + username + "!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password");
        }
    }

    private void clearForm() {
        username.clear();
        password.clear();

    }

}
