package org.mvnsearch.dotenvx.spring.encryptor;

import org.mvnsearch.dotenvx.ecies.Ecies;
import org.mvnsearch.dotenvx.spring.exception.DecryptionException;
import org.mvnsearch.dotenvx.spring.exception.EncryptionException;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * dotenvx encryptor implementation
 *
 * @author linux_china
 */
public class DotenvxEncryptorImpl implements DotenvxEncryptor {
    private final String publicKeyHex;
    private final String privateKeyHex;
    private final HashMap<String, String> profileKeyPairs;

    public DotenvxEncryptorImpl(@Nullable String publicKeyHex, @Nullable String privateKeyHex, HashMap<String, String> profileKeyPairs) {
        this.publicKeyHex = publicKeyHex;
        this.privateKeyHex = privateKeyHex;
        this.profileKeyPairs = profileKeyPairs;
    }

    /**
     * encrypt text using dotenvx encryptor
     *
     * @param text text to encrypt
     * @return base64 encoded encrypted text with the `encrypted:` prefix
     * @throws EncryptionException encryption exception if encryption fails
     */
    @Override
    public String encrypt(String text) throws EncryptionException {
        try {
            return "encrypted:" + Ecies.encrypt(publicKeyHex, text);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt text: " + text, e);
        }
    }

    /**
     * decrypt text using dotenvx encryptor
     *
     * @param base64EncodedText base64 encoded encrypted text with the `encrypted:` prefix if applicable
     * @return decrypted text
     * @throws DecryptionException descryption exception if decryption fails
     */
    @Override
    public String decrypt(String base64EncodedText) throws DecryptionException {
        try {
            if (base64EncodedText.startsWith("encrypted:")) {
                base64EncodedText = base64EncodedText.substring("encrypted:".length());
            }
            return Ecies.decrypt(privateKeyHex, base64EncodedText);
        } catch (Exception ignore) {
        }
        for (Map.Entry<String, String> keyPair : profileKeyPairs.entrySet()) {
            try {
                String privateKey = keyPair.getValue();
                if (base64EncodedText.startsWith("encrypted:")) {
                    base64EncodedText = base64EncodedText.substring("encrypted:".length());
                }
                return Ecies.decrypt(privateKey, base64EncodedText);
            } catch (Exception ignore) {
            }
        }
        throw new DecryptionException("Failed to decrypt text: " + base64EncodedText);
    }
}
