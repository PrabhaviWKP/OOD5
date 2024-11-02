package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        loadMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void loadMainMenu() throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/MainMenu.fxml"));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to load the login screen
    public static void loadLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("User Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // Method to load the registration screen
    public static void loadRegisterScreen() throws Exception {
        Parent root = FXMLLoader.load(Main.class.getResource("/fxml/register.fxml"));
        primaryStage.setTitle("User Registration");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
