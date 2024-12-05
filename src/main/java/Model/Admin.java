package Model;

import Database.DatabaseHandler;

public class Admin extends SystemUser {
    public Admin(int userID, String userName, String firstName, String lastName, String password) {
        super(userID, userName, firstName, lastName, password);
    }

    // Method to delete an article
    public void deleteArticle(int articleId, DatabaseHandler dbHandler) {
        dbHandler.deleteArticle(articleId);
    }

    // Method to delete a user
    public void deleteUser(int userId, DatabaseHandler dbHandler) {
        dbHandler.deleteUser(userId);
    }
}
