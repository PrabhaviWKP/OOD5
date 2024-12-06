package Service;

import Database.DatabaseHandler;
import Model.SystemUser;
import Model.User;
import Database.DatabaseConnection;

import java.sql.*;

public class userService {

    public int getNextUserId() {
        int nextUserId = 1;
        String query = "SELECT MAX(userID) FROM users";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                nextUserId = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextUserId;
    }

    public boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registerUser(SystemUser user) {
        String insertQuery = "INSERT INTO users (userID, userName, firstName, lastName, password, preferences) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, user.getUserID());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPassword());

            if (user instanceof User) {
                preparedStatement.setString(6, ((User) user).getPreferences());
            } else {
                preparedStatement.setString(6, null); // Admin does not have preferences
            }

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if any result is returned
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
    public int getUserIdByUsername(String username) {
        String query = "SELECT userID FROM users WHERE username = ?";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if user ID is not found
    }

    public String getUserTypeByUsername(String username) {
        String query = "SELECT userType FROM users WHERE username = ?";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if user type is not found
    }

    // Method to record viewed article
    public void recordViewedArticle(int userId, int articleId, DatabaseHandler dbHandler) {
        dbHandler.saveViewedHistory(userId, articleId);
    }

    // Method to check if an article is liked
    public boolean isArticleLiked(int userId, int articleId, DatabaseHandler dbHandler) {
        return dbHandler.isArticleLiked(userId, articleId);
    }
}
