module org.example.ood5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;
    requires jdk.httpserver;
    requires mysql.connector.j;


    opens Main to javafx.graphics, javafx.fxml;
    exports Main;
    exports App;
    opens App to javafx.fxml;
}