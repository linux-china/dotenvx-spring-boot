package org.mvnsearch.dotenvx.spring.encryptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mvnsearch.dotenvx.spring.configuration.DotenvxEncryptorBuilder;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mvnsearch.dotenvx.spring.util.Functional.tap;

/**
 * Default Lazy Encryptor that delegates to a custom {@link DotenvxEncryptor} bean or creates a default {@link DotenvxEncryptorImpl}
 * based on what properties are provided
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class DefaultLazyEncryptor implements DotenvxEncryptor {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultLazyEncryptor.class);
    private final Singleton<DotenvxEncryptor> singleton;

    /**
     * <p>Constructor for DefaultLazyEncryptor.</p>
     *
     * @param env                     {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param customEncryptorBeanName {@link java.lang.String} object
     * @param isCustom                boolean
     * @param bf                      {@link org.springframework.beans.factory.BeanFactory} object
     */
    public DefaultLazyEncryptor(final ConfigurableEnvironment env, final String customEncryptorBeanName, boolean isCustom, final BeanFactory bf) {
        singleton = new Singleton<>(() ->
                Optional.of(customEncryptorBeanName)
                        .filter(bf::containsBean)
                        .map(name -> (DotenvxEncryptor) bf.getBean(name))
                        .map(tap(bean -> log.info("Found Custom Encryptor Bean {} with name: {}", bean, customEncryptorBeanName)))
                        .orElseGet(() -> {
                            if (isCustom) {
                                throw new IllegalStateException(String.format("String Encryptor custom Bean not found with name '%s'", customEncryptorBeanName));
                            }
                            log.info("String Encryptor custom Bean not found with name '{}'. Initializing Default String Encryptor", customEncryptorBeanName);
                            return createDefault(env);
                        }));
    }

    /**
     * <p>Constructor for DefaultLazyEncryptor.</p>
     *
     * @param env {@link org.springframework.core.env.ConfigurableEnvironment} object
     */
    public DefaultLazyEncryptor(final ConfigurableEnvironment env) {
        singleton = new Singleton<>(() -> createDefault(env));
    }

    private DotenvxEncryptor createDefault(ConfigurableEnvironment env) {
        final HashMap<String, String> globalKeyStore = getGlobalKeyStore();
        String publicKeyHex = env.getProperty("dotenv.public.key", String.class);
        String privateKeyHex = env.getProperty("dotenv.private.key", String.class);
        if (privateKeyHex == null) {
            privateKeyHex = System.getenv("DOTENV_PRIVATE_KEY");
        }
        // get private key from global key pairs if public key is provided
        if (publicKeyHex != null && privateKeyHex == null) {
            privateKeyHex = globalKeyStore.get(publicKeyHex);
        }
        if (privateKeyHex == null) {
            privateKeyHex = readPrivateKeyFromKeysFile("DOTENV_PRIVATE_KEY");
        }
        DotenvxEncryptorBuilder builder = new DotenvxEncryptorBuilder();
        builder.withPrimaryKeyPair(publicKeyHex, privateKeyHex);
        // get private keys for profile: public -> private
        for (String activeProfile : env.getActiveProfiles()) {
            String profilePublicKey = env.getProperty("dotenv.public.key." + activeProfile, String.class);
            String profilePrivateKey = env.getProperty("dotenv.private.key." + activeProfile, String.class);
            if (privateKeyHex == null) {
                privateKeyHex = System.getenv("DOTENV_PRIVATE_KEY_" + activeProfile.toUpperCase());
            }
            // get private key from global key pairs if public key is provided
            if (profilePublicKey != null && profilePrivateKey == null) {
                profilePrivateKey = globalKeyStore.get(profilePublicKey);
            }
            // read private key from .env.keys file if not found
            if (profilePrivateKey == null) {
                profilePrivateKey = readPrivateKeyFromKeysFile("DOTENV_PRIVATE_KEY_" + activeProfile.toUpperCase());
            }
            if (profilePublicKey != null && profilePrivateKey != null) {
                builder.withProfileKeyPair(profilePublicKey, profilePrivateKey);
            }
        }
        return builder.build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encrypt(final String message) {
        return singleton.get().encrypt(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String decrypt(final String encryptedMessage) {
        return singleton.get().decrypt(encryptedMessage);
    }

    private HashMap<String, String> getGlobalKeyStore() {
        HashMap<String, String> globalKeyPairs = new HashMap<>();
        // read global key pairs from $HOME/.dotenvx/.env.keys.json
        final Path globalEnvKeysPath = Path.of(System.getProperty("user.home"), ".dotenvx", ".env.keys.json");
        if (globalEnvKeysPath.toFile().exists()) {
            try {
                Map<String, Object> keyStore = objectMapper.readValue(globalEnvKeysPath.toFile(), Map.class);
                if (!keyStore.isEmpty()) {
                    if (keyStore.containsKey("version") && keyStore.containsKey("keys")) {
                        keyStore = (Map<String, Object>) keyStore.get("keys");
                    }
                    for (String publicKey : keyStore.keySet()) {
                        Object pair = keyStore.get(publicKey);
                        if (pair instanceof Map<?, ?>) {
                            String privateKye = ((Map<?, ?>) pair).get("private_key").toString();
                            globalKeyPairs.put(publicKey, privateKye);
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return globalKeyPairs;
    }

    public String readPrivateKeyFromKeysFile(String privateKeyEnvName) {
        String privateKey = null;
        try {
            if (Files.exists(Paths.get(".env.keys"))) { // Check in the current directory
                for (String line : Files.readAllLines(Paths.get(".env.keys"))) {
                    if (line.startsWith(privateKeyEnvName + "=")) {
                        privateKey = line.substring((privateKeyEnvName + "=").length()).trim();
                        break;
                    }
                }
            } else if (Files.exists(Paths.get(System.getProperty("user.home"), ".env.keys"))) { // Check in the user's home directory
                for (String line : Files.readAllLines(Paths.get(System.getProperty("user.home"), ".env.keys"))) {
                    if (line.startsWith(privateKeyEnvName + "=")) {
                        privateKey = line.substring((privateKeyEnvName + "=").length()).trim();
                        break;
                    }
                }
            }
        } catch (Exception ignore) {

        }
        if (privateKey != null && (privateKey.startsWith("\"") || privateKey.startsWith("'"))) {
            privateKey = privateKey.substring(1, privateKey.length() - 1);
        }
        return privateKey;
    }

}
