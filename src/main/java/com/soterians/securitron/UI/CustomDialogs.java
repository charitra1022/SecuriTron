package com.soterians.securitron.UI;

import javafx.scene.control.Alert;

public class CustomDialogs {
  /**
   * Creates and shows a alert dialog box with specific text and title
   * @param title text to display as dialogBox title
   * @param text text to display as dialogBox content
   */
  public static void showAlertDialog(String title, String text, Alert.AlertType alertType) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setContentText(text);
    alert.setHeaderText(null);
    alert.showAndWait();
  }


  /**
   * Displays a predefined incorrect password dialog box
   */
  public static void incorrectPasswordDialog() {
    String title = "Incorrect Password!";
    String context = "Please enter correct password!";
    showAlertDialog(title, context, Alert.AlertType.ERROR);
  }


  /**
   * Displays a predefined invalid password length dialog box
   */
  public static void invalidPasswordLengthDialog(int length) {
    String title = "Invalid Password Length";
    String context = "Password Length should be in between 8 - 16 characters\nCurrent length: " + length;
    showAlertDialog(title, context, Alert.AlertType.ERROR);
  }


  /**
   * Displays a predefined password dont match dialog box
   */
  public static void passwordMismatchDialog() {
    String title = "Password Mismatch";
    String context = "Password fields don't match!";
    showAlertDialog(title, context, Alert.AlertType.ERROR);
  }


  /**
   * Displays a predefined password registered dialog box
   */
  public static void passwordRegisteredDialog() {
    String title = "Password Registered!";
    String context = "Store the password at some safe place as you won't be able to access the software without it!!";
    showAlertDialog(title, context, Alert.AlertType.WARNING);
  }
}
