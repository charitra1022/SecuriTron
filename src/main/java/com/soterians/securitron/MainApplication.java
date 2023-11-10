package com.soterians.securitron;

import com.soterians.securitron.UI.MainWindowController;
import com.soterians.securitron.Utils.EncryptedFileMetadata;
import com.soterians.securitron.Utils.ManageEncryptedFileList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class MainApplication extends Application {
  private FXMLLoader fxmlLoader;

  @Override
  public void start(Stage stage) throws IOException {
    fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main_window.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 500, 500);
    stage.setTitle("SecuriTron");
    stage.setScene(scene);
    stage.setMinWidth(900);
    stage.setMinHeight(600);
    stage.setResizable(false);

    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}