package App;

import Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;

public class editPreferencesController {

    @FXML
    private CheckBox chkSports;
    @FXML
    private CheckBox chkTechnology;
    @FXML
    private CheckBox chkPolitics;
    @FXML
    private CheckBox chkEntertainment;
    @FXML
    private CheckBox chkHealth;
    @FXML
    private CheckBox chkAI;

    private User user; // Store the user object

    // Initialize the controller with the current user
    public void initialize(User user) {
        this.user = user;
        loadPreferences();
    }

    // Load the user's current preferences
    private void loadPreferences() {
        String[] preferences = user.getPreferences().split(",");
        for (String preference : preferences) {
            switch (preference.trim()) {
                case "Sports":
                    chkSports.setSelected(true);
                    break;
                case "Technology":
                    chkTechnology.setSelected(true);
                    break;
                case "Politics":
                    chkPolitics.setSelected(true);
                    break;
                case "Entertainment":
                    chkEntertainment.setSelected(true);
                    break;
                case "Health":
                    chkHealth.setSelected(true);
                    break;
                case "AI":
                    chkAI.setSelected(true);
                    break;
            }
        }
    }

    // Handle the save preferences action
    @FXML
    private void handleSavePreferences() {
        StringBuilder preferences = new StringBuilder();
        if (chkSports.isSelected()) {
            preferences.append("Sports").append(",");
        }
        if (chkTechnology.isSelected()) {
            preferences.append("Technology").append(",");
        }
        if (chkPolitics.isSelected()) {
            preferences.append("Politics").append(",");
        }
        if (chkEntertainment.isSelected()) {
            preferences.append("Entertainment").append(",");
        }
        if (chkHealth.isSelected()) {
            preferences.append("Health").append(",");
        }
        if (chkAI.isSelected()) {
            preferences.append("AI").append(",");
        }

        // Remove the trailing comma
        if (preferences.length() > 0) {
            preferences.setLength(preferences.length() - 1);
        }

        user.updatePreferences(preferences.toString());
        showAlert(Alert.AlertType.INFORMATION, "Preferences Saved", "Your preferences have been successfully saved.");
    }

    // Method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
