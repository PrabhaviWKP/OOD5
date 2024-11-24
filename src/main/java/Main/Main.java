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
        if (lastFetchTime == null || ChronoUnit.HOURS.between(lastFetchTime, LocalDateTime.now()) >= 6) {
            fetchArticlesAsync(); // Fetch articles in a separate thread
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

    public static void loadLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("User Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void loadRegisterScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/register.fxml"));
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void fetchArticlesAsync() {
        new Thread(() -> {
            List<Article> articles = articleService.fetchArticlesFromAPI(); // Fetch articles from API

            Platform.runLater(() -> {
                System.out.println("Articles fetched: " + articles.size());

                // Store articles in the database
                articles.forEach(article -> dbHandler.insertArticle(article));

                // Update last fetch time
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
            Thread.sleep(10000); // Wait for 5 seconds
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
