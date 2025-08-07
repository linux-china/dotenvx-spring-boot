package org.mvnsearch.dotenvx.spring.encryptor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Properties;

/**
 * dotenvx encryptor implementation test
 *
 * @author linux_china
 */
public class DotenvxEncryptorImplTest {
    private static DotenvxEncryptorImpl dotenvxEncryptor;

    @BeforeAll
    public static void setUp() throws Exception {
        String publicKeyHex = "02324d763b27358d4229651fd9d0822fb263b07bcc3422f5bd9968cafc194011ff";
        Properties properties = new Properties();
        properties.load(new FileReader(".env.keys"));
        String privateKeyHex = properties.getProperty("DOTENV_PRIVATE_KEY");
        dotenvxEncryptor = new DotenvxEncryptorImpl(publicKeyHex, privateKeyHex, new HashMap<>());
    }

    @Test
    public void testEncrypt() {
        String message = "Jackie";
        String encryptedText = dotenvxEncryptor.encrypt(message);
        System.out.println("Encrypted Text: " + encryptedText);
        String decryptedText = dotenvxEncryptor.decrypt(encryptedText);
        Assertions.assertEquals(message, decryptedText, "Decrypted text should match the original message");
    }

    @Test
    public void testDecrypt() {
        String base64EncodedText = "encrypted:BIVywnmpHgfABepOg1TMxSMGl705wQLutCf4fEVJoG6Zr/yARC2TjTAKHi9flPYh27Bbuv4rsnfYBRZkIm+y8isQSaJnvNAKrVKxWDh2v0fKpAPPSTry9qQVT4DIG9QVxunJfqtcbg=="; // Example base64 encoded text
        String decryptedText = dotenvxEncryptor.decrypt(base64EncodedText);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
