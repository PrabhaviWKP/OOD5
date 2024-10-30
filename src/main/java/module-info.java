module org.example.ood5 {
    requires javafx.controls;
    requires javafx.fxml;


    opens Main to javafx.graphics, javafx.fxml;
    exports Main;
    exports Controller;
    opens Controller to javafx.fxml;
}