package App;

import Model.Article;
import Model.User;
import Service.userService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import Database.DatabaseHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ArticleContentController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label sourceLabel;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private Button likeButton;

    @FXML
    private Button skipButton;

    private Article article;
    private User user; // Declare the user variable
    private DatabaseHandler dbHandler = new DatabaseHandler();
    private userService userService = new userService();

    public void initialize(Article article, User user) {
        this.article = article;
        this.user = user; // Initialize the user variable
        titleLabel.setText(article.getTitle());
        sourceLabel.setText(article.getSource());
        contentTextArea.setText(article.getContent());

        // Check if the article is already liked
        if (userService.isArticleLiked(user.getUserID(), article.getId(), dbHandler)) {
            likeButton.setText("Liked");
            likeButton.setDisable(true);
            skipButton.setDisable(true); // Disable the skip button if the article is already liked
        }
    }

    @FXML
    private void handleLike() {
        // Check if the article is already liked
        if (!userService.isArticleLiked(user.getUserID(), article.getId(), dbHandler)) {
            user.likeArticle(article.getId(), dbHandler);
            userService.recordViewedArticle(user.getUserID(), article.getId(), dbHandler);
            likeButton.setText("Liked");
            likeButton.setDisable(true);
            skipButton.setDisable(true); // Disable the skip button if the article is liked
        }
    }

    @FXML
    private void handleSkip() {
        // Update the user's preferences to reflect that the article has been skipped
        user.skipArticle(article.getId(), dbHandler);
        // Navigate back to the article list without creating a new view article window
        navigateToArticleList();
    }

    private void navigateToArticleList() {
        // Close the current window to return to the previous window
        Stage stage = (Stage) skipButton.getScene().getWindow();
        stage.close();
    }
}
