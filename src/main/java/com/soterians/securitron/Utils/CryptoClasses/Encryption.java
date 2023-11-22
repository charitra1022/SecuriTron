package com.soterians.securitron.Utils.CryptoClasses;

import com.soterians.securitron.UI.CustomDialogs;
import com.soterians.securitron.Utils.DatabaseManager;
import com.soterians.securitron.Utils.IconPack;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;

public class Encryption {
  private static final String extension = ".securitron";

  private static boolean deleteOriginal = false;  // delete all original files after encryption


  /**
   * Displays an Alert DialogPane to ask for user's consent to delete original files after encryption is done
   * @param contextString Filename of the file just encrypted
   * @return ButtonType object that user selected - YES, NO, APPLY (Yes to All)
   */
  private static ButtonType showDeleteAlert(String contextString) {
    // Alert for user confirmation for deleting original files
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(IconPack.APP_ICON.getImage());
    alert.setTitle("Encryption Done");
    alert.setHeaderText("Do you want to delete the Original file?");
    alert.setContentText(contextString);
    alert.getButtonTypes().clear();
    ButtonType yesAll = new ButtonType("Yes to All", ButtonBar.ButtonData.APPLY);
    alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, yesAll);

    // set NO button as default
    ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
    ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
    ((Button)alert.getDialogPane().lookupButton(yesAll)).setDefaultButton(false);

