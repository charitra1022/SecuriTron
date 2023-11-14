package com.soterians.securitron.Utils;

import com.soterians.securitron.MainApplication;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public enum IconPack{
    AUDIO(new Image(MainApplication.class.getResource("icons/audio.png").toExternalForm())),
    CODE(new Image(MainApplication.class.getResource("icons/code.png").toExternalForm())),
    COMPRESS(new Image(MainApplication.class.getResource("icons/compress.png").toExternalForm())),
    EXCEL(new Image(MainApplication.class.getResource("icons/excel.png").toExternalForm())),
    FOLDER(new Image(MainApplication.class.getResource("icons/folder.png").toExternalForm())),
    GIF(new Image(MainApplication.class.getResource("icons/gif.png").toExternalForm())),
    LINK(new Image(MainApplication.class.getResource("icons/link.png").toExternalForm())),
    LOCK(new Image(MainApplication.class.getResource("icons/lock.png").toExternalForm())),
    PDF(new Image(MainApplication.class.getResource("icons/pdf.png").toExternalForm())),
    PICTURE(new Image(MainApplication.class.getResource("icons/picture.png").toExternalForm())),
    PPT(new Image(MainApplication.class.getResource("icons/ppt.png").toExternalForm())),
    PSD(new Image(MainApplication.class.getResource("icons/psd.png").toExternalForm())),
    SVG(new Image(MainApplication.class.getResource("icons/svg.png").toExternalForm())),
    TXT(new Image(MainApplication.class.getResource("icons/txt.png").toExternalForm())),
    UNKNOWN(new Image(MainApplication.class.getResource("icons/unknown.png").toExternalForm())),
    UNLOCK(new Image(MainApplication.class.getResource("icons/unlock.png").toExternalForm())),
    VIDEO(new Image(MainApplication.class.getResource("icons/video.png").toExternalForm())),
    WORD(new Image(MainApplication.class.getResource("icons/word.png").toExternalForm())),
    APPLICATION(new Image(MainApplication.class.getResource("icons/application.png").toExternalForm())),
    DRAG_DROP_GREY(new Image(MainApplication.class.getResource("icons/dragdrop_grey.png").toExternalForm())),
    DRAG_DROP_BLUE(new Image(MainApplication.class.getResource("icons/dragdrop_blue.png").toExternalForm()));

    private final Image enumValue;  // store the value of the enum


    /**
     * initialize the enum with custom values
     * @param enumValue
     */
    IconPack(Image enumValue) {
        this.enumValue = enumValue;
    }


    /**
     * returns the value of the enum
     * @return Image object
     */
    public Image getImage() {
        return enumValue;
    }


    /**
     * Accesses the File object and returns appropriate Image object according to file type
     * @param fileName File object
     * @return Image object
     */
    public static Image getFileIconImage(File fileName) {
        try {
            String mimeType = Files.probeContentType(fileName.toPath());  // get the type of file
            System.out.println("IconPack: getFileIconImage -> " + mimeType);

            // check for known files
            if(mimeType == null) return UNKNOWN.enumValue;
            else if(mimeType.contains("audio")) return AUDIO.enumValue;
            else if(mimeType.contains("text/plain")) return TXT.enumValue;
            else if(mimeType.contains("text")) return CODE.enumValue;
            else if(mimeType.contains("zip") || mimeType.contains("compressed") || mimeType.contains("tar"))
                return COMPRESS.enumValue;
            else if(mimeType.contains("gif")) return GIF.enumValue;
            else if(mimeType.contains("pdf")) return PDF.enumValue;
            else if(mimeType.contains("svg")) return SVG.enumValue;
            else if(mimeType.contains("image")) return PICTURE.enumValue;
            else if(mimeType.contains("powerpoint") || mimeType.contains("presentation")) return PPT.enumValue;
            else if(mimeType.contains("psd") || mimeType.contains("postscript")) return PSD.enumValue;
            else if(mimeType.contains("video")) return VIDEO.enumValue;
            else if(mimeType.contains("msword") || mimeType.contains("wordprocessingml"))
                return WORD.enumValue;
            else if(mimeType.contains("ms-excel") || mimeType.contains("spreadsheet")) return EXCEL.enumValue;
            else if(mimeType.contains("application")) return APPLICATION.enumValue;
            else return UNKNOWN.enumValue;

            // to add folder, link, code files
        } catch(IOException err) {}  // use default file icon if exception occurs
        return UNKNOWN.enumValue;
    }
}
