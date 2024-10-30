package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Adjust the path if register.fxml is located in a subdirectory like "resources/fxml/"
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));

        primaryStage.setTitle("User Registration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
