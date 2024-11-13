package Database;

import Model.Article;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

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
}