    // process the user interaction with the alert
    Optional<ButtonType> result = alert.showAndWait();
    return result.orElse(ButtonType.NO);
  }


  /**
   * Encrypts a file and returns the EncryptedFileMetadata object that contains all important info
   * @param inputFile File object to be encrypted
   * @return EncryptedFileMetadata object that contains all basic info about the encrypted file
   */
  public static EncryptedFileMetadata encryptFile(File inputFile) throws NoSuchAlgorithmException, IOException {
    String parent = inputFile.getParent();      // get path of parent directory
    String filename = inputFile.getName();      // get name of the file
    String outputFilename = filename + extension;   // name of new file

    // create file object for encrypted file
    File encryptedFile = new File(Paths.get(parent, outputFilename).toString());

    String secret_key = "this is new key!"; // to be generated randomly of size 16

    // store basic info before encrypting
    EncryptedFileMetadata fileMetadata = new EncryptedFileMetadata(inputFile, encryptedFile, secret_key);

    // encrypt the file
    try {
      CryptoUtils.encrypt(secret_key, inputFile, encryptedFile); // encrypt the input file
      System.out.println("Encryption: encryptFile -> Encrypted: " + encryptedFile.getAbsolutePath());

      // if delete for all files is enabled, direct delete
      if(deleteOriginal) inputFile.delete();
      // otherwise ask for user confirmation
      else {
        ButtonType result = showDeleteAlert(inputFile.getName());  // show confirmation

        // delete only current file
        if(result.getButtonData() == ButtonType.YES.getButtonData())
          inputFile.delete();     // delete original file after encryption

        // delete all consequent files
        else if(result.getButtonData() == ButtonType.APPLY.getButtonData()){
          inputFile.delete();     // delete original file after encryption
          deleteOriginal = true;
        }
      }

    } catch (CryptoException ex) {
      System.out.println("Encryption: encryptFile -> Error: " + ex.getMessage());
      ex.printStackTrace();
    }

    return fileMetadata;
  }


  /**
   * Encrypts the list of files passed
   * @param files ArrayList&lt;File&gt; files object that stores list of Files that need to be encrypted
   */
  public static void encryptFiles(ArrayList<File> files) throws IOException, NoSuchAlgorithmException {
    // create list of EncryptedFileMetadata objects
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    for(int i=0; i<files.size(); i++) {
      fileMetadataList.add(encryptFile(files.get(i)));
    }

    // save the list to the database
    DatabaseManager.insertEncryptedFileData(fileMetadataList);

    // reset the default deletion value
    deleteOriginal = false;
  }


  /**
   * Decrypts the encrypted file and deletes it from the disk
   * @param fileMetadata EncryptedFileMetadata object of the encrypted file
   */
  public static boolean decryptFile(EncryptedFileMetadata fileMetadata) {
    System.out.println("Encryption: decryptFile (1) -> " + fileMetadata);

    // create respective file objects
    File encryptedFile = fileMetadata.getEncryptedFile();
    File decryptedFile = fileMetadata.getFile();

    // TODO: if a file with same name as decrypting file is present, ask user consent to delete or rename new file

    // decrypt the file
    try {
      CryptoUtils.decrypt(fileMetadata.getSecretKey(), encryptedFile, decryptedFile);
      System.out.println("Encryption: decryptFile (2) -> Decrypted: " + decryptedFile.getAbsolutePath());
      encryptedFile.delete(); // delete the encrypted file after it is decrypted
      DatabaseManager.deleteEncryptedFileData(fileMetadata);  // delete the file info from the database

      return true;

      // TODO: remove the file entry from the list and update the listView
    } catch (CryptoException ex) {
      String msg = "";
      if(!encryptedFile.exists()) msg = "File not found!";
      else msg = "Error in decrypting file!";

      System.out.println("Encryption: decryptFile (3) -> ERROR: " + ex.getMessage());
      System.out.println("Encryption: decryptFile (4) -> ERROR: " + msg);
      ex.printStackTrace();

      // display the error dialog box
      CustomDialogs.showAlertDialog("SecuriTron: " + msg, "Path: " + fileMetadata.getEncryptedFilePath(), Alert.AlertType.ERROR);

      // TODO: to ask user to remove entry for the error file from the list
    }

    return false;
  }


  /**
   * Opens a file temporarily.
   * Opens text and image files directly from memory, without decrypting to disk.
   * @param fileMetadata EncryptedFileMetadata object of the main encrypted file
   */
  public static void openFileTemporarily(EncryptedFileMetadata fileMetadata) {
    try{
      String mimeType = Files.probeContentType(fileMetadata.getFile().toPath());  // get file type

      long heapSize = Runtime.getRuntime().freeMemory();  // get the current free heap memory
      long fileSize = fileMetadata.getFileSize(); // get file size

      // if file is too large to be opened
      if(fileSize > (heapSize - heapSize*0.1)) {
        System.out.println("Encryption: openFileTemporarily (1) -> heapsize = " + heapSize + " filesize = " + fileSize);
        CustomDialogs.showAlertDialog("Cannot open file", "File too large to be opened!", Alert.AlertType.ERROR);
        return;
      }

      // if file is supported, display it
      if(mimeType!=null && (mimeType.contains("text") || mimeType.contains("image"))) {
        Stage stage = new Stage();  // stage that will contain the popup for preview
        Node previewChild = new Node(){}; // node to display contents. casted to required type according to file type

        // read the file contents in form of byte array
        byte[] dataBytes = CryptoUtils.readEncryptedData(fileMetadata.getSecretKey(), fileMetadata.getEncryptedFile());

        // content is text, so cast Node to TextArea
        if(mimeType.contains("text")) {
          TextArea textArea = new TextArea(new String(dataBytes, StandardCharsets.UTF_8));  // get string from the file bytes
          textArea.setEditable(false);
          textArea.setFont(Font.font(18));
          previewChild = textArea;
        } else {
          Label imageLabel = new Label("this is a label, to display image here");
          imageLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

          ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(dataBytes)));
          imageView.setPreserveRatio(true);
          imageView.setFitHeight(Math.min(imageView.getImage().getHeight(), 600));  // choose the image view height to be minimum of canvas size and iamge size
          imageLabel.setGraphic(imageView);
          previewChild = imageLabel;  // assign the label to the node
          stage.setResizable(false);
        }

        // preview content modal
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(fileMetadata.getFileName());
        stage.getIcons().add(IconPack.APP_ICON.getImage());

        AnchorPane parent = new AnchorPane();
        AnchorPane.setTopAnchor(previewChild, 0.0);
        AnchorPane.setBottomAnchor(previewChild, 0.0);
        AnchorPane.setLeftAnchor(previewChild, 0.0);
        AnchorPane.setRightAnchor(previewChild, 0.0);
        parent.getChildren().add(previewChild);
        stage.setScene(new Scene(parent));
        stage.showAndWait();
        return;
      }

      CustomDialogs.showAlertDialog("Unsupported File","Selected file cannot be opened without decrypting!\n\nOnly images and text files can be opened!", Alert.AlertType.ERROR);
    } catch(IOException | CryptoException ex) {
      if(!fileMetadata.getEncryptedFile().exists())
        CustomDialogs.showAlertDialog("SecuriTron: File not found!", "Path: " + fileMetadata.getEncryptedFilePath(), Alert.AlertType.ERROR);
      else
        CustomDialogs.showAlertDialog("SecuriTron: Error!", ex.getMessage(), Alert.AlertType.ERROR);

      System.out.println("Encryption: openFileTemporarily (2) -> error = " + ex);
      ex.printStackTrace();
    }
  }


  /**
   * Opens encrypted file by decrypting it temporarily to disk, and deletes decrypted file once app is closed
   * @param fileMetadata EncryptedFileMetadata object of the main encrypted file
   * @throws IOException
   */
  public static void openFileTemporarilyByDecrypting(EncryptedFileMetadata fileMetadata) throws IOException {
    File encryptedFile = fileMetadata.getEncryptedFile(); // path to encrypted file
    File decryptedFile = File.createTempFile(RandomStringUtils.randomAlphanumeric(3), "." + fileMetadata.getFileFormat());

    try {
      CryptoUtils.decrypt(fileMetadata.getSecretKey(), encryptedFile, decryptedFile);
      System.out.println("Encryption: openFileTemporarily (1) -> Opening as " + decryptedFile.getAbsolutePath());
      Desktop.getDesktop().open(decryptedFile); // open the decrypted file
    } catch(CryptoException ex) {
      String msg = "";
      if(!encryptedFile.exists()) msg = "File not found!";
      else msg = "Error in decrypting file!";

      System.out.println("Encryption: decryptFile (2) -> ERROR: " + ex.getMessage());
      System.out.println("Encryption: decryptFile (3) -> ERROR: " + msg);
      ex.printStackTrace();

      // display the error dialog box
      Alert errAlert = new Alert(Alert.AlertType.ERROR);
      errAlert.setHeaderText(msg + "\nPath: " + fileMetadata.getEncryptedFilePath());
      errAlert.showAndWait();

      // TODO: to ask user to remove entry for the error file from the list
    } finally {
      decryptedFile.deleteOnExit(); // delete the decrypted file after the app has been closed
    }
  }
}

