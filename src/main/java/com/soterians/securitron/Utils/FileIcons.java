package com.soterians.securitron.Utils;

import com.soterians.securitron.MainApplication;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class FileIcons {
  private HashMap<String, String> fileIcons;  // map of icons mapped to their relative resource path

  private static FileIcons fileIconsInstance = null;  // singleton class instance


  /**
   * Instantiates the class and creates a map of all icons
   */
  private FileIcons() {
    // add the file icons to the list
    fileIcons = new HashMap<>();
    fileIcons.put("audio", MainApplication.class.getResource("icons/audio.png").toExternalForm());
    fileIcons.put("code", MainApplication.class.getResource("icons/code.png").toExternalForm());
    fileIcons.put("compress", MainApplication.class.getResource("icons/compress.png").toExternalForm());
    fileIcons.put("excel", MainApplication.class.getResource("icons/excel.png").toExternalForm());
    fileIcons.put("folder", MainApplication.class.getResource("icons/folder.png").toExternalForm());
    fileIcons.put("gif", MainApplication.class.getResource("icons/gif.png").toExternalForm());
    fileIcons.put("link", MainApplication.class.getResource("icons/link.png").toExternalForm());
    fileIcons.put("lock", MainApplication.class.getResource("icons/lock.png").toExternalForm());
    fileIcons.put("pdf", MainApplication.class.getResource("icons/pdf.png").toExternalForm());
    fileIcons.put("picture", MainApplication.class.getResource("icons/picture.png").toExternalForm());
    fileIcons.put("ppt", MainApplication.class.getResource("icons/ppt.png").toExternalForm());
    fileIcons.put("psd", MainApplication.class.getResource("icons/psd.png").toExternalForm());
    fileIcons.put("svg", MainApplication.class.getResource("icons/svg.png").toExternalForm());
    fileIcons.put("txt", MainApplication.class.getResource("icons/txt.png").toExternalForm());
    fileIcons.put("unknown", MainApplication.class.getResource("icons/unknown.png").toExternalForm());
    fileIcons.put("unlock", MainApplication.class.getResource("icons/unlock.png").toExternalForm());
    fileIcons.put("video", MainApplication.class.getResource("icons/video.png").toExternalForm());
    fileIcons.put("word", MainApplication.class.getResource("icons/word.png").toExternalForm());
  }


  /**
   * returns the class instance if present, otherwise creates new instance and returns it
   * @return FileIcons instance
   */
  public static FileIcons getInstance() {
    if (fileIconsInstance == null) fileIconsInstance = new FileIcons();
    return fileIconsInstance;
  }


  /**
   * Gets the Image object depending on the type of file format of the file
   * @param fileName File object
   * @return Image object containing the correct file icon
   */
  public Image getIconImage(File fileName) {
    String iconType = "unknown";    // default icon for all files
    try {
      String mimeType = Files.probeContentType(fileName.toPath());  // get the type of file
      System.out.println("FileIcons: getIconImage -> " + mimeType);

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
      else iconType = "unknown";

      // to add folder, link, code files
    } catch (IOException err) {}  // use default file icon if exception occurs

    Image image = new Image(fileIcons.get(iconType)); // create image object
    return image;
  }
}
