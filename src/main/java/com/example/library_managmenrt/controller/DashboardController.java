package com.example.library_managmenrt.controller;

import com.example.library_managmenrt.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    public Button category_button;
    public Button book_button;
    public Button member_button;
    public Button transaction_button;
    public Button logout_button;

    public void handleCategoryClick(ActionEvent actionEvent) {
        navigateToScene("manage-categories.fxml");
    }

    public void handleBookClick(ActionEvent actionEvent) {
        navigateToScene("manage-books.fxml");
    }
    public void handleMemberClick(ActionEvent actionEvent) {
        navigateToScene("manage-members.fxml");
    }
    public void handleBorrowingClick(ActionEvent actionEvent) {
        navigateToScene("manage-transactions.fxml");
    }

    public void handleLogoutClick(ActionEvent actionEvent) {
        navigateToScene("login.fxml");
    }

    private void navigateToScene(String pageName) {
        try {
            // Load the new scene
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(pageName));
            Scene mainScene = new Scene(fxmlLoader.load());

            // Get the current stage (window)
            Stage currentStage = (Stage) category_button.getScene().getWindow();

            // Set the new scene to the current stage
            currentStage.setScene(mainScene);
            currentStage.setTitle(pageName.split("\\.")[0]);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
