// This class provides abstraction for app's settings with the help of basic APIs.


package com.soterians.securitron.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that provides basic APIs for managing the app's internal settings.
 * (Singleton class with only one possible object in whole application's lifecycle.)
 * Usage: SettingsManager smgr = SettingsManager.getInstance();
 */
public class SettingsManager {
  private static SettingsManager single_instance = null;  // object of the settings manager class
  private static String db_password = null; // database password. Gets updated in runtime
  private static final String db_name = "securitron.db";  // name of the database file

  /**
   * Constructor of the class that initiates basic operations of the settings for the app
   */
  private SettingsManager() {}


  /**
   * Function that returns the object of the settings manager class. if object not present, creates it
   * @return object of SettingsManager class
   */
  public static SettingsManager getInstance() {
    // if the object is not present, create and store it
    if(single_instance == null) single_instance = new SettingsManager();
    return single_instance;
  }


  /**
   * Creates the Roaming folder for the app if not already present and returns the path to it
   * @return path to the folder where the database is present
   * @throws IOException
   */
  public static Path getDBParentPath() throws IOException {
    String appdataFolder = System.getenv("APPDATA");  // path to the  roaming directory
    String appName = "SecuriTron";  // folder name of our application

    Path dbParentPath = Paths.get(appdataFolder, appName); // path to our app folder in roaming
    Files.createDirectories(dbParentPath);  // create the folder
    return dbParentPath;
  }


  /**
   * Checks if database is already present
   * @return true if database is present, otherwise false
   * @throws IOException
   */
  public static boolean isDBPresent() {
    Path dbPath = null;
    try {
      dbPath = Paths.get(getDBParentPath().toString(), db_name);
      File db = new File(String.valueOf(dbPath));
      return db.exists();
    } catch(IOException e) {
      return false;
    }
  }
}
