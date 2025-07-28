package org.mvnsearch.dotenvx.spring.detector;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyDetector;
import org.mvnsearch.dotenvx.spring.properties.DotenvxEncryptorConfigurationProperties;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Optional;

import static org.mvnsearch.dotenvx.spring.util.Functional.tap;

/**
 * Default Lazy property detector that delegates to a custom {@link EncryptablePropertyDetector} bean or initializes a
 * default {@link org.mvnsearch.dotenvx.spring.detector.DefaultPropertyDetector}.
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class DefaultLazyPropertyDetector implements EncryptablePropertyDetector {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DefaultLazyPropertyDetector.class);

    private final Singleton<EncryptablePropertyDetector> singleton;

    /**
     * <p>Constructor for DefaultLazyPropertyDetector.</p>
     *
     * @param environment            a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param customDetectorBeanName a {@link java.lang.String} object
     * @param isCustom               a boolean
     * @param bf                     a {@link org.springframework.beans.factory.BeanFactory} object
     */
    public DefaultLazyPropertyDetector(ConfigurableEnvironment environment, String customDetectorBeanName, boolean isCustom, BeanFactory bf) {
        singleton = new Singleton<>(() ->
                Optional.of(customDetectorBeanName)
                        .filter(bf::containsBean)
                        .map(name -> (EncryptablePropertyDetector) bf.getBean(name))
                        .map(tap(bean -> log.info("Found Custom Detector Bean {} with name: {}", bean, customDetectorBeanName)))
                        .orElseGet(() -> {
                            if (isCustom) {
                                throw new IllegalStateException(String.format("Property Detector custom Bean not found with name '%s'", customDetectorBeanName));
                            }
                            log.info("Property Detector custom Bean not found with name '{}'. Initializing Default Property Detector", customDetectorBeanName);
                            return createDefault(environment);
                        }));
    }

    private DefaultPropertyDetector createDefault(ConfigurableEnvironment environment) {
        DotenvxEncryptorConfigurationProperties props = DotenvxEncryptorConfigurationProperties.bindConfigProps(environment);
        return new DefaultPropertyDetector(props.getProperty().getPrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEncrypted(String property) {
        return singleton.get().isEncrypted(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String unwrapEncryptedValue(String property) {
        return singleton.get().unwrapEncryptedValue(property);
    }
}
