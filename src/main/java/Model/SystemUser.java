package Model;

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

    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
