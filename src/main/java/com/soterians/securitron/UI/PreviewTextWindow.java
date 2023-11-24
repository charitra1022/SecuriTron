package com.soterians.securitron.UI;

import com.soterians.securitron.Utils.CryptoClasses.CryptoException;
import com.soterians.securitron.Utils.CryptoClasses.CryptoUtils;
import com.soterians.securitron.Utils.CryptoClasses.EncryptedFileMetadata;
import com.soterians.securitron.Utils.DatabaseManager;
import com.soterians.securitron.Utils.IconPack;
import com.soterians.securitron.Utils.SHA256Checksum;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PreviewTextWindow {
  private AnchorPane root, ac1, ac2;
  private GridPane gridPane;
  private Button editBtn, cancelBtn, saveBtn;
  private TextArea textArea;
  private Stage stage;
  private String originalText = null;
  private EncryptedFileMetadata fileMetadata;

  public static boolean isFileEdited = false; // updates if any file has been edited in Open mode and updates UI accordingly


  public PreviewTextWindow(EncryptedFileMetadata fileMetadata) throws IOException, CryptoException {
    this.fileMetadata = fileMetadata;
    this.originalText = getStringFromEncryptedFileMetadata();

    // initialize textarea properties
    textArea = new TextArea(originalText);
    textArea.setEditable(false);
    textArea.setFont(Font.font(15));
    AnchorPane.setTopAnchor(textArea, 0.0);
    AnchorPane.setRightAnchor(textArea, 0.0);
    AnchorPane.setBottomAnchor(textArea, 0.0);
    AnchorPane.setLeftAnchor(textArea, 0.0);
    ac1 = new AnchorPane();
    ac1.getChildren().add(textArea);


    // initialize edit button properties
    editBtn = new Button();
    editBtn.setText("Edit");
    editBtn.setPrefWidth(60.0);
    AnchorPane.setTopAnchor(editBtn, 10.0);
    AnchorPane.setLeftAnchor(editBtn, 20.0);
    AnchorPane.setBottomAnchor(editBtn, 10.0);

    // initialize save button properties
    saveBtn = new Button();
    saveBtn.setText("Save");
    saveBtn.setPrefWidth(60.0);
    AnchorPane.setTopAnchor(saveBtn, 10.0);
    AnchorPane.setRightAnchor(saveBtn, 20.0);
    AnchorPane.setBottomAnchor(saveBtn, 10.0);

    // initialize cancel button properties
    cancelBtn = new Button();
    cancelBtn.setText("Cancel");
    editBtn.setPrefWidth(60.0);
    AnchorPane.setTopAnchor(cancelBtn, 10.0);
    AnchorPane.setRightAnchor(cancelBtn, 100.0);
    AnchorPane.setBottomAnchor(cancelBtn, 10.0);

    // add buttons to anchor pane
    ac2 = new AnchorPane();
    ac2.getChildren().addAll(editBtn, cancelBtn, saveBtn);

    // initialize gridpane properties
    gridPane = new GridPane();
    AnchorPane.setTopAnchor(gridPane, 0.0);
    AnchorPane.setRightAnchor(gridPane, 0.0);
    AnchorPane.setBottomAnchor(gridPane, 0.0);
    AnchorPane.setLeftAnchor(gridPane, 0.0);
    gridPane.addRow(0, ac1);
    gridPane.addRow(1, ac2);
    RowConstraints rc1 = new RowConstraints();
    RowConstraints rc2 = new RowConstraints();
    ColumnConstraints cc1 = new ColumnConstraints();
    rc1.setVgrow(Priority.SOMETIMES);
    rc2.setVgrow(Priority.NEVER);
    cc1.setHgrow(Priority.SOMETIMES);
    gridPane.getRowConstraints().add(0,  rc1);
    gridPane.getRowConstraints().add(1,  rc2);
    gridPane.getColumnConstraints().add(0, cc1);
    root = new AnchorPane();
    root.getChildren().add(gridPane);


    // setting click listeners
    editBtn.setOnAction(e -> editBtnClicked());
    cancelBtn.setOnAction(e -> cancelBtnClicked());
    saveBtn.setOnAction(e -> {
      try {
        saveBtnClicked();
      } catch(CryptoException ex) {
        throw new RuntimeException(ex);
      } catch(NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      } catch(IOException ex) {
        throw new RuntimeException(ex);
      }
    });


    // set button visibilities
    saveBtn.setVisible(false);
    cancelBtn.setVisible(false);


    // initialize the stage
    stage = new Stage();
    stage.setScene(new Scene(root));
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle(fileMetadata.getFileName());
    stage.getIcons().add(IconPack.APP_ICON.getImage());
  }

  public Stage getStage() { return stage; }


  private void editBtnClicked() {
    textArea.setEditable(true); // set the text area editable
    saveBtn.setVisible(true);
    cancelBtn.setVisible(true);
    editBtn.setVisible(false);
  }

  private void cancelBtnClicked() {
    textArea.setEditable(false);  // set the text area non-editable
    saveBtn.setVisible(false);
    cancelBtn.setVisible(false);
    editBtn.setVisible(true);
    textArea.setText(originalText);  // reset the edited text to original text
  }


  private void saveBtnClicked() throws CryptoException, NoSuchAlgorithmException, IOException {
    // if no changes were made, skip
    if(Objects.equals(textArea.getText(), originalText)) {
      CustomDialogs.showAlertDialog("SecuriTron: Save file", "No changes detected!", Alert.AlertType.INFORMATION);
      return;
    }

    // show confirmation box for saving file
    ButtonType userInput = CustomDialogs.showAlertDialog("SecuriTron: Save file", "Do you want to save the edited file?", Alert.AlertType.CONFIRMATION);

    // if user clicked cancel, fire the cancel button of the UI
    if(userInput != ButtonType.OK) {
      cancelBtn.fire();
      return;
    }

    // if user clicked save button, save the new data to the encrypted file if changed and close the preview
    String newText = textArea.getText();
    textArea.setEditable(false);  // set the text area non-editable
    saveBtn.setVisible(false);
    cancelBtn.setVisible(false);
    editBtn.setVisible(true);

    // convert string content to byte array and re-encrypt the file
    byte[] dataBytes = newText.getBytes();
    CryptoUtils.saveDecryptedData(
            fileMetadata.getSecretKey(),
            dataBytes,
            fileMetadata.getEncryptedFile()
    );

    // create new fileMetadata object with new data
    EncryptedFileMetadata newFileMetadata = new EncryptedFileMetadata(
            fileMetadata.getFile(),
            SHA256Checksum.getFileChecksumFromBytes(dataBytes),
            fileMetadata.getEncryptedOn(),
            dataBytes.length,
            fileMetadata.getEncryptedFile(),
            fileMetadata.getSecretKey()
    );

    // update the data in the database
    DatabaseManager.deleteEncryptedFileData(fileMetadata);
    DatabaseManager.updateEncryptedFileData(fileMetadata, newFileMetadata);

    PreviewTextWindow.isFileEdited = true;  // for updating the UI

    stage.close();
  }


  /**
   * Gets the file contents from the encrypted file by decrypting its data byte array
   * @return String representing the file contents
   * @throws IOException
   * @throws CryptoException
   */
  private String getStringFromEncryptedFileMetadata() throws IOException, CryptoException {
    // read the file contents in form of byte array
    FileInputStream fileInputStream = new FileInputStream(fileMetadata.getEncryptedFile());

    // get the decrypted data bytes from the encrypted data file
    byte[] dataBytes = CryptoUtils.readEncryptedData(
            fileMetadata.getSecretKey(),
            fileMetadata.getEncryptedFile(),
            fileInputStream
    );
    fileInputStream.close();

    return new String(dataBytes, StandardCharsets.UTF_8);
  }
}
