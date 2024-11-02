package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import Database.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML
    private TextField txtUserID;
    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    public void initialize() {
        int nextUserId = getNextUserId();
        txtUserID.setText(String.valueOf(nextUserId));
    }

    private int getNextUserId() {
        int nextUserId = 1;
        String query = "SELECT MAX(userID) from users";
        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                nextUserId = resultSet.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextUserId;
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String userName = txtUsername.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (userName.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill all the fields.");
            return;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Password must be at least 6 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }

        if (isUsernameTaken(userName)) {
            showAlert(Alert.AlertType.ERROR, "Username Taken", "Username is taken.");
        }

        int nextUserId = getNextUserId();
        registerUser(nextUserId, userName, firstName, lastName, password);
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private boolean isUsernameTaken(String username) {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

//
        try (PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Returns true if username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void registerUser(int userId, String username, String firstName, String lastName, String password) {
        if (isUsernameTaken(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed!", "Username already exists. Please choose another.");
            return;
        }

        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String insertQuery = "INSERT INTO users (userID, userName, firstName, lastName, password) VALUES(?,?,?,?,?)";

        try{
            PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
            preparedStatement.setInt(1, userId); // Set user ID in the query
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            preparedStatement.setString(5, password);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Welcome" + username + "!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed!", "Something went wrong!");
            }
            preparedStatement.close();
            connectDB.close();
        } catch (Exception e){
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occured!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    private void clearForm() {
        txtUsername.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
    }
}
