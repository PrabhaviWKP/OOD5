package Model;

import Database.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class User extends SystemUser {
    private String preferences;
    private List<Integer> likedArticles;
    private List<Integer> viewedArticles;
    private List<Integer> skippedArticles;
    private final DatabaseHandler dbHandler = new DatabaseHandler();

    public User(int userID, String userName, String firstName, String lastName, String password, String preferences) {
        super(userID, userName, firstName, lastName, password);
        this.preferences = preferences;
        this.likedArticles = new ArrayList<>();
        this.viewedArticles = new ArrayList<>();
        this.skippedArticles = new ArrayList<>();
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    // Method to like an article
    public void likeArticle(int articleId, DatabaseHandler dbHandler) {
        if (!likedArticles.contains(articleId)) {
            likedArticles.add(articleId);
            dbHandler.saveLikedArticle(this.getUserID(), articleId);
        }
    }

    // Method to skip an article
    public void skipArticle(int articleId, DatabaseHandler dbHandler) {
        if (!skippedArticles.contains(articleId)) {
            skippedArticles.add(articleId);
            dbHandler.saveSkippedArticle(this.getUserID(), articleId);
        }
    }

    // Method to update user preferences
    public void updatePreferences(String preferences) {
        this.preferences = preferences;
        dbHandler.updateUserPreferences(this.getUserID(), preferences);
    }

    // Method to update user password
    public void updatePassword(String newPassword) {
        if (User.isValidPassword(newPassword)) {
            dbHandler.updateUserPassword(this.getUserID(), newPassword);
        } else {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
    }
}
