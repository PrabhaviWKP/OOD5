package App;

import Model.User;
import Model.Admin;
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
            // Successful login, load the appropriate dashboard
            try {
                int userId = userService.getUserIdByUsername(userName); // Get the user ID
                String userType = userService.getUserTypeByUsername(userName); // Get the user type

                if ("admin".equals(userType)) {
                    Admin admin = new Admin(userId, userName, "", "", userPassword); // Create an Admin object
                    loadAdminDashboard(admin);
                } else {
                    User user = new User(userId, userName, "", "", userPassword, ""); // Create a User object
                    loadUserDashboard(user);
                }
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

    private void loadUserDashboard(User user) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/userDashboard.fxml"));
        Parent root = loader.load();

        userDashboardController dashboardController = loader.getController();
        dashboardController.initialize(user.getUserName(), user); // Pass the user name and user object to the dashboard controller

        Stage stage = (Stage) username.getScene().getWindow();
        stage.setScene(new Scene(root));
    }

    private void loadAdminDashboard(Admin admin) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/adminDashboard.fxml"));
        Parent root = loader.load();

        adminDashboardController dashboardController = loader.getController();
        dashboardController.initialize(admin.getUserName(), admin); // Pass the user name and admin object to the dashboard controller

        Stage stage = (Stage) username.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
