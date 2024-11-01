package com.example.library_managmenrt.utill;

import com.example.library_managmenrt.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigation {
    public static void navigateToScene(String pageName, Button wantedButtonName) {
        try {
            // Load the new scene
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
            Scene mainScene = new Scene(fxmlLoader.load());

            // Get the current stage (window)
            Stage currentStage = (Stage) wantedButtonName.getScene().getWindow();

            // Set the new scene to the current stage
            currentStage.setScene(mainScene);
            currentStage.setTitle("Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
