package App;

import Model.Article;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ArticleContentController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label sourceLabel;

    @FXML
    private TextArea contentTextArea;

    private Article article;

    public void initialize(Article article) {
        this.article = article;
        titleLabel.setText(article.getTitle());
        sourceLabel.setText(article.getSource());
        contentTextArea.setText(article.getContent());
    }
}
