package com.geekbrains.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cloud client!");
        stage.setScene(scene);
        stage.show();
    }
}
