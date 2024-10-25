package com.soterians.securitron.Utils.CryptoClasses;

import com.soterians.securitron.UI.CustomDialogs;
import com.soterians.securitron.UI.PreviewImageWindow;
import com.soterians.securitron.UI.PreviewTextWindow;
import com.soterians.securitron.Utils.DatabaseManager;
import com.soterians.securitron.Utils.IconPack;

import com.soterians.securitron.Utils.SHA256Checksum;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;

public class Encryption {
  private static final String extension = ".securitron";

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

    String secret_key = CryptoUtils.generateKeyString(); // key of size 16 bytes

    // store basic info before encrypting
    EncryptedFileMetadata fileMetadata = new EncryptedFileMetadata(inputFile, encryptedFile, secret_key);

    // encrypt the file
    try {
      CryptoUtils.encrypt(secret_key, inputFile, encryptedFile); // encrypt the input file
      System.out.println("Encryption: encryptFile -> Encrypted: " + encryptedFile.getAbsolutePath());

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
    boolean deleteDefault = false;  // delete all files after encryption

    // create list of EncryptedFileMetadata objects
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    for (File f : files) {
      fileMetadataList.add(encryptFile(f));

      // if delete for all files is enabled, direct delete
      if (deleteDefault) f.delete();

      // otherwise ask for user confirmation
      else {
        ButtonType result = showDeleteAlert(f.getName());  // show confirmation

        // delete only current file
        if(result.getButtonData() == ButtonType.YES.getButtonData())
          f.delete();     // delete original file after encryption

        // delete all consequent files
        else if(result.getButtonData() == ButtonType.APPLY.getButtonData()){
          f.delete();     // delete original file after encryption
          deleteDefault = true;
        }
      }
    }

    // save the list to the database
    DatabaseManager.insertEncryptedFileData(fileMetadataList);
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

      // verify the file checksum
      String newChecksum = SHA256Checksum.getFileChecksum(decryptedFile); // checksum of new file
      if(!newChecksum.equals(fileMetadata.getChecksum())) {
        CustomDialogs.showAlertDialog("SecuriTron: File Corrupt!", "Seems like the file was changed after encryption!\nThe decrypted file doesn't match the original file!", Alert.AlertType.WARNING);
      } else {
        encryptedFile.delete(); // delete the encrypted file after it is decrypted
        DatabaseManager.deleteEncryptedFileData(fileMetadata);  // delete the file info from the database
      }

      ButtonType btnPressed = CustomDialogs.showAlertDialog("SecuriTron: Decryption done!", "File decrypted: " + fileMetadata.getFileName() + "\nDo you want to view it?", Alert.AlertType.CONFIRMATION);
      if(btnPressed == ButtonType.OK) Desktop.getDesktop().open(decryptedFile);

      return true;
    } catch (CryptoException ex) {
      String msg = "";
      if(!encryptedFile.exists()) msg = "File not found!";
      else msg = "Error in decrypting file!";

      System.out.println("Encryption: decryptFile (3) -> ERROR: " + ex.getMessage());
      System.out.println("Encryption: decryptFile (4) -> ERROR: " + msg);
      ex.printStackTrace();

      // display the error dialog box
      CustomDialogs.showAlertDialog("SecuriTron: " + msg, "Path: " + fileMetadata.getEncryptedFilePath(), Alert.AlertType.ERROR);

      ButtonType result = CustomDialogs.showAlertDialog("SecuriTron: Remove entry?", "Do you want to remove this file from the disk and the app list?", Alert.AlertType.CONFIRMATION);
      if(result == ButtonType.OK) {
        encryptedFile.delete(); // delete the encrypted file after it is decrypted
        DatabaseManager.deleteEncryptedFileData(fileMetadata);  // delete the file info from the database
        return true;
      }
    } catch(IOException e) {
      throw new RuntimeException(e);
    } catch(NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
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

        // handle textual files in a different window with editing features
        if(mimeType.contains("text")) {
          PreviewTextWindow textWindow = new PreviewTextWindow(fileMetadata);
          Stage textWindowStage = textWindow.getStage();
          textWindowStage.showAndWait();
          return;
        }

        // read the file contents in form of byte array
        FileInputStream fileInputStream = new FileInputStream(fileMetadata.getEncryptedFile());
        byte[] dataBytes = CryptoUtils.readEncryptedData(
                fileMetadata.getSecretKey(),
                fileMetadata.getEncryptedFile(),
                fileInputStream
        );

        PreviewImageWindow imageWindow = new PreviewImageWindow(fileMetadata, dataBytes);
        Stage imageWindowStage = imageWindow.getStage();
        imageWindowStage.showAndWait();

        // close stream of encrypted file after preview is closed so that file is locked until preview is closed
        fileInputStream.close();
        return;
      }

      ButtonType btnPressed = CustomDialogs.showAlertDialog(
              "SecuriTron: Unsupported File",
              "Selected file cannot be opened without decrypting!\nOnly images and text files can be opened!\n\n" +
                      "Do you want to view it by temporarily decrypting it to disk?\nThis option can make the file vulnerable to unauthorised access, so choose wisely!",
              Alert.AlertType.CONFIRMATION
      );
      if(btnPressed == ButtonType.OK) openFileTemporarilyByDecrypting(fileMetadata);

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

