package App;

import Model.Article;
import Model.User;
import Service.RecommendationService;
import Service.MatrixFactorizationModel;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

public class PersonalizedArticlesController {

    @FXML
    private ListView<String> articlesListView;

    private User user;
    private final RecommendationService recommendationService = new RecommendationService();

    // Initialize the controller with the current user
    public void initUser(User user) {
        this.user = user;
    }

    // Load personalized articles for the user
    @FXML
    private void loadPersonalizedArticles() {
        // Train the model and get recommendations
        MatrixFactorizationModel model = recommendationService.trainModel(user);
        List<Article> recommendations = recommendationService.getRecommendations(user, model);

        // Check if recommendations are available
        if (recommendations.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("No Recommendations");
            alert.setHeaderText(null);
            alert.setContentText("No personalized articles available at the moment.");
            alert.showAndWait();
            return;
        }

        // Display the recommended articles
        articlesListView.getItems().clear();
        for (Article article : recommendations) {
            articlesListView.getItems().add(article.getTitle());
        }
    }
}
