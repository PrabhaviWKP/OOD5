package Service;

import Model.User;
import Database.DatabaseConnection;

import java.sql.*;

public class userService {
    private static final String url = "jdbc:mysql://localhost:3306/sample1"; // replace with your database URL
    private static final String dbusername = "root"; // replace with your DB username
    private static final String dbpassword = "Prabs1412"; // replace with your DB password


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

    public boolean registerUser(User user) {
        String insertQuery = "INSERT INTO users (userID, userName, firstName, lastName, password, preferences) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {

            preparedStatement.setInt(1, user.getUserID());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getPreferences());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean validateCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try(Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            // Check if any result is returned
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
}
