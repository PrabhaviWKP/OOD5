package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Service.ArticleService;
import Model.Article;

import javafx.application.Platform;

import java.util.List;

public class Main extends Application {

    private static Stage primaryStage;

    // Instantiate ArticleService to fetch articles
    private ArticleService articleService = new ArticleService();

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Fetch articles in a separate thread to avoid blocking UI
        fetchArticlesAsync();

        loadMainMenu(); // Load the main menu
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadMainMenu() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/MainMenu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to load the login screen
    public static void loadLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("User Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to load the registration screen
    public static void loadRegisterScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/register.fxml"));
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to fetch articles asynchronously
    private void fetchArticlesAsync() {
        new Thread(() -> {
            List<Article> articles = articleService.fetchArticlesFromAPI(); // Fetch articles from API

            // Now that we have the articles, you can update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                // Do something with the articles (e.g., store in DB, update UI)
                System.out.println("Articles fetched: " + articles.size());

                // Optionally, store articles in the database
                articles.forEach(article -> {
                    // Assuming you have a DatabaseHandler class for storing articles
                    Database.DatabaseHandler dbHandler = new Database.DatabaseHandler(new Database.DatabaseConnection());
                    dbHandler.insertArticle(article);
                });
            });
        }).start();
    }
}
