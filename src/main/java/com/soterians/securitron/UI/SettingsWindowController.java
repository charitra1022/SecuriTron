package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.DatabaseManager;
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

  @FXML
  private Label appTitleLabel;

  private final ImageView show_icon_old = new ImageView(IconPack.SHOW_EYE.getImage());
  private final ImageView show_icon_new = new ImageView(IconPack.SHOW_EYE.getImage());
  private final ImageView hide_icon_old = new ImageView(IconPack.HIDE_EYE.getImage());
  private final ImageView hide_icon_new = new ImageView(IconPack.HIDE_EYE.getImage());


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set app icon in label
    ImageView imgView = new ImageView(IconPack.APP_ICON_LIGHT.getImage());
    imgView.setFitHeight(60);
    imgView.setPreserveRatio(true);
    appTitleLabel.setGraphic(imgView);

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

    // check if old password is correct or not
    if(!DatabaseManager.isPasswordCorrect(oldPswd)) {
      CustomDialogs.incorrectPasswordDialog();
      if(oldPswdField.isVisible()) oldPswdField.requestFocus();
      else oldPswdTxtField.requestFocus();
      return;
    }

    // get the new password field text
    if(newPswdField.isVisible()) newPswd = newPswdField.getText();
    else newPswd = newPswdTxtField.getText();

    // if the password length is incorrect, notify user and focus on the textfield
    if(newPswd.length() < 6 || newPswd.length() > 16) {
      CustomDialogs.invalidPasswordLengthDialog(newPswd.length());  // show error dialog box
      if(newPswdField.isVisible()) newPswdField.requestFocus();
      else newPswdTxtField.requestFocus();
      return;
    }

    // if the new password and confirm password fields don't match
    if(!newPswd.equals(confirmPswd)) {
      CustomDialogs.passwordMismatchDialog(); // show error dialog box
      confirmPswdField.requestFocus();
      return;
    }

    System.out.println("SettingsWindowController: changePswdBtn -> " + newPswd);

    // attempt to change the password
    if(DatabaseManager.changeDBPassword(oldPswd, newPswd)) {
      CustomDialogs.showAlertDialog("Password Changed", "Password change was successful", Alert.AlertType.INFORMATION);
      CustomDialogs.passwordRegisteredDialog();
      resetFields();
    } else {
      CustomDialogs.showAlertDialog("Error", "Couldn't change the password!\nPlease try again later!", Alert.AlertType.ERROR);
    }
  }


  /**
   * Resets all the password and text fields
   */
  private void resetFields() {
    oldPswdField.setText("");
    oldPswdTxtField.setText("");
    newPswdField.setText("");
    newPswdTxtField.setText("");
    confirmPswdField.setText("");
  }
}
