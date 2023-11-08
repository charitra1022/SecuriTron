package com.soterians.securitron.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;

public class Encryption {
  private static final String extension = ".securitron";


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
//    try {
//      CryptoUtils.encrypt(key, inputFile, encryptedFile);
//      System.out.println("Encrypted: " + encryptedFile.getAbsolutePath());
//      inputFile.delete();     // delete original file after encryption
//    } catch (CryptoException ex) {
//      System.out.println(ex.getMessage());
//      ex.printStackTrace();
//      return false;
//    }
//    return true;

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
  }
}
