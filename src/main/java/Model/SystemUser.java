package Model;

import Service.userService;

public abstract class SystemUser {
    private int userID;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;

    public SystemUser(int userID, String userName, String firstName, String lastName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
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

    // Method to handle login logic
    public boolean login(String userName, String password, userService userService) {
        return userService.validateCredentials(userName, password);
    }

    // Method to handle log out logic
    public void logOut() {
        System.out.println("User logged out successfully.");
    }
}
