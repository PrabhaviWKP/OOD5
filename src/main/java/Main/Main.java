package Main;

import Database.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Service.ArticleService;
import Model.Article;

import javafx.application.Platform;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static Stage primaryStage;

    // Instantiate ArticleService to fetch articles
    private ArticleService articleService = new ArticleService();
    private DatabaseHandler dbHandler = new DatabaseHandler();

    // Scheduler for periodic fetching
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Check if articles need to be fetched
        LocalDateTime lastFetchTime = dbHandler.getLastFetchTime();
        if (lastFetchTime == null || ChronoUnit.HOURS.between(lastFetchTime, LocalDateTime.now()) >= 24) {
            // Fetch articles in a separate thread to avoid blocking UI
            fetchArticlesAsync();
        } else {
            System.out.println("Using saved articles in the database");
        }

        // Schedule fetchArticlesAsync to run every 24 hours
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchArticlesAsync, 24, 24, TimeUnit.HOURS);

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
                    dbHandler.insertArticle(article);
                });

                // Update last fetch time
                dbHandler.storeLastFetchTime(LocalDateTime.now());
            });
        }).start();
    }
}
