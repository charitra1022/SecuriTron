package com.soterians.securitron.Utils.CryptoClasses;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
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
  public static EncryptedFileMetadata encryptFile(File inputFile) {
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
  public static void encryptFiles(ArrayList<File> files) throws IOException, ParseException {
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
}
