package Controller;

import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {
    @FXML
    private void handleGoToLogin(ActionEvent event) {
        try {
            Main.loadLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToRegister(ActionEvent event) {
        try{
            Main.loadRegisterScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
