package App;

import Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class userManagementController {

    private User user; // Store the user object

    // Initialize the controller with the current user
    public void initialize(User user) {
        this.user = user;
    }

    // Handle the edit preferences action
    @FXML
    private void handleEditPreferences() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/editPreferences.fxml"));
        Parent root = loader.load();

        editPreferencesController controller = loader.getController();
        controller.initialize(user); // Pass the user object to the edit preferences controller

        Stage stage = new Stage();
        stage.setTitle("Edit Preferences");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Handle the edit password action
    @FXML
    private void handleEditPassword() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/editPassword.fxml"));
        Parent root = loader.load();

        editPasswordController controller = loader.getController();
        controller.initialize(user); // Pass the user object to the edit password controller

        Stage stage = new Stage();
        stage.setTitle("Edit Password");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
