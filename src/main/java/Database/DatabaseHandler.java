package Database;

import Model.Article;
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
        // Initialize the DatabaseConnection within this class
        this.databaseConnection = new DatabaseConnection();
    }

    // Insert a new article only if it doesn't exist in the database
    public void insertArticle(Article article) {
        if (!articleExists(article.getUrl())) {
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
