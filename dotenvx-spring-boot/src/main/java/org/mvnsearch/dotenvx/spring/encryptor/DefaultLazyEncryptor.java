package org.mvnsearch.dotenvx.spring.encryptor;

import org.mvnsearch.dotenvx.spring.configuration.DotenvxEncryptorBuilder;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;

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
        String publicKeyHex = env.getProperty("dotenv.public.key", String.class);
        String privateKeyHex = env.getProperty("dotenv.private.key", String.class);
        if (privateKeyHex == null) {
            privateKeyHex = System.getenv("DOTENV_PRIVATE_KEY");
        }
        return new DotenvxEncryptorBuilder(publicKeyHex, privateKeyHex).build();
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

}
