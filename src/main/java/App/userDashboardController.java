package App;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class userDashboardController {

    @FXML
    private Label lblWelcomeMessage;

    private String userName;
    private int userId;

    // This method is called when the dashboard is initialized
    public void initialize(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        lblWelcomeMessage.setText("Welcome, " + userName + "!");
    }

    // Handle the "View Articles" button
    @FXML
    private void handleViewArticles() {
        // Logic to go to the "View Articles" screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ViewArticles.fxml"));
            Parent root = loader.load();
            ViewArticlesController controller = loader.getController();
            controller.initialize(userId); // Pass the user ID to the ViewArticlesController
            Stage stage = new Stage();
            stage.setTitle("View Articles");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle the "Edit Preferences" button
    @FXML
    private void handleEditPreferences() {
        // Logic to go to the "Edit Preferences" screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/your/EditPreferences.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Edit Preferences");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handle the "Log Out" button
    @FXML
    private void handleLogOut() {
        // Logic to log out the user and return to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/your/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblWelcomeMessage.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
