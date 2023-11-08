package com.soterians.securitron.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

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

    System.out.println("ManageEncryptedFileList: getEncryptedFilesListPath -> " + filesListPath);

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


  /**
   * Saves the list of encrypted files as a JSON file in the disk for later retrieval
   * @param encryptedFileMetadataList Array&lt;EncryptedFileMetadata&gt; object
   * @throws IOException
   */
  public static void saveEncryptedFileMetaData(ArrayList<EncryptedFileMetadata> encryptedFileMetadataList) throws IOException, ParseException {
    // create a JSONArray object and add JSONObject objects to it
    JSONArray jsonArray = new JSONArray();
    for(int i=0; i<encryptedFileMetadataList.size(); i++)
      jsonArray.put(createJSONObjectFromEncryptedFileMetadata(encryptedFileMetadataList.get(i)));

    System.out.println("ManageEncryptedFileList: saveEncryptedFileMetaData -> " + jsonArray.toString(2));

    // create a fileWriter object and place the string representation of the JSONArray
    FileWriter fileWriter = new FileWriter(getEncryptedFilesListPath().toString());
    jsonArray.write(fileWriter, 2, 2);
    fileWriter.close();
  }


  public static ArrayList<EncryptedFileMetadata> readEncryptedFileMetaData() throws IOException, ParseException {
    // read the file contents
    BufferedReader reader = Files.newBufferedReader(getEncryptedFilesListPath());
    String line = reader.readLine();
    StringBuilder fileContents = new StringBuilder();
    while(line != null) {
      fileContents.append(line);
      line = reader.readLine();
    }

    JSONArray jsonArray = new JSONArray(fileContents.toString());

    System.out.println("ManageEncryptedFileList: readEncryptedFileMetaData (1) -> " + jsonArray);

    // create array of EncryptedFileMetadata objects from JSONObjects
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    for(int i=0; i<jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      EncryptedFileMetadata fileMetadata = createEncryptedFileMetadataFromJSONObject(jsonObject);
      fileMetadataList.add(fileMetadata);
      System.out.println("ManageEncryptedFileList: readEncryptedFileMetaData (2) -> " + jsonObject);
    }

    return fileMetadataList;
  }


  public static EncryptedFileMetadata createEncryptedFileMetadataFromJSONObject(JSONObject jsonObject) throws ParseException {
    EncryptedFileMetadata fileMetadata = new EncryptedFileMetadata(
            new File(jsonObject.get("path").toString()),
            jsonObject.get("checksum").toString(),
            (new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH)).parse(jsonObject.get("encryptedOn").toString()),
            jsonObject.get("fileFormat").toString()
    );

    return fileMetadata;
  }


  /**
   * Creates an equivalent JSONOject from the EncryptedFileMetadata object passesd
   * @param fileMetadata EncryptedFileMetadata object - contains important information about the encrypted file
   * @return JSONOject object for the corresponding EncryptedFileMetadata object
   */
  public static JSONObject createJSONObjectFromEncryptedFileMetadata(EncryptedFileMetadata fileMetadata){
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("path", fileMetadata.getFilePath());
    jsonObject.put("checksum", fileMetadata.getChecksum());
    jsonObject.put("encryptedOn", fileMetadata.getEncryptedOn().toString());
    jsonObject.put("fileFormat", fileMetadata.getFileFormat());
    jsonObject.put("fileSize", fileMetadata.getFileSizeBytes());
    return jsonObject;
  }


  // method to verify if a file in the list exists and is valid or not
  // better solution: to add a property in the json file itself to mark validity of the file
  // alternate: to check validity only if the file is selected in the listiew to reduce redundant calls.
  public static boolean doesFileExist(ArrayList<EncryptedFileMetadata> encryptedFileMetadataList, int index){ return true; }
}
