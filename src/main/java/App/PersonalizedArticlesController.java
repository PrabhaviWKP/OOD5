package App;

import Model.Article;
import Model.User;
import Service.RecommendationService;
import Service.ArticleService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import net.librec.recommender.item.RecommendedItem;

import java.util.List;

public class PersonalizedArticlesController {

    @FXML
    private ListView<String> articlesListView;


    private User user;
    private final ArticleService articleService = new ArticleService();
    private final RecommendationService recommendationService = new RecommendationService();

    // Initialize the controller with the current user
    public void initUser(User user) {
        this.user = user;
    }

    // Load personalized articles for the user
    @FXML
    private void loadPersonalizedArticles() {
        try {
            // Fetch recommendations using LibRec
            List<RecommendedItem> recommendations = recommendationService.getRecommendations(user.getUserID());

            // Debug statement to check recommendations
            System.out.println("Recommendations fetched: " + recommendations.size());

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
            for (RecommendedItem recommendedItem : recommendations) {
                int articleId = Integer.parseInt(recommendedItem.getItemId());
                Article article = articleService.getAllArticles().stream()
                        .filter(a -> a.getId() == articleId)
                        .findFirst()
                        .orElse(null);

                if (article != null) {
                    articlesListView.getItems().add(article.getTitle());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while loading personalized articles.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }


}
