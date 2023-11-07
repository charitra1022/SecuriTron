package com.soterians.securitron.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ManageEncryptedFileList {

  /**
   * Returns the Path object to the encrypted-files-list json file. If not present, create empty file
   * @return  Path to the encrypted-files-list json file
   * @throws IOException
   */
  public static Path getEncryptedFilesListPath() throws IOException {
    String appdataFolder = System.getenv("APPDATA");  // path to the  roaming directory
    String appName = "SecuriTron";  // folder name of our application
    String fileName = "list.json";  // filename of our encrypted files list

    Path filesListParentPath = Paths.get(appdataFolder, appName); // path to our app folder in roaming
    Path filesListPath = Paths.get(appdataFolder, appName, fileName); // path to the files list

    // if the file is not present in the system
    if(!(new File(filesListPath.toString())).exists()) {
      System.out.println("ManageEncryptedFileList: getEncryptedFilesListPath -> encrypted-files-list json file not found. Creating..");
      Files.createDirectories(filesListParentPath); // create the folders

      // create files list json file and write [] to it as contents
      FileOutputStream fos = new FileOutputStream(filesListPath.toString());
      fos.write("[]".getBytes());
      fos.close();
    }

    // return the Path to encrypted-files-list json file
    return filesListPath;
  }


  /**
   * Returns an arraylist of EncryptedFileMetadata after reading the encrypted-files-list json file.
   * @return ArrayList of EncryptedFileMetadata
   * @throws IOException
   */
  public static ArrayList<EncryptedFileMetadata> getEncryptedFilesList() throws IOException {
    ArrayList<EncryptedFileMetadata> metadataList = new ArrayList<>();

    Path fileListPath = getEncryptedFilesListPath();

    return  metadataList;
  }


  public static void saveEncryptedFileMetaData(ArrayList<EncryptedFileMetadata> encryptedFileMetadataList) {
    for(int i=0; i<encryptedFileMetadataList.size(); i++)
      System.out.println("ManageEncryptedFileList: saveEncryptedFileMetaData -> " + encryptedFileMetadataList.get(i).getFileName());
  }
}
