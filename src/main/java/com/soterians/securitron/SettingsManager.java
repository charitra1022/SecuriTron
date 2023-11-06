// This class provides abstraction for app's settings with the help of basic APIs.


package com.soterians.securitron;

/**
 * Class that provides basic APIs for managing the app's internal settings.
 * (Singleton class with only one possible object in whole application's lifecycle.)
 * Usage: SettingsManager smgr = SettingsManager.getInstance();
 */
public class SettingsManager {
  private static SettingsManager single_instance = null;  // object of the settings manager class

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
}
