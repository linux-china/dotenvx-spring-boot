package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.*;
import org.mvnsearch.dotenvx.spring.detector.DefaultLazyPropertyDetector;
import org.mvnsearch.dotenvx.spring.encryptor.DefaultLazyEncryptor;
import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor;
import org.mvnsearch.dotenvx.spring.filter.DefaultLazyPropertyFilter;
import org.mvnsearch.dotenvx.spring.properties.DotenvxEncryptorConfigurationProperties;
import org.mvnsearch.dotenvx.spring.resolver.DefaultLazyPropertyResolver;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>EncryptablePropertyResolverConfiguration class.</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
@Configuration
public class EncryptablePropertyResolverConfiguration {

    private static final String ENCRYPTOR_BEAN_PROPERTY = "dotenvx.encryptor.bean";
    private static final String ENCRYPTOR_BEAN_PLACEHOLDER = String.format("${%s:dotenvxEncryptor}", ENCRYPTOR_BEAN_PROPERTY);
    private static final String DETECTOR_BEAN_PROPERTY = "dotenvx.encryptor.property.detector-bean";
    private static final String DETECTOR_BEAN_PLACEHOLDER = String.format("${%s:encryptablePropertyDetector}", DETECTOR_BEAN_PROPERTY);
    private static final String RESOLVER_BEAN_PROPERTY = "dotenvx.encryptor.property.resolver-bean";
    private static final String RESOLVER_BEAN_PLACEHOLDER = String.format("${%s:encryptablePropertyResolver}", RESOLVER_BEAN_PROPERTY);
    private static final String FILTER_BEAN_PROPERTY = "dotenvx.encryptor.property.filter-bean";
    private static final String FILTER_BEAN_PLACEHOLDER = String.format("${%s:encryptablePropertyFilter}", FILTER_BEAN_PROPERTY);

    private static final String ENCRYPTOR_BEAN_NAME = "lazyDotenvxEncryptor";
    private static final String DETECTOR_BEAN_NAME = "lazyEncryptablePropertyDetector";
    private static final String CONFIG_SINGLETON = "configPropsSingleton";
    /**
     * Constant <code>RESOLVER_BEAN_NAME="lazyEncryptablePropertyResolver"</code>
     */
    public static final String RESOLVER_BEAN_NAME = "lazyEncryptablePropertyResolver";
    /**
     * Constant <code>FILTER_BEAN_NAME="lazyEncryptablePropertyFilter"</code>
     */
    public static final String FILTER_BEAN_NAME = "lazyEncryptablePropertyFilter";

    /**
     * <p>encryptablePropertySourceConverter.</p>
     *
     * @param environment      a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param propertyResolver a {@link EncryptablePropertyResolver} object
     * @param propertyFilter   a {@link EncryptablePropertyFilter} object
     * @return a {@link EncryptablePropertySourceConverter} object
     */
    @SuppressWarnings("unchecked")
    @Bean
    public static EncryptablePropertySourceConverter encryptablePropertySourceConverter(ConfigurableEnvironment environment, @Qualifier(RESOLVER_BEAN_NAME) EncryptablePropertyResolver propertyResolver, @Qualifier(FILTER_BEAN_NAME) EncryptablePropertyFilter propertyFilter) {
        final boolean proxyPropertySources = environment.getProperty("dotenvx.encryptor.proxy-property-sources", Boolean.TYPE, false);
        final List<String> skipPropertySources = (List<String>) environment.getProperty("dotenvx.encryptor.skip-property-sources", List.class, Collections.EMPTY_LIST);
        final List<Class<PropertySource<?>>> skipPropertySourceClasses = skipPropertySources.stream().map(EncryptablePropertySourceConverter::getPropertiesClass).collect(Collectors.toList());
        final InterceptionMode interceptionMode = proxyPropertySources ? InterceptionMode.PROXY : InterceptionMode.WRAPPER;
        return new EncryptablePropertySourceConverter(interceptionMode, skipPropertySourceClasses, propertyResolver, propertyFilter);
    }

    /**
     * <p>envCopy.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @return a {@link org.mvnsearch.dotenvx.spring.configuration.EnvCopy} object
     */
    @Bean
    public EnvCopy envCopy(final ConfigurableEnvironment environment) {
        return new EnvCopy(environment);
    }

