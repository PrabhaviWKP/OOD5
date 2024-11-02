package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "sample1";
        String databaseUser = "root";
        String databasePassword = "Prabs1412";
        String url = "jdbc:mysql://localhost:3306/sample1";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink  = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

        return databaseLink;
    }

}
