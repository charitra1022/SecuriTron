package com.soterians.securitron.Utils.CryptoClasses;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;

public class Encryption {
  private static final String extension = ".securitron";
  private static final String key = "090801----------";   // 16 bytes fixed size (to be changed later)

  private static boolean deleteOriginal = false;  // delete all original files after encryption


  /**
   * Displays an Alert DialogPane to ask for user's consent to delete original files after encryption is done
   * @param contextString Filename of the file just encrypted
   * @return ButtonType object that user selected - YES, NO, APPLY (Yes to All)
   */
  private static ButtonType showDeleteAlert(String contextString) {
    // Alert for user confirmation for deleting original files
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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

    // store basic info before encrypting
    EncryptedFileMetadata fileMetadata = new EncryptedFileMetadata(inputFile, encryptedFile);

    // encrypt the file
    try {
      CryptoUtils.encrypt(key, inputFile, encryptedFile); // encrypt the input file
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

    // save the EncryptedFileMetadata objects to disk
    ManageEncryptedFileList.saveEncryptedFileMetaData(fileMetadataList);

    // reset the default deletion value
    deleteOriginal = false;
  }


  /**
   * Decrypts the encrypted file and deletes it from the disk
   * @param fileMetadata EncryptedFileMetadata object of the encrypted file
   */
  public static void decryptFile(EncryptedFileMetadata fileMetadata) {
    System.out.println("Encryption: decryptFile (1) -> " + fileMetadata);

    // create respective file objects
    File encryptedFile = fileMetadata.getEncryptedFile();
    File decryptedFile = fileMetadata.getFile();

    // TODO: if a file with same name as decrypting file is present, ask user consent to delete or rename new file

    // decrypt the file
    try {
      CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
      System.out.println("Encryption: decryptFile (2) -> Decrypted: " + decryptedFile.getAbsolutePath());
      encryptedFile.delete(); // delete the encrypted file after it is decrypted
      ManageEncryptedFileList.removeFileEntryFromList(fileMetadata); // remove the file entry from list.json

      // TODO: remove the file entry from the list and update the listView
    } catch (CryptoException ex) {
      String msg = "";
      if(!encryptedFile.exists()) msg = "File not found!";
      else msg = "Error in decrypting file!";

      System.out.println("Encryption: decryptFile (3) -> ERROR: " + ex.getMessage());
      System.out.println("Encryption: decryptFile (4) -> ERROR: " + msg);
      ex.printStackTrace();

      // display the error dialog box
      Alert errAlert = new Alert(Alert.AlertType.ERROR);
      errAlert.setHeaderText(msg + "\nPath: " + fileMetadata.getEncryptedFilePath());
      errAlert.showAndWait();

      // TODO: to ask user to remove entry for the error file from the list
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Open file without decrypting to the main location
   * @param fileMetadata EncryptedFileMetadata object of the main encrypted file
   * @throws IOException
   */
  public static void openFileTemporarily(EncryptedFileMetadata fileMetadata) throws IOException {
    File encryptedFile = fileMetadata.getEncryptedFile(); // path to encrypted file
    File decryptedFile = File.createTempFile(RandomStringUtils.randomAlphanumeric(3), "." + fileMetadata.getFileFormat());

    try {
      CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
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

