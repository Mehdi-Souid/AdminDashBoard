package com.example.admindashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainDashBoardApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainDashBoardApp.class.getResource("MainDashBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        MainDashBoardController controller = fxmlLoader.getController();
        controller.setStage(stage);


        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}