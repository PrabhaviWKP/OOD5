package App;

import Model.Article;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import Service.ArticleService;
import javafx.stage.Stage;

public class ViewArticlesController {

    @FXML
    private ListView<Article> articlesList;

    @FXML
    private TextArea articleContent;

    private ArticleService articleService = new ArticleService();

    @FXML
    public void initialize() {
        // Fetch and display articles
        ObservableList<Article> articles = FXCollections.observableArrayList(articleService.getAllArticles());
        articlesList.setItems(articles);

        // Set cell factory to display title and source
        articlesList.setCellFactory(param -> new javafx.scene.control.ListCell<Article>() {
            @Override
            protected void updateItem(Article item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " - " + item.getSource());
                }
            }
        });

        // Handle selection change to navigate to article content page
        articlesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showArticleContent(newValue);
            }
        });
    }

    private void showArticleContent(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/ArticleContent.fxml"));
            Parent root = loader.load();
            ArticleContentController controller = loader.getController();
            controller.initialize(article);

            Stage stage = new Stage();
            stage.setTitle("Article Content");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
