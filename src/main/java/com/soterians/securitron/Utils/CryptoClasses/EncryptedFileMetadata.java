package com.soterians.securitron.Utils.CryptoClasses;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.soterians.securitron.Utils.SHA256Checksum;
import org.apache.commons.io.FilenameUtils;

public class EncryptedFileMetadata {
  private File file, encryptedFile;
  private String checksum;
  private Date encryptedOn;
  private String fileFormat;
  private long fileSize;
  private String secret_key;


  /**
   * Creates an object with all pre-defined values. used for retrieving data from stored place, e.g., JSON
   * @param file File object for original file
   * @param checksum checksum value for the original file
   * @param encryptedOn date when the original file was encrypted on
   * @param encryptedFile File object for encrypted file
   */
  public EncryptedFileMetadata(File file, String checksum, Date encryptedOn, long fileSize, File encryptedFile, String secret_key) {
    this.file = file;
    this.checksum = checksum;
    this.encryptedOn = encryptedOn;
    this.fileSize = fileSize;
    this.fileFormat = calculateFileFormat(file);
    this.encryptedFile = encryptedFile;
    this.secret_key = secret_key;
  }


  /**
   * Creates an object with only original file information. used for initial process of encryption.
   * Automatically initiates encryptedOn and checksum attributes
   * @param originalFile File object for original file
   * @param encryptedFile File object for encrypted file
   * @param secret_key Secret key string used to encrypt the file
   */
  public EncryptedFileMetadata(File originalFile, File encryptedFile, String secret_key) throws NoSuchAlgorithmException, IOException {
    this.file = originalFile;
    this.checksum = SHA256Checksum.getFileChecksum(originalFile);
    this.encryptedOn = new Date();
    this.fileSize = file.length();
    this.fileFormat = calculateFileFormat(originalFile);
    this.encryptedFile = encryptedFile;
    this.secret_key = secret_key;
  }

  public String getSecretKey() { return secret_key; }

  public Date getEncryptedOn() {
    return encryptedOn;
  }

  public File getFile() {
    return file;
  }

  public File getEncryptedFile() {
    return encryptedFile;
  }

  public String getChecksum() {
    return checksum;
  }

  public String getFileFormat() {
    return fileFormat;
  }

  public long getFileSize() {
    return fileSize;
  }

  public String getFileSizeString() {
    double size = (double)getFileSize();

    // size in bytes
    if(size < 1024) return size + " bytes";

    // size in kilobytes
    size /= 1024;
    if(size < 1024) return (Math.floor(size * 100) / 100.0) + "KB";

    // size in megabytes
    size /= 1024;
    if(size < 1024) return (Math.floor(size * 100) / 100.0) + "MB";

    // size in gigabytes
    size /= 1024;
    return (Math.floor(size * 100) / 100.0) + "GB";
  }

  public String getFileName() {
    return file.getName();
  }

  public String getFilePath() {
    return file.getAbsolutePath();
  }

  public String getEncryptedFilePath() {
    return encryptedFile.getAbsolutePath();
  }


  @Override
  public String toString() {
    return this.getFileName();
  }


  /**
   * Returns the file extension for a file
   * @param file File object for the file to be processed
   * @return String containing the file extension
   */
  public String calculateFileFormat(File file) {
    return FilenameUtils.getExtension(file.getName());
  }


  public void printAllData() {
    System.out.println("file: " + file);
    System.out.println("encryptedFile: " + encryptedFile);
    System.out.println("fileSize: " + fileSize);
    System.out.println("fileFormat: " + fileFormat);
    System.out.println("encryptedOn: " + encryptedOn);
    System.out.println("checksum: " + checksum);
    System.out.println("secret_key: " + secret_key);
    System.out.println();
  }
}
