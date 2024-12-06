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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static Stage primaryStage;

    private ArticleService articleService = new ArticleService();
    private DatabaseHandler dbHandler = new DatabaseHandler();

    private ScheduledExecutorService scheduler;
    private Process flaskProcess;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Start the Flask server
        startFlaskServer();

        // Check if articles need to be fetched
        LocalDateTime lastFetchTime = dbHandler.getLastFetchTime();

        if (lastFetchTime == null) {
            System.out.println("No fetch history found. Fetching articles...");
            fetchArticlesAsync(); // First-time fetch
        } else {
            long hoursSinceLastFetch = ChronoUnit.HOURS.between(lastFetchTime, LocalDateTime.now());
            System.out.println("Last fetch was " + hoursSinceLastFetch + " hours ago.");

            if (hoursSinceLastFetch < 6) {
                System.out.println("Using saved articles in the database (within 6 hours).");
            } else {
                System.out.println("Last fetch was more than 6 hours ago. Fetching new articles...");
                fetchArticlesAsync();
            }
        }

        // Schedule fetchArticlesAsync to run every 6 hours
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::fetchArticlesAsync, 6, 6, TimeUnit.HOURS);

        loadMainMenu(); // Load the main menu
    }

    public static void main(String[] args) {
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        launch(args);
    }

    public static void loadMainMenu() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/App/MainMenu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void loadLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/App/Login.fxml"));
        primaryStage.setTitle("User Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void loadRegisterScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/App/register.fxml"));
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    private void fetchArticlesAsync() {
        new Thread(() -> {
            System.out.println("Fetching articles from API...");
            List<Article> articles = articleService.fetchArticlesFromAPI(); // Fetch articles from API

            // Log articles
            articles.forEach(article -> {
                if (!dbHandler.articleExists(article.getUrl())) {
                    Platform.runLater(() -> {
                        System.out.println("Article inserted: " + article.getTitle());
                        dbHandler.insertArticle(article);
                    });
                } else {
                    System.out.println("Duplicate article found, skipping: " + article.getUrl());
                }
            });

            // Update last fetch time after all articles have been processed
            Platform.runLater(() -> {
                System.out.println("Updated last fetch time to: " + LocalDateTime.now());
                dbHandler.storeLastFetchTime(LocalDateTime.now());
            });
        }).start();
    }


    // Start the Flask server
    private void startFlaskServer() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "C:\\Users\\prabh\\IdeaProjects\\OOD5\\python\\app.py");
            pb.redirectErrorStream(true);
            flaskProcess = pb.start();
            System.out.println("Flask server started successfully.");

            // Delay to ensure Flask server is fully ready
            Thread.sleep(7000); // Wait for 5 seconds
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to start Flask server: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Shut down the Flask server when the JavaFX application closes
        if (flaskProcess != null && flaskProcess.isAlive()) {
            flaskProcess.destroy();
            System.out.println("Flask server stopped.");
        }

        // Shut down the scheduler
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
