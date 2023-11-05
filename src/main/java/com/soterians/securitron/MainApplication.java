package com.soterians.securitron;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main_window.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 500, 500);
    stage.setTitle("SecuriTron");
    stage.setScene(scene);
    stage.setMinWidth(500);
    stage.setMinHeight(500);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}