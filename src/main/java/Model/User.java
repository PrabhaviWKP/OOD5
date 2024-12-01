package Model;

import Database.DatabaseHandler;
import java.util.List;

public class User {
    private int userID;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private String preferences;

    public User(int userID, String userName, String firstName, String lastName, String password, String preferences) {
        this.userID = userID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.preferences = preferences;
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    // Method to record viewed article
    public void recordViewedArticle(int articleId, DatabaseHandler dbHandler) {
        dbHandler.saveViewedHistory(this.userID, articleId);
    }

    // Method to like an article
    public void likeArticle(int articleId, DatabaseHandler dbHandler) {
        dbHandler.saveLikedArticle(this.userID, articleId);
    }

    // Method to check if an article is liked
    public boolean isArticleLiked(int articleId, DatabaseHandler dbHandler) {
        return dbHandler.isArticleLiked(this.userID, articleId);
    }

    // Method to skip an article
    public void skipArticle(int articleId, DatabaseHandler dbHandler) {
        dbHandler.saveSkippedArticle(this.userID, articleId);
    }
}
