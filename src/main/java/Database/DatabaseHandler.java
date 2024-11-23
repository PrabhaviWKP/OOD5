package Database;

import Model.Article;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DatabaseHandler {
    private final DatabaseConnection databaseConnection;

    public DatabaseHandler(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // No-argument constructor
    public DatabaseHandler() {
        this.databaseConnection = new DatabaseConnection();
    }

    // Insert a new article and classify it
    public void insertArticle(Article article) {
        if (!articleExists(article.getUrl())) {
            // Classify the article before saving
            try {
                String category = classifyArticle(article.getContent());
                article.setCategory(category); // Set the category for the article
            } catch (Exception e) {
                e.printStackTrace();
                return; // If classification fails, skip saving the article
            }

            String sql = "INSERT INTO articles (title, source, url, content, category, publicationDate) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, article.getTitle());
                statement.setString(2, article.getSource());
                statement.setString(3, article.getUrl());
                statement.setString(4, article.getContent());
                statement.setString(5, article.getCategory());
                statement.setDate(6, new java.sql.Date(article.getPublicationDate().getTime()));

                statement.executeUpdate();
                System.out.println("Article inserted: " + article.getTitle());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Duplicate article found, skipping: " + article.getUrl());
        }
    }

    // Check if an article already exists based on URL
    public boolean articleExists(String url) {
        String sql = "SELECT COUNT(*) FROM articles WHERE url = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, url);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Classify article content using the Flask API
    private String classifyArticle(String articleContent) throws Exception {
        // Prepare the HTTP request to Flask API
        String apiUrl = "http://localhost:5000/classify"; // Flask API URL
        Map<String, String> requestData = new HashMap<>();
        requestData.put("text", articleContent);
        String json = new Gson().toJson(requestData);

        // Create HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response
        String responseBody = response.body();
        Gson gson = new Gson();
        Map<String, String> responseMap = gson.fromJson(responseBody, Map.class);
        return responseMap.getOrDefault("category","Unknown"); // Return the category from the response
    }

    // Store the last fetch time in the database
    public void storeLastFetchTime(LocalDateTime lastFetchTime) {
        String sql = "INSERT INTO fetch_times (last_fetch_time) VALUES (?) ON DUPLICATE KEY UPDATE last_fetch_time = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setTimestamp(1, java.sql.Timestamp.valueOf(lastFetchTime));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(lastFetchTime));

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve the last fetch time from the database
    public LocalDateTime getLastFetchTime() {
        String sql = "SELECT last_fetch_time FROM fetch_times LIMIT 1";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("last_fetch_time").toLocalDateTime();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
