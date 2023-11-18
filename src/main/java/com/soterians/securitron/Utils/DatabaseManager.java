// This class provides abstraction for app's settings with the help of basic APIs.


package com.soterians.securitron.Utils;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Objects;

/**
 * Class that provides basic APIs for managing the app's internal settings.
 * (Singleton class with only one possible object in whole application's lifecycle.)
 * Usage: SettingsManager smgr = SettingsManager.getInstance();
 */
public class DatabaseManager {
  private static DatabaseManager single_instance = null;  // object of the settings manager class
  private static String db_password = null; // database password. Gets updated in runtime
  private static final String db_name = "securitron.db";  // name of the database file
  private static String db_path = null; // stores the path to the database

  /**
   * Constructor of the class that initiates basic operations of the settings for the app
   */
  private DatabaseManager() {}


  /**
   * Function that returns the object of the settings manager class. if object not present, creates it
   * @return object of SettingsManager class
   */
  public static DatabaseManager getInstance() {
    // if the object is not present, create and store it
    if(single_instance == null) single_instance = new DatabaseManager();
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
    try {
      db_path = Paths.get(getDBParentPath().toString(), db_name).toString();
      File db = new File(db_path);
      return db.exists();
    } catch(IOException e) {
      return false;
    }
  }


  /**
   * Creates the securitron database
   * @param pswd String containing password to be set to the database
   */
  public static void initializeDB(String pswd) {
    DatabaseManager.db_password = pswd; // set the database password
    System.out.println("DatabaseManager: initializeDB (1) -> db_path = " + db_path);

    String jdbcUrl = "jdbc:sqlite:/" + db_path; // jdbc path
    System.out.println("DatabaseManager: initializeDB (2) -> jdbcUrl = " + jdbcUrl);

    // create database
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, "", pswd)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: initializeDB (3) -> connection established, password = " + pswd);
        stmt.execute("CREATE TABLE files (path TEXT(100), key TEXT(100));");
        stmt.execute("INSERT INTO files VALUES ('xyz.txt', 'KDFLN867YB');");
        ResultSet res = stmt.executeQuery("SELECT * FROM files;");
        while(res.next()) System.out.println(res.getString("path") + " --> " + res.getString("key"));
      }
    } catch(SQLiteException ex) {
      System.out.println("DatabaseManager: initializeDB (4) -> error " + ex);
      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: initializeDB (5) -> error " + ex);
      ex.printStackTrace();
    }
  }


  /**
   * Checks if the supplied password is correct
   * @param pswd String containing password
   * @return true if password is correct, false if password is incorrect
   */
  public static boolean isPasswordCorrect(String pswd) {
    // if password has been updated previously by any other process
    if(db_password != null) return Objects.equals(pswd, db_password);

    System.out.println("DatabaseManager: isPasswordCorrect (1) -> db_path = " + db_path);

    String jdbcUrl = "jdbc:sqlite:/" + db_path; // jdbc path
    System.out.println("DatabaseManager: isPasswordCorrect (2) -> jdbcUrl = " + jdbcUrl);

    // try connecting to the database and run a query
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, "", pswd)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: isPasswordCorrect (3) -> connection established");
        ResultSet res = stmt.executeQuery("SELECT * FROM files;");
        DatabaseManager.db_password = pswd; // set the database password
        while(res.next()) System.out.println(res.getString("path") + " --> " + res.getString("key"));
        return true;
      }
    } catch(SQLiteException ex) {
      System.out.println("DatabaseManager: isPasswordCorrect (4) -> error = " + ex);

      if(ex.getResultCode() == SQLiteErrorCode.SQLITE_NOTADB)
        System.out.println("DatabaseManager: isPasswordCorrect (5) -> incorrect password");
      else System.out.println("DatabaseManager: isPasswordCorrect (5) -> something different");

      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: isPasswordCorrect (5) -> error " + ex);
      ex.printStackTrace();
    }

    return false;
  }
}
