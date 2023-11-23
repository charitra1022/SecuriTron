package com.soterians.securitron.Utils.CryptoClasses;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

// TODO: to use file locking while encrypting/decrypting


/**
 * Custom exception for the CryptoUtils class
 */
class CryptoException extends Exception{
    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}


/**
 * Manages the internals of encryption and decryption in Java SDK with provided key, input/output files and algorithm
 */
public class CryptoUtils{
    private static final String ALGORITHM = "AES", TRANSFORMATION = "AES";

    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }


  /**
   * Reads an encrypted file and returns its decrypted contents in the form of byte array, and return it
   * @param key Encryption key of the file
   * @param inputFile input file
   * @return byte array containing the decrypted contents
   * @throws CryptoException
   */
  public static byte[] readEncryptedData(String keyString, File inputFile) throws CryptoException {
        try {
            SecretKey secretKey = stringToKey(keyString);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // read the input file
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            inputStream.close();

            return cipher.doFinal(inputBytes);  // return decrypted bytes
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException |
                IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    private static void doCrypto(int cipherMode, String keyString, File inputFile, File outputFile) throws CryptoException {
        // to implement encryption of large files using chunks of data
        try {
            Key secretKey = stringToKey(keyString);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);   // inputstream for inputfile
            FileOutputStream outputStream = new FileOutputStream(outputFile);   // outputstream for output file

            byte dataBytes[] = new byte[1024];  // byte chunk
            int bytesRead = 0;  // number of bytes read at a time

            // loop until complete file is read
            while ((bytesRead = inputStream.read(dataBytes)) != -1) {
                outputStream.write(cipher.update(dataBytes, 0, bytesRead));
            }

            outputStream.write(cipher.doFinal());   // do the final encoding

            inputStream.close();    // close inputstream
            outputStream.close();   // close outputstream
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }


    /**
     * Generates a key and returns String representation of the SecreKey object so formed
     * @return String containing key. (size -> 24 chars)
     * @throws NoSuchAlgorithmException
     */
    public static String generateKeyString() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secRan = new SecureRandom();
        keyGen.init(128, secRan);
        SecretKey key = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }


    /**
     * Converts string key to SecretKey object
     * @param key String containing key. (size -> 24 chars)
     * @return SecretKey object
     */
    private static SecretKey stringToKey(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
    }
}
