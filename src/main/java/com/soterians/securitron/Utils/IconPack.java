package com.soterians.securitron.Utils;

import com.soterians.securitron.MainApplication;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class IconPack {
  private HashMap<String, String> iconMap;  // map of icons mapped to their relative resource path

  private static IconPack iconPackInstance = null;  // singleton class instance


  /**
   * Instantiates the class and creates a map of all icons
   */
  private IconPack() {
    // add the file icons to the list
    iconMap = new HashMap<>();
    iconMap.put("audio", MainApplication.class.getResource("icons/audio.png").toExternalForm());
    iconMap.put("code", MainApplication.class.getResource("icons/code.png").toExternalForm());
    iconMap.put("compress", MainApplication.class.getResource("icons/compress.png").toExternalForm());
    iconMap.put("excel", MainApplication.class.getResource("icons/excel.png").toExternalForm());
    iconMap.put("folder", MainApplication.class.getResource("icons/folder.png").toExternalForm());
    iconMap.put("gif", MainApplication.class.getResource("icons/gif.png").toExternalForm());
    iconMap.put("link", MainApplication.class.getResource("icons/link.png").toExternalForm());
    iconMap.put("lock", MainApplication.class.getResource("icons/lock.png").toExternalForm());
    iconMap.put("pdf", MainApplication.class.getResource("icons/pdf.png").toExternalForm());
    iconMap.put("picture", MainApplication.class.getResource("icons/picture.png").toExternalForm());
    iconMap.put("ppt", MainApplication.class.getResource("icons/ppt.png").toExternalForm());
    iconMap.put("psd", MainApplication.class.getResource("icons/psd.png").toExternalForm());
    iconMap.put("svg", MainApplication.class.getResource("icons/svg.png").toExternalForm());
    iconMap.put("txt", MainApplication.class.getResource("icons/txt.png").toExternalForm());
    iconMap.put("unknown", MainApplication.class.getResource("icons/unknown.png").toExternalForm());
    iconMap.put("unlock", MainApplication.class.getResource("icons/unlock.png").toExternalForm());
    iconMap.put("video", MainApplication.class.getResource("icons/video.png").toExternalForm());
    iconMap.put("word", MainApplication.class.getResource("icons/word.png").toExternalForm());
    iconMap.put("application", MainApplication.class.getResource("icons/application.png").toExternalForm());

    iconMap.put("dragdrop_grey", MainApplication.class.getResource("icons/dragdrop_grey.png").toExternalForm());
    iconMap.put("dragdrop_blue", MainApplication.class.getResource("icons/dragdrop_blue.png").toExternalForm());
  }


  /**
   * returns the class instance if present, otherwise creates new instance and returns it
   * @return IconPack instance
   */
  public static IconPack getInstance() {
    if (iconPackInstance == null) iconPackInstance = new IconPack();
    return iconPackInstance;
  }


  /**
   * Gets the Image object depending on the type of file format of the file
   * @param fileName File object
   * @return Image object containing the correct file icon
   */
  public Image getFileIconImage(File fileName) {
    String iconType = "unknown";    // default icon for all files
    try {
      String mimeType = Files.probeContentType(fileName.toPath());  // get the type of file
      System.out.println("IconPack: getIconImage -> " + mimeType);

      // check for known files
      if(mimeType == null) iconType = "unknown";
      else if (mimeType.contains("audio")) iconType = "audio";
      else if (mimeType.contains("text/plain")) iconType = "txt";
      else if (mimeType.contains("text")) iconType = "code";
      else if (mimeType.contains("zip") || mimeType.contains("compressed") || mimeType.contains("tar"))
        iconType = "compress";
      else if (mimeType.contains("gif")) iconType = "gif";
      else if (mimeType.contains("pdf")) iconType = "pdf";
      else if (mimeType.contains("svg")) iconType = "svg";
      else if (mimeType.contains("image")) iconType = "picture";
      else if(mimeType.contains("powerpoint") || mimeType.contains("presentation")) iconType = "ppt";
      else if (mimeType.contains("psd") || mimeType.contains("postscript")) iconType = "psd";
      else if (mimeType.contains("video")) iconType = "video";
      else if(mimeType.contains("msword") || mimeType.contains("wordprocessingml")) iconType = "word";
      else if(mimeType.contains("ms-excel") || mimeType.contains("spreadsheet")) iconType = "excel";
      else if(mimeType.contains("application")) iconType = "application";
      else iconType = "unknown";

      // to add folder, link, code files
    } catch (IOException err) {}  // use default file icon if exception occurs

    Image image = new Image(iconMap.get(iconType)); // create image object
    return image;
  }


  /**
   * Returns the icon by direct icon name
   * @param icon name of icon
   * @return Image object containing the icon
   */
  public Image getIcon(String icon) {
    String iconURL = iconMap.get(icon);
    System.out.println("IconPack: getIcon -> " + iconURL);
    return new Image(iconURL);
  }
}
