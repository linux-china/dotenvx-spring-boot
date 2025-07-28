package org.mvnsearch.dotenvx.spring.encryptor;


import org.mvnsearch.dotenvx.spring.exception.DecryptionException;
import org.mvnsearch.dotenvx.spring.exception.EncryptionException;

public interface DotenvxEncryptor {

    /**
     * encrypt text using dotenvx encryptor
     *
     * @param text text to encrypt
     * @return base64 encoded encrypted text with the `encrypted:` prefix
     */
    String encrypt(String text) throws EncryptionException;

    /**
     * decrypt text using dotenvx encryptor
     *
     * @param base64EncodedText base64 encoded encrypted text with the `encrypted:` prefix
     * @return decrypted text
     */
    String decrypt(String base64EncodedText) throws DecryptionException;
}
