package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.IconPack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsWindowController implements Initializable{
  @FXML
  private PasswordField oldPswdField, newPswdField, confirmPswdField;

  @FXML
  private TextField oldPswdTxtField, newPswdTxtField;

  @FXML
  private Button showOldPswdBtn, showNewPswdBtn, applyPswdBtn;

  private final ImageView show_icon_old = new ImageView(IconPack.SHOW_EYE.getImage());
  private final ImageView show_icon_new = new ImageView(IconPack.SHOW_EYE.getImage());
  private final ImageView hide_icon_old = new ImageView(IconPack.HIDE_EYE.getImage());
  private final ImageView hide_icon_new = new ImageView(IconPack.HIDE_EYE.getImage());


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set icon properties
    show_icon_old.setPreserveRatio(true);
    show_icon_new.setPreserveRatio(true);
    hide_icon_old.setPreserveRatio(true);
    hide_icon_new.setPreserveRatio(true);
    show_icon_old.setFitHeight(20);
    show_icon_new.setFitHeight(20);
    hide_icon_old.setFitHeight(20);
    hide_icon_new.setFitHeight(20);

    // set button icons
    showOldPswdBtn.setGraphic(show_icon_old);
    showNewPswdBtn.setGraphic(show_icon_new);

    // set visibility of fields
    oldPswdTxtField.setVisible(false);
    newPswdTxtField.setVisible(false);

    // set button action for show/hide toggle buttons
    showOldPswdBtn.setOnAction(e -> toggleOldPasswordVisibility());
    showNewPswdBtn.setOnAction(e -> toggleNewPasswordVisibility());
  }


  /**
   * Toggles the visibility of Old Password fields and button icon
   */
  @FXML
  private void toggleOldPasswordVisibility() {
    if(oldPswdField.isVisible()) {
      oldPswdTxtField.setText(oldPswdField.getText());
      oldPswdTxtField.setVisible(true);
      oldPswdField.setVisible(false);
      showOldPswdBtn.setGraphic(hide_icon_old);
    } else {
      oldPswdField.setText(oldPswdTxtField.getText());
      oldPswdTxtField.setVisible(false);
      oldPswdField.setVisible(true);
      showOldPswdBtn.setGraphic(show_icon_old);
    }
  }


  /**
   * Toggles the visibility of New Password fields and button icon
   */
  @FXML
  private void toggleNewPasswordVisibility() {
    if(newPswdField.isVisible()) {
      newPswdTxtField.setText(newPswdField.getText());
      newPswdTxtField.setVisible(true);
      newPswdField.setVisible(false);
      showNewPswdBtn.setGraphic(hide_icon_new);
    } else {
      newPswdField.setText(newPswdTxtField.getText());
      newPswdTxtField.setVisible(false);
      newPswdField.setVisible(true);
      showNewPswdBtn.setGraphic(show_icon_new);
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
  private void changePswdBtn() {
    String oldPswd = "";
    String newPswd = "";
    String confirmPswd = confirmPswdField.getText();

    // get the old password field text
    if(oldPswdField.isVisible()) oldPswd = oldPswdField.getText();
    else oldPswd = oldPswdTxtField.getText();

    // focus on the old password field if empty
    if(oldPswd.isEmpty()) {
      if(oldPswdField.isVisible()) oldPswdField.requestFocus();
      else oldPswdTxtField.requestFocus();
      return;
    }

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
    if(!newPswd.equals(confirmPswd)) {
      showSimpleDialog("Password Mismatch", "Password fields don't match!");
      confirmPswdField.requestFocus();
      return;
    }

    System.out.println("SettingsWindowController: changePswdBtn -> " + newPswd);


  }
}
