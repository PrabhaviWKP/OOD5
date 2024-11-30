package App;

import Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import Service.userService;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private userService userService = new userService();


    // Handle login logic
    @FXML
    private void handleLogin() {
        String userName = username.getText();
        String userPassword = password.getText();

        if (userService.validateCredentials(userName, userPassword)) {
            // Successful login, load the dashboard
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/userDashboard.fxml"));
                Parent root = loader.load();

                userDashboardController dashboardController = loader.getController();
                int userId = userService.getUserIdByUsername(userName);
                dashboardController.initialize(userName, userId); // Pass the username to the dashboard controller

                Stage stage = (Stage) username.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Show error message if login fails
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid credentials");
            alert.setContentText("Please check your username and password.");
            alert.showAndWait();
        }
    }
}
