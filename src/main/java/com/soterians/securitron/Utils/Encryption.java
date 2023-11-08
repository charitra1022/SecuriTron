package com.soterians.securitron.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Encryption {

  /**
   * Creates EncryptedFileMetadata object for the File object passed
   * @param file File object
   * @return EncryptedFileMetadata object
   */
  public static EncryptedFileMetadata createEncryptedFileMetadataObject(File file) {
    // to write correct code for this
    EncryptedFileMetadata efm = new EncryptedFileMetadata(file, "", (new Date()), "");
    return efm;
  }


  /**
   * Encrypts the list of files passed
   * @param files
   */
  public static void encryptFiles(ArrayList<File> files) throws IOException, ParseException {
    // handle encryption logic

    // create list of EncryptedFileMetadata objects
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    for(int i=0; i<files.size(); i++) fileMetadataList.add(createEncryptedFileMetadataObject(files.get(i)));

    // save the EncryptedFileMetadata objects to disk
    ManageEncryptedFileList.saveEncryptedFileMetaData(fileMetadataList);
  }
}
