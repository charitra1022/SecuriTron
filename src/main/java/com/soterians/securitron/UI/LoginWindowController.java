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

public class LoginWindowController implements Initializable{
  @FXML
  private PasswordField pswdField;

  @FXML
  private TextField pswdTxtField;

  @FXML
  private Button loginBtn, showPswdBtn;

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
    showPswdBtn.setGraphic(show_icon);

    // set visibility of fields
    pswdTxtField.setVisible(false);

    // set button action for show/hide toggle buttons
    showPswdBtn.setOnAction(e -> togglePasswordVisibility());
    loginBtn.setOnAction(e -> loginBtnClicked());
  }


  /**
   * Toggles the visibility of Old Password fields and button icon
   */
  @FXML
  private void togglePasswordVisibility() {
    if(pswdField.isVisible()) {
      pswdTxtField.setText(pswdField.getText());
      pswdTxtField.setVisible(true);
      pswdField.setVisible(false);
      showPswdBtn.setGraphic(hide_icon);
    } else {
      pswdField.setText(pswdTxtField.getText());
      pswdTxtField.setVisible(false);
      pswdField.setVisible(true);
      showPswdBtn.setGraphic(show_icon);
    }
  }


  @FXML
  private void loginBtnClicked() {
    String pswd = "";

    // get the password field text
    if(pswdField.isVisible()) pswd = pswdField.getText();
    else pswd = pswdTxtField.getText();

    // focus on the old password field if empty
    if(pswd.isEmpty()) {
      if(pswdField.isVisible()) pswdField.requestFocus();
      else pswdTxtField.requestFocus();
      return;
    }

    System.out.println("LoginWindowController: loginBtnClicked (1) -> " + pswd);

    if(!DatabaseManager.isPasswordCorrect(pswd)) {
      CustomDialogs.incorrectPasswordDialog();  // show error dialog box
      return;
    }

    System.out.println("LoginWindowController: loginBtnClicked (2) -> login successful");
    ((Stage)loginBtn.getScene().getWindow()).close();  // close the login window
  }
}
