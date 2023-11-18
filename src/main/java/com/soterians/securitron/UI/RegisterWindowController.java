package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.DatabaseManager;
import com.soterians.securitron.Utils.IconPack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterWindowController implements Initializable{
  @FXML
  private PasswordField newPswdField, confirmPswdField;

  @FXML
  private TextField newPswdTxtField;

  @FXML
  private Button showNewPswdBtn, registerBtn;

  private final ImageView show_icon = new ImageView(IconPack.SHOW_EYE.getImage());
  private final ImageView hide_icon = new ImageView(IconPack.HIDE_EYE.getImage());


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set icon properties
    show_icon.setPreserveRatio(true);
    hide_icon.setPreserveRatio(true);
    show_icon.setFitHeight(20);
    hide_icon.setFitHeight(20);

    // set button icon
    showNewPswdBtn.setGraphic(show_icon);

    // set visibility of fields
    newPswdTxtField.setVisible(false);

    // set button action for show/hide toggle buttons
    showNewPswdBtn.setOnAction(e -> toggleNewPasswordVisibility());
    registerBtn.setOnAction(e -> registerBtnClicked());
  }


  /**
   * Toggles the visibility of New Password field and button icon
   */
  @FXML
  private void toggleNewPasswordVisibility() {
    if(newPswdField.isVisible()) {
      newPswdTxtField.setText(newPswdField.getText());
      newPswdTxtField.setVisible(true);
      newPswdField.setVisible(false);
      showNewPswdBtn.setGraphic(hide_icon);
    } else {
      newPswdField.setText(newPswdTxtField.getText());
      newPswdTxtField.setVisible(false);
      newPswdField.setVisible(true);
      showNewPswdBtn.setGraphic(show_icon);
    }
  }


  /**
   * Creates and shows a dialog box with specific text and title
   * @param title text to display as dialogBox title
   * @param text text to display as dialogBox content
   */
  private void showSimpleDialog(String title, String text) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle(title);
    dialog.setContentText(text);
    dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
    dialog.showAndWait();
  }


  @FXML
  private void registerBtnClicked() {
    String newPswd = "";
    String confirmPswd = confirmPswdField.getText();

    // get the new password field text
    if(newPswdField.isVisible()) newPswd = newPswdField.getText();
    else newPswd = newPswdTxtField.getText();

    // if the password length is incorrect, notify user and focus on the textfield
    if(newPswd.length() < 6 || newPswd.length() > 16) {
      showSimpleDialog("Invalid Password Length", "Password Length should be in between 8 - 16 characters" +
              "\nCurrent length: " + newPswd.length());
      if(newPswdField.isVisible()) newPswdField.requestFocus();
      else newPswdTxtField.requestFocus();
      return;
    }

    // if the new password and confirm password fields don't match
    if(newPswd.length() != confirmPswd.length()) {
      showSimpleDialog("Password Mismatch", "Password fields don't match!");
      confirmPswdField.requestFocus();
      return;
    }

    System.out.println("RegisterWindowController: registerBtnClicked -> " + newPswd);

    DatabaseManager.initializeDB(newPswd);  // create database
    ((Stage)registerBtn.getScene().getWindow()).close();  // close the register window
  }
}
