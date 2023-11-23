// This class provides abstraction for app's settings with the help of basic APIs.


package com.soterians.securitron.Utils;

import com.soterians.securitron.Utils.CryptoClasses.EncryptedFileMetadata;
import org.h2.jdbc.JdbcSQLInvalidAuthorizationSpecException;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * Class that provides basic APIs for managing the app's internal settings.
 * (Singleton class with only one possible object in whole application's lifecycle.)
 * Usage: SettingsManager smgr = SettingsManager.getInstance();
 */
public class DatabaseManager {
  private static DatabaseManager single_instance = null;  // object of the settings manager class
  private static String db_password = null; // database password. Gets updated in runtime
  private static final String db_user = "root";  // database username
  private static final String db_name = "securitron";  // name of the database file
  private static String db_path = null; // stores the path to the database

  private static final String tableName = "files";  // name of the table that will contain file list

  private static final String tableCreationCmd = "CREATE TABLE " + tableName + " (file_path VARCHAR(max), fileSize BIGINT, encryptedFile VARCHAR(max), checksum VARCHAR(64), encryptedOn BIGINT, secret_key VARCHAR(24));";

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
   * Creates insertion statement from EncryptedFileMetadata object
   * @param fileMetadata EncryptedFileMetadata object containing information about the encrypted file
   * @return insertion sql query
   */
  public static String insertCmdFromEncryptedFileMetadata(EncryptedFileMetadata fileMetadata) {
    String cmd = "INSERT INTO files VALUES (" +
            "'" + fileMetadata.getFilePath() + "', " +
            fileMetadata.getFileSize() + ", " +
            "'" + fileMetadata.getEncryptedFilePath() + "', " +
            "'" + fileMetadata.getChecksum() + "', " +
            fileMetadata.getEncryptedOn().getTime() + ", " +
            "'" + fileMetadata.getSecretKey() + "'" +
            ");";
    return cmd;
  }


  /**
   * Converts result set object into ArrayList&lt;EncryptedFileMetadata&gt; object
   * @param res ResultSet object
   * @return ArrayList&lt;EncryptedFileMetadata&gt; object with all predefined values
   * @throws SQLException
   */
  public static ArrayList<EncryptedFileMetadata> getEncryptedFileListFromResultSet(ResultSet res) throws SQLException {
    ArrayList<EncryptedFileMetadata> fileMetadataList = new ArrayList<>();
    while(res.next()) {
      EncryptedFileMetadata fileMetadata = new EncryptedFileMetadata(
              new File(res.getString("file_path")),
              res.getString("checksum"),
              new Date(res.getLong("encryptedOn")),
              res.getLong("fileSize"),
              new File(res.getString("encryptedFile")),
              res.getString("secret_key")
      );
      fileMetadataList.add(fileMetadata);
    }

    return fileMetadataList;
  }


  /**
   * display contents of arraylist just for debugging purposes
   * @param list
   */
  private static void printList(ArrayList<EncryptedFileMetadata> list) {
    for(EncryptedFileMetadata i: list) {
      System.out.println(i.getFile());
      System.out.println(i.getFileSize());
      System.out.println(i.getEncryptedFile());
      System.out.println(i.getChecksum());
      System.out.println(i.getEncryptedOn());
      System.out.println(i.getSecretKey());
      System.out.println();
    }
    System.out.println();
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
   * Returns the JDBC connection URL for the database
   * @return JDBC connection URL string
   */
  private static String getJdbcURL() {
    return "jdbc:h2:" + db_path; // jdbc path
  }


  /**
   * Checks if database is already present
   * @return true if database is present, otherwise false
   * @throws IOException
   */
  public static boolean isDBPresent() {
    try {
      db_path = Paths.get(getDBParentPath().toString(), db_name).toString();
      File db = new File(db_path + ".mv.db");
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

    String jdbcUrl = getJdbcURL();
    System.out.println("DatabaseManager: initializeDB (2) -> jdbcUrl = " + jdbcUrl);

    // create database
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, pswd)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: initializeDB (3) -> connection established, creating table.");
        stmt.execute(tableCreationCmd);
      }
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: initializeDB (4) -> error " + ex);
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

    String jdbcUrl = getJdbcURL();
    System.out.println("DatabaseManager: isPasswordCorrect (2) -> jdbcUrl = " + jdbcUrl);

    // try connecting to the database and run a query
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, pswd)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: isPasswordCorrect (3) -> connection established");
        DatabaseManager.db_password = pswd; // set the database password
        return true;
      }
    } catch(JdbcSQLInvalidAuthorizationSpecException ex) {
      System.out.println("DatabaseManager: isPasswordCorrect (4) -> incorrect username/password = " + ex);
      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: isPasswordCorrect (5) -> error " + ex);
      ex.printStackTrace();
    }

    return false;
  }


  /**
   * Changes the database password
   * @param oldPswd old password of the database
   * @param newPswd new password to be set
   * @return true if password was changed successfully, otherwise false
   */
  public static boolean changeDBPassword(String oldPswd, String newPswd) {
    String jdbcUrl = getJdbcURL();

    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, oldPswd)) {
      try(final Statement stmt = conn.createStatement()) {
        stmt.execute("alter user " + db_user + " set password '" + newPswd + "'");
        DatabaseManager.db_password = newPswd;  // update the new password to the class variable
        return true;
      }

    } catch(JdbcSQLInvalidAuthorizationSpecException ex) {
      System.out.println("Invalid username or password");
    } catch(Exception ex) {
      System.out.println("exception occured: " + ex);
      ex.printStackTrace();
    }

    return false;
  }


  /**
   * Inserts the file metadata list into the database
   * @param fileList ArrayList&lt;EncryptedFileMetadata&gt; object containing data to be inserted
   */
  public static void insertEncryptedFileData(ArrayList<EncryptedFileMetadata> fileList) {
    String jdbcUrl = getJdbcURL();

    // try connecting to the database and run query to insert data
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, db_password)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: insertEncryptedFileData (1) -> connection established, inserting data");

        // run insertion query for all EncryptedFileMetadata object
        for(EncryptedFileMetadata fileMetadata: fileList)
          stmt.execute(insertCmdFromEncryptedFileMetadata(fileMetadata));
      }
    } catch(JdbcSQLInvalidAuthorizationSpecException ex) {
      System.out.println("DatabaseManager: insertEncryptedFileData (2) -> incorrect username/password = " + ex);
      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: insertEncryptedFileData (3) -> error " + ex);
      ex.printStackTrace();
    } catch(Exception ex) {
      System.out.println("DatabaseManager: insertEncryptedFileData (4) -> error " + ex);
      ex.printStackTrace();
    }
  }


  /**
   * Deletes a file metadata from the database
   * @param fileMetadata EncryptedFileMetadata object
   */
  public static void deleteEncryptedFileData(EncryptedFileMetadata fileMetadata) {
    String jdbcUrl = getJdbcURL();

    // try connecting to the database and run query to insert data
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, db_password)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: deleteEncryptedFileData (1) -> connection established, deleting data");

        // run deletion query for the EncryptedFileMetadata object
        stmt.execute("DELETE FROM " + tableName + " WHERE file_path = '" + fileMetadata.getFilePath() + "';");

        System.out.println("DatabaseManager: deleteEncryptedFileData (2) -> new data after deletion");
        ResultSet res = stmt.executeQuery("SELECT * FROM files;");
        printList(getEncryptedFileListFromResultSet(res));
      }
    } catch(JdbcSQLInvalidAuthorizationSpecException ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (3) -> incorrect username/password = " + ex);
      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (4) -> error " + ex);
      ex.printStackTrace();
    } catch(Exception ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (5) -> error " + ex);
      ex.printStackTrace();
    }
  }


  /**
   * Reads file metadata list from the database
   * @return ArrayList&lt;EncryptedFileMetadata&gt; object containing data retrieved
   */
  public static ArrayList<EncryptedFileMetadata> readEncryptedFileData() {
    String jdbcUrl = getJdbcURL();

    ArrayList<EncryptedFileMetadata> fileList = null;

    // try connecting to the database and run query to insert data
    try(final Connection conn = DriverManager.getConnection(jdbcUrl, db_user, db_password)) {
      try(final Statement stmt = conn.createStatement()) {
        System.out.println("DatabaseManager: deleteEncryptedFileData (1) -> connection established");

        // run retrieval query for the EncryptedFileMetadata object list
        ResultSet res = stmt.executeQuery("SELECT * FROM files;");
        fileList = getEncryptedFileListFromResultSet(res);
        printList(fileList);
      }
    } catch(JdbcSQLInvalidAuthorizationSpecException ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (2) -> incorrect username/password = " + ex);
      ex.printStackTrace();
    } catch(SQLException ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (3) -> error " + ex);
      ex.printStackTrace();
    } catch(Exception ex) {
      System.out.println("DatabaseManager: deleteEncryptedFileData (4) -> error " + ex);
      ex.printStackTrace();
    }

    return fileList;
  }

}