    @Bean(name = ENCRYPTOR_BEAN_NAME)
    public DotenvxEncryptor dotenvxEncryptor(
            final EnvCopy envCopy,
            final BeanFactory bf) {
        final String customEncryptorBeanName = envCopy.get().resolveRequiredPlaceholders(ENCRYPTOR_BEAN_PLACEHOLDER);
        final boolean isCustom = envCopy.get().containsProperty(ENCRYPTOR_BEAN_PROPERTY);
        return new DefaultLazyEncryptor(envCopy.get(), customEncryptorBeanName, isCustom, bf);
    }

    /**
     * <p>encryptablePropertyDetector.</p>
     *
     * @param envCopy a {@link org.mvnsearch.dotenvx.spring.configuration.EnvCopy} object
     * @param bf      a {@link org.springframework.beans.factory.BeanFactory} object
     * @return a {@link EncryptablePropertyDetector} object
     */
    @Bean(name = DETECTOR_BEAN_NAME)
    public EncryptablePropertyDetector encryptablePropertyDetector(
            final EnvCopy envCopy,
            final BeanFactory bf) {
        final String customDetectorBeanName = envCopy.get().resolveRequiredPlaceholders(DETECTOR_BEAN_PLACEHOLDER);
        final boolean isCustom = envCopy.get().containsProperty(DETECTOR_BEAN_PROPERTY);
        return new DefaultLazyPropertyDetector(envCopy.get(), customDetectorBeanName, isCustom, bf);
    }

    /**
     * <p>configProps.</p>
     *
     * @param envCopy a {@link org.mvnsearch.dotenvx.spring.configuration.EnvCopy} object
     * @return a {@link org.mvnsearch.dotenvx.spring.util.Singleton} object
     */
    @Bean(name = CONFIG_SINGLETON)
    public Singleton<DotenvxEncryptorConfigurationProperties> configProps(
            final EnvCopy envCopy) {
        return new Singleton<>(() -> DotenvxEncryptorConfigurationProperties.bindConfigProps(envCopy.get()));
    }

    /**
     * <p>encryptablePropertyFilter.</p>
     *
     * @param envCopy a {@link org.mvnsearch.dotenvx.spring.configuration.EnvCopy} object
     * @param bf      a {@link org.springframework.beans.factory.config.ConfigurableBeanFactory} object
     * @return a {@link EncryptablePropertyFilter} object
     */
    @Bean(name = FILTER_BEAN_NAME)
    public EncryptablePropertyFilter encryptablePropertyFilter(
            final EnvCopy envCopy,
            final ConfigurableBeanFactory bf) {
        final String customFilterBeanName = envCopy.get().resolveRequiredPlaceholders(FILTER_BEAN_PLACEHOLDER);
        final boolean isCustom = envCopy.get().containsProperty(FILTER_BEAN_PROPERTY);
        return new DefaultLazyPropertyFilter(envCopy.get(), customFilterBeanName, isCustom, bf);
    }

    /**
     * <p>encryptablePropertyResolver.</p>
     *
     * @param propertyDetector a {@link EncryptablePropertyDetector} object
     * @param encryptor        a {@link DotenvxEncryptor} object
     * @param bf               a {@link org.springframework.beans.factory.BeanFactory} object
     * @param envCopy          a {@link org.mvnsearch.dotenvx.spring.configuration.EnvCopy} object
     * @param environment      a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @return a {@link EncryptablePropertyResolver} object
     */
    @Bean(name = RESOLVER_BEAN_NAME)
    public EncryptablePropertyResolver encryptablePropertyResolver(
            @Qualifier(DETECTOR_BEAN_NAME) final EncryptablePropertyDetector propertyDetector,
            @Qualifier(ENCRYPTOR_BEAN_NAME) final DotenvxEncryptor encryptor, final BeanFactory bf,
            final EnvCopy envCopy, final ConfigurableEnvironment environment) {
        final String customResolverBeanName = envCopy.get().resolveRequiredPlaceholders(RESOLVER_BEAN_PLACEHOLDER);
        final boolean isCustom = envCopy.get().containsProperty(RESOLVER_BEAN_PROPERTY);
        return new DefaultLazyPropertyResolver(propertyDetector, encryptor, customResolverBeanName, isCustom, bf, environment);
    }

}
