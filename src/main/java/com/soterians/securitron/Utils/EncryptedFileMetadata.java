package com.soterians.securitron.Utils;

import java.io.File;
import java.util.Date;

public class EncryptedFileMetadata {
  private File file;
  private String checksum;
  private Date encryptedOn;
  private String fileFormat;

  EncryptedFileMetadata(File file, String checksum, Date encryptedOn, String fileFormat) {
    this.file = file;
    this.checksum = checksum;
    this.encryptedOn = encryptedOn;
    this.fileFormat = fileFormat;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public void setEncryptedOn(Date encryptedOn) {
    this.encryptedOn = encryptedOn;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public void setFileFormat(String fileFormat) {
    this.fileFormat = fileFormat;
  }

  public Date getEncryptedOn() {
    return encryptedOn;
  }

  public File getFile() {
    return file;
  }

  public String getChecksum() {
    return checksum;
  }

  public String getFileFormat() {
    return fileFormat;
  }

  public String getFileSizeString() {
    double size = (double)file.length();

    // size in bytes
    if(size < 1024) return size + " bytes";

    // size in megabytes
    size /= 1024;
    if(size < 1024) return (Math.floor(size * 100) / 100.0) + "MB";

    // size in gigabytes
    size /= 1024;
    return (Math.floor(size * 100) / 100.0) + "GB";
  }

  public long getFileSizeBytes() {
    return file.length();
  }

  public String getFileName() {
    return file.getName();
  }

  public String getFilePath() {
    return file.getAbsolutePath();
  }


  @Override
  public String toString() {
    return this.getFileName();
  }
}
