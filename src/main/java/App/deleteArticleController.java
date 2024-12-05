package App;

import Model.Article;
import Model.Admin;
import Database.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.List;

public class deleteArticleController {

    @FXML
    private ListView<String> articlesListView;

    private Admin admin; // Store the admin object
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    // Initialize the controller with the admin object
    public void initialize(Admin admin) {
        this.admin = admin;
        loadArticles();
    }

    // Load articles from the database
    private void loadArticles() {
        List<Article> articles = dbHandler.getAllArticles();
        articlesListView.getItems().clear();
        for (Article article : articles) {
            articlesListView.getItems().add(article.getTitle() + " - ID: " + article.getId());
        }
    }

    // Handle the delete article action
    @FXML
    private void handleDeleteArticle() {
        String selectedItem = articlesListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String[] parts = selectedItem.split(" - ID: ");
            if (parts.length == 2) {
                int articleId = Integer.parseInt(parts[1]);
                admin.deleteArticle(articleId, dbHandler); // Call the delete method from the Admin class
                loadArticles(); // Refresh the list view
                showAlert(Alert.AlertType.INFORMATION, "Article Deleted", "The article has been successfully deleted.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Article Selected", "Please select an article to delete.");
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
