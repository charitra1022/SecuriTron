package com.soterians.securitron.UI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainWindowController {
  @FXML
  private Button settingsBtn, aboutBtn, closeBtn;

  @FXML
  private void settingsBtnClicked(ActionEvent actionEvent) {
    System.out.println("settings button clicked");
  }

  @FXML
  private void aboutBtnClicked(ActionEvent actionEvent) {
    System.out.println("about button clicked");
  }

  @FXML
  private void closeBtnClicked(ActionEvent actionEvent) {
    Stage stage = (Stage) closeBtn.getScene().getWindow();
    stage.close();
  }
}