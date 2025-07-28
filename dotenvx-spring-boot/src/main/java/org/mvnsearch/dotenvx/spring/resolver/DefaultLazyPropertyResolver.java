package org.mvnsearch.dotenvx.spring.resolver;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyDetector;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;

import java.util.Optional;

import static org.mvnsearch.dotenvx.spring.util.Functional.tap;

/**
 * Default Resolver bean that delegates to a custom defined {@link EncryptablePropertyResolver} or creates a new {@link org.mvnsearch.dotenvx.spring.resolver.DefaultPropertyResolver}
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class DefaultLazyPropertyResolver implements EncryptablePropertyResolver {
    private static final Logger log = LoggerFactory.getLogger(DefaultLazyPropertyResolver.class);

    private final Singleton<EncryptablePropertyResolver> singleton;

    /**
     * <p>Constructor for DefaultLazyPropertyResolver.</p>
     *
     * @param propertyDetector       a {@link EncryptablePropertyDetector} object
     * @param encryptor              a {@link DotenvxEncryptor} object
     * @param customResolverBeanName a {@link java.lang.String} object
     * @param isCustom               a boolean
     * @param bf                     a {@link org.springframework.beans.factory.BeanFactory} object
     * @param environment            a {@link org.springframework.core.env.Environment} object
     */
    public DefaultLazyPropertyResolver(EncryptablePropertyDetector propertyDetector, DotenvxEncryptor encryptor, String customResolverBeanName, boolean isCustom, BeanFactory bf, Environment environment) {
        singleton = new Singleton<>(() ->
                Optional.of(customResolverBeanName)
                        .filter(bf::containsBean)
                        .map(name -> (EncryptablePropertyResolver) bf.getBean(name))
                        .map(tap(bean -> log.info("Found Custom Resolver Bean {} with name: {}", bean, customResolverBeanName)))
                        .orElseGet(() -> {
                            if (isCustom) {
                                throw new IllegalStateException(String.format("Property Resolver custom Bean not found with name '%s'", customResolverBeanName));
                            }
                            log.info("Property Resolver custom Bean not found with name '{}'. Initializing Default Property Resolver", customResolverBeanName);
                            return createDefault(propertyDetector, encryptor, environment);
                        }));
    }

    /**
     * <p>Constructor for DefaultLazyPropertyResolver.</p>
     *
     * @param propertyDetector a {@link EncryptablePropertyDetector} object
     * @param encryptor        a {@link DotenvxEncryptor} object
     * @param environment      a {@link org.springframework.core.env.Environment} object
     */
    public DefaultLazyPropertyResolver(EncryptablePropertyDetector propertyDetector, DotenvxEncryptor encryptor, Environment environment) {
        singleton = new Singleton<>(() -> createDefault(propertyDetector, encryptor, environment));
    }

    private DefaultPropertyResolver createDefault(EncryptablePropertyDetector propertyDetector, DotenvxEncryptor encryptor, Environment environment) {
        return new DefaultPropertyResolver(encryptor, propertyDetector, environment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resolvePropertyValue(String value) {
        return singleton.get().resolvePropertyValue(value);
    }
}
