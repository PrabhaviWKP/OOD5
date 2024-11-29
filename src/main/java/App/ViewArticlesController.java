package App;

import Model.Article;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import Service.ArticleService;

public class ViewArticlesController {

    @FXML
    private TableView<Article> articlesTable;

    @FXML
    private TableColumn<Article, String> titleColumn;

    @FXML
    private TableColumn<Article, String> sourceColumn;

    @FXML
    private TableColumn<Article, String> urlColumn;

    @FXML
    private TableColumn<Article, String> contentColumn;

    @FXML
    private TableColumn<Article, String> categoryColumn;

    @FXML
    private TableColumn<Article, String> publicationDateColumn;

    private ArticleService articleService = new ArticleService();

    @FXML
    public void initialize() {
        // Set up the table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        publicationDateColumn.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));

        // Fetch and display articles
        ObservableList<Article> articles = FXCollections.observableArrayList(articleService.getAllArticles());
        articlesTable.setItems(articles);
    }
}