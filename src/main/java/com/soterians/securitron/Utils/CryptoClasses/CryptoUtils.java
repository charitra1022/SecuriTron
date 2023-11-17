package com.soterians.securitron.Utils.CryptoClasses;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

// TODO: to use file locking while encrypting/decrypting


/**
 * Custom exception for the CryptoUtils class
 */
class CryptoException extends Exception {
    public CryptoException() {}
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}


/**
 * Manages the internals of encryption and decryption in Java SDK with provided key, input/output files and algorithm
 */
public class CryptoUtils {
    private static final String ALGORITHM = "AES", TRANSFORMATION = "AES";

    public static void encrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }

    public static void decrypt(String key, File inputFile, File outputFile) throws CryptoException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException {
        // to implement encryption of large files using chunks of data
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);   // inputstream for inputfile
            FileOutputStream outputStream = new FileOutputStream(outputFile);   // outputstream for output file

            byte dataBytes[] = new byte[1024];  // byte chunk
            int bytesRead = 0;  // number of bytes read at a time

            // loop until complete file is read
            while((bytesRead = inputStream.read(dataBytes)) != -1) {
                outputStream.write(cipher.update(dataBytes, 0, bytesRead));
            }

            outputStream.write(cipher.doFinal());   // do the final encoding

            inputStream.close();    // close inputstream
            outputStream.close();   // close outputstream
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}
