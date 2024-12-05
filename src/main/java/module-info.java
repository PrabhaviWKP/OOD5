module org.example.ood5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;
    requires jdk.httpserver;
    requires mysql.connector.j;
    requires librec.core;
    requires commons.math3;


    opens Main to javafx.graphics, javafx.fxml;
    exports App;
    exports Model;
    exports Service;
    exports Database;
    exports Main;
    opens App to javafx.fxml;
}