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
//            Path outPath = Paths.get("D:/Temp");
//            byte[] plainBuf = new byte[8192];
//
//            try (InputStream in = Files.newInputStream(f.toPath());
//                 OutputStream out = Files.newOutputStream(outPath)) {
//                int nread;
//                while ((nread = in.read(plainBuf)) > 0) {
//                    byte[] enc = cipher.update(plainBuf, 0, nread);
//                    out.write(enc);
//                }
//                byte[] enc = cipher.doFinal();
//                out.write(enc);
//            }
//            return outPath.toFile();

            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];

            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}
