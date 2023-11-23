package com.soterians.securitron.Utils.CryptoClasses;

/**
 * Custom exception for the CryptoUtils class
 */
public class CryptoException extends Exception{
  public CryptoException() {
  }

  public CryptoException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
