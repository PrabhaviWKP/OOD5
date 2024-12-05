package App;

import Model.Admin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class adminDashboardController {

    @FXML
    private Label lblWelcomeMessage;

    private Admin admin; // Store the admin object

    // This method is called when the dashboard is initialized
    public void initialize(String userName, Admin admin) {
        this.admin = admin;
        lblWelcomeMessage.setText("Welcome, " + userName + "!");
    }

    // Handle the show delete article action
    @FXML
    private void handleShowDeleteArticle() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/deleteArticle.fxml"));
        Parent root = loader.load();

        deleteArticleController controller = loader.getController();
        controller.initialize(admin); // Pass the admin object to the delete article controller

        Stage stage = new Stage();
        stage.setTitle("Delete Article");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Handle the show delete user action
    @FXML
    private void handleShowDeleteUser() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/deleteUser.fxml"));
        Parent root = loader.load();

        deleteUserController controller = loader.getController();
        controller.initialize(admin); // Pass the admin object to the delete user controller

        Stage stage = new Stage();
        stage.setTitle("Delete User");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
