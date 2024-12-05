package Database;

import Model.Article;
import Model.User;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class DatabaseHandler {
    private final DatabaseConnection databaseConnection;
    private final ExecutorService executorService;

    public DatabaseHandler(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed
    }

    // No-argument constructor
    public DatabaseHandler() {
        this.databaseConnection = new DatabaseConnection();
        this.executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed
    }

    // Insert a new article and classify it
    public void insertArticle(Article article) {
        executorService.submit(() -> {
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
        });
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
        return responseMap.getOrDefault("category", "Unknown"); // Return the category from the response
    }

    // Store the last fetch time in the database
    public void storeLastFetchTime(LocalDateTime lastFetchTime) {
        executorService.submit(() -> {
            String sql = "INSERT INTO fetch_times (last_fetch_time) VALUES (?) ON DUPLICATE KEY UPDATE last_fetch_time = ?";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setTimestamp(1, java.sql.Timestamp.valueOf(lastFetchTime));
                statement.setTimestamp(2, java.sql.Timestamp.valueOf(lastFetchTime));

                statement.executeUpdate();
                System.out.println("Updated last fetch time to: " + lastFetchTime);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Retrieve the last fetch time from the database
    public LocalDateTime getLastFetchTime() {
        String sql = "SELECT last_fetch_time FROM fetch_times ORDER BY last_fetch_time DESC LIMIT 1";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                LocalDateTime lastFetch = resultSet.getTimestamp("last_fetch_time").toLocalDateTime();
                System.out.println("Retrieved last fetch time: " + lastFetch);
                return lastFetch;
            } else {
                System.out.println("No last fetch time found in the database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No fetch time found
    }

    // Retrieve all articles from the database
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT id, title, source, url, content, category, publicationDate FROM articles";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                articles.add(new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("source"),
                        resultSet.getString("url"),
                        resultSet.getString("content"),
                        resultSet.getString("category"),
                        resultSet.getString("publicationDate")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching articles: " + e.getMessage());
            e.printStackTrace();
        }
        return articles;
    }

    // Method to save viewed article history
    public void saveViewedHistory(int userId, int articleId) {
        executorService.submit(() -> {
            if (!isEntryExists(userId, articleId, "viewed_history")) {
                String sql = "INSERT INTO viewed_history (userID, articleID) VALUES (?, ?)";
                try (Connection connection = databaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setInt(1, userId);
                    statement.setInt(2, articleId);

                    statement.executeUpdate();
                    System.out.println("Viewed history saved for user ID: " + userId + " and article ID: " + articleId);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to save liked article
    public void saveLikedArticle(int userId, int articleId) {
        executorService.submit(() -> {
            if (!isEntryExists(userId, articleId, "article_likes")) {
                String sql = "INSERT INTO article_likes (userID, articleID) VALUES (?, ?)";
                try (Connection connection = databaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setInt(1, userId);
                    statement.setInt(2, articleId);

                    statement.executeUpdate();
                    System.out.println("Liked article saved for user ID: " + userId + " and article ID: " + articleId);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to check if an article is already liked by a user
    public boolean isArticleLiked(int userId, int articleId) {
        return isEntryExists(userId, articleId, "article_likes");
    }

    // Method to save skipped article
    public void saveSkippedArticle(int userId, int articleId) {
        executorService.submit(() -> {
            if (!isEntryExists(userId, articleId, "article_skips")) {
                String sql = "INSERT INTO article_skips (userID, articleID) VALUES (?, ?)";
                try (Connection connection = databaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    statement.setInt(1, userId);
                    statement.setInt(2, articleId);

                    statement.executeUpdate();
                    System.out.println("Skipped article saved for user ID: " + userId + " and article ID: " + articleId);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Method to check if an entry exists
    private boolean isEntryExists(int userId, int articleId, String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE userID = ? AND articleID = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, articleId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Article> getViewedArticles(int userId) {
        System.out.println("Fetching viewed articles for user ID: " + userId); // Debug statement
        List<Article> viewedArticles = new ArrayList<>();
        String sql = "SELECT a.id, a.title, a.source, a.url, a.content, a.category, a.publicationDate FROM articles a " +
                "JOIN viewed_history v ON a.id = v.articleID WHERE v.userID = ?";
        System.out.println("SQL Query: " + sql); // Debug statement
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                viewedArticles.add(new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("source"),
                        resultSet.getString("url"),
                        resultSet.getString("content"),
                        resultSet.getString("category"),
                        resultSet.getString("publicationDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Viewed Articles Fetched: " + viewedArticles.size()); // Debug statement
        return viewedArticles;
    }

    public List<Article> getLikedArticles(int userId) {
        System.out.println("Fetching liked articles for user ID: " + userId); // Debug statement
        List<Article> likedArticles = new ArrayList<>();
        String sql = "SELECT a.id, a.title, a.source, a.url, a.content, a.category, a.publicationDate FROM articles a " +
                "JOIN article_likes al ON a.id = al.articleID WHERE al.userID = ?";
        System.out.println("SQL Query: " + sql); // Debug statement
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                likedArticles.add(new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("source"),
                        resultSet.getString("url"),
                        resultSet.getString("content"),
                        resultSet.getString("category"),
                        resultSet.getString("publicationDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Liked Articles Fetched: " + likedArticles.size()); // Debug statement
        return likedArticles;
    }

    // Method to get popular articles
    public List<Article> getPopularArticles() {
        List<Article> popularArticles = new ArrayList<>();
        String sql = "SELECT a.id, a.title, a.source, a.url, a.content, a.category, a.publicationDate, COUNT(v.articleID) AS viewCount " +
                "FROM articles a " +
                "JOIN viewed_history v ON a.id = v.articleID " +
                "GROUP BY a.id " +
                "ORDER BY viewCount DESC " +
                "LIMIT 10";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                popularArticles.add(new Article(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("source"),
                        resultSet.getString("url"),
                        resultSet.getString("content"),
                        resultSet.getString("category"),
                        resultSet.getString("publicationDate")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return popularArticles;
    }

    // Method to delete an article by ID
    public void deleteArticle(int articleId) {
        executorService.submit(() -> {
            String sql = "DELETE FROM articles WHERE id = ?";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, articleId);
                statement.executeUpdate();
                System.out.println("Article deleted with ID: " + articleId);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Method to delete a user by ID
    public void deleteUser(int userId) {
        executorService.submit(() -> {
            String sql = "DELETE FROM users WHERE userID = ?";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, userId);
                statement.executeUpdate();
                System.out.println("User deleted with ID: " + userId);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Method to get all users from the database
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT userID, userName, firstName, lastName, password, preferences FROM users";
        try (Connection connection = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("userID"),
                        resultSet.getString("userName"),
                        resultSet.getString("firstName"),
                        resultSet.getString("lastName"),
                        resultSet.getString("password"),
                        resultSet.getString("preferences")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // Method to update user preferences
    public void updateUserPreferences(int userId, String preferences) {
        executorService.submit(() -> {
            String sql = "UPDATE users SET preferences = ? WHERE userID = ?";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, preferences);
                statement.setInt(2, userId);
                statement.executeUpdate();
                System.out.println("User preferences updated for user ID: " + userId);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Method to update user password
    public void updateUserPassword(int userId, String password) {
        executorService.submit(() -> {
            String sql = "UPDATE users SET password = ? WHERE userID = ?";
            try (Connection connection = databaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, password);
                statement.setInt(2, userId);
                statement.executeUpdate();
                System.out.println("User password updated for user ID: " + userId);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    // Shutdown the ExecutorService when it is no longer needed
    public void shutdown() {
        executorService.shutdown();
    }
}
