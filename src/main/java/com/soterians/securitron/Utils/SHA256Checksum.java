package com.soterians.securitron.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Checksum {

  /**
   * Calculates the SHA-256 checksum of a file
   * @param file File object for which the checksum is to be calculated
   * @return String containing the checksum of the file
   * @throws NoSuchAlgorithmException thrown if an invalid checksum algorithm is used
   * @throws IOException thrown if a file is not found
   */
  public static String getFileChecksum(File file) throws NoSuchAlgorithmException, IOException {
    // get implementation of SHA-256 checksum
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

    FileInputStream fileInputStream = new FileInputStream(file);  // create file input stream to read file

    byte dataBytes[] = new byte[1024];  // store the bytes that are read from the file at a time
    int bytesRead = 0;  // number of bytes that are read at a time

    // loop until the file is read completely
    while ((bytesRead = fileInputStream.read(dataBytes)) != -1) {
      messageDigest.update(dataBytes, 0, bytesRead);  // update the object with the complete file
    }

    fileInputStream.close();  // close the filestream

    byte[] digestBytes = messageDigest.digest();  // compute digest of the file that is read
    messageDigest.reset();  // reset the messageDigest after the process

    return (new BigInteger(1, digestBytes)).toString(16); // convert the byte digest to string
  }
}
