package Database;

import Database.DatabaseConnection;
import Model.Article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

    private final DatabaseConnection databaseConnection;

    public DatabaseHandler(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // Method to insert a new article into the database
    public void insertArticle(Article article) {
        if (article.getPublicationDate() == null) {
            System.out.println("Skipping article due to null publication date: " + article.getArticleTitle());
            return;
        }
        String sql = "INSERT INTO articles (title, content, category, source, publicationDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, article.getArticleTitle());
            statement.setString(2, article.getContent());
            statement.setString(3, article.getCategory());
            statement.setString(4, article.getSource());
            statement.setDate(5, new java.sql.Date(article.getPublicationDate().getTime()));

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
