package com.soterians.securitron.Utils.CryptoClasses;

import com.soterians.securitron.Utils.CryptoClasses.EncryptedFileMetadata;
import org.json.JSONArray;
import org.json.JSONException;
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
   * Saves the list of encrypted files as a JSON file in the disk for later retrieval
   * @param encryptedFileMetadataList Array&lt;EncryptedFileMetadata&gt; object
   * @throws IOException
   */
  public static void saveEncryptedFileMetaData(ArrayList<EncryptedFileMetadata> encryptedFileMetadataList) throws IOException {
    // retrieve JSONArray object from stored json data add new data to it
    JSONArray jsonArray = readJSONArrayFromListFile();
    for(int i=0; i<encryptedFileMetadataList.size(); i++)
      jsonArray.put(createJSONObjectFromEncryptedFileMetadata(encryptedFileMetadataList.get(i)));

    System.out.println("ManageEncryptedFileList: saveEncryptedFileMetaData -> " + jsonArray.toString(2));

    // create a fileWriter object and place the string representation of the JSONArray
    FileWriter fileWriter = new FileWriter(getEncryptedFilesListPath().toString());
    jsonArray.write(fileWriter, 2, 2);
    fileWriter.close();

    // ------- IMPORTANT: to append incoming changes to already present list, and check for duplicate entries!!!!!
  }


  /**
   * Reads the json file and returns the JSONArray parsed from it
   * @return JSONArray object parsed from the json file
   * @throws IOException
   */
  public static JSONArray readJSONArrayFromListFile() throws IOException {
    // read the file contents
    BufferedReader reader = Files.newBufferedReader(getEncryptedFilesListPath());
    String line = reader.readLine();
    StringBuilder fileContents = new StringBuilder();
    while(line != null) {
      fileContents.append(line);
      line = reader.readLine();
    }
    reader.close();

    // if the file is a valid json file, return its JSONArray object
    if(isValidJsonArray(fileContents.toString()))
      return new JSONArray(fileContents.toString());

    // if the file is not a valid json file, delete it
    getEncryptedFilesListPath().toFile().delete();
    return new JSONArray("[]");
  }


  /**
   * Checks if the provided string is a valid JSON string
   * @param string string to be checked
   * @return true if string is a valid JSONArray
   */
  public static boolean isValidJsonArray(String string) {
    try{
      // if its possible to make a JSONArray object, its a valid json file
      new JSONArray(string);
      return true;
    } catch (JSONException err) {
      return false;
    }
  }


  /**
   * Reads the json file of list of encrypted files and returns the list of EncryptedFileMetadata objects
   * @return ArrayList&lt;EncryptedFileMetadata&gt; object that contains list of EncryptedFileMetadata objects
   * @throws IOException
   */
  public static ArrayList<EncryptedFileMetadata> readEncryptedFileMetaData() throws IOException {
    JSONArray jsonArray = readJSONArrayFromListFile();
    System.out.println("ManageEncryptedFileList: readEncryptedFileMetaData (1) -> " + jsonArray);

    // create array of EncryptedFileMetadata objects from JSONObjects
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    try {
      for(int i=0; i<jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        EncryptedFileMetadata fileMetadata = createEncryptedFileMetadataFromJSONObject(jsonObject);
        fileMetadataList.add(fileMetadata);
        System.out.println("ManageEncryptedFileList: readEncryptedFileMetaData (2) -> " + jsonObject);
      }
    } catch (Exception err) {
      new File(getEncryptedFilesListPath().toString()).delete();
    }

    return fileMetadataList;
  }


  /**
   * Creates EncryptedFileMetadata object from provided JSONObject
   * @param jsonObject JSONObject that contains information read from the json file containing list of encrypted files
   * @return EncryptedFileMetadata object parsed from the JSONObject
   * @throws ParseException
   */
  public static EncryptedFileMetadata createEncryptedFileMetadataFromJSONObject(JSONObject jsonObject) throws ParseException {
    return new EncryptedFileMetadata(
            new File(jsonObject.get("path").toString()),
            jsonObject.get("checksum").toString(),
            (new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH)).parse(jsonObject.get("encryptedOn").toString()),
            Long.parseLong(jsonObject.get("fileSize").toString()),
            new File(jsonObject.get("encryptedFile").toString())
    );
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
    jsonObject.put("fileSize", fileMetadata.getFileSize());
    jsonObject.put("encryptedFile", fileMetadata.getEncryptedFilePath());
    return jsonObject;
  }


  // method to verify if a file in the list exists and is valid or not
  // better solution: to add a property in the json file itself to mark validity of the file
  // alternate: to check validity only if the file is selected in the listiew to reduce redundant calls.
  public static boolean doesFileExist(ArrayList<EncryptedFileMetadata> encryptedFileMetadataList, int index){ return true; }
}
