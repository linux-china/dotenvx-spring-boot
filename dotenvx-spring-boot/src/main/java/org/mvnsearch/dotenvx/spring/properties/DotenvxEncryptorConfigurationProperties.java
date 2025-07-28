package org.mvnsearch.dotenvx.spring.properties;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyDetector;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyFilter;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Partially used to load {@link EncryptablePropertyFilter} config.
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties(prefix = "dotenvx.encryptor", ignoreUnknownFields = true)
public class DotenvxEncryptorConfigurationProperties {

    /**
     * <p>bindConfigProps.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @return a {@link DotenvxEncryptorConfigurationProperties} object
     */
    public static DotenvxEncryptorConfigurationProperties bindConfigProps(ConfigurableEnvironment environment) {
        final BindHandler handler = new IgnoreErrorsBindHandler(BindHandler.DEFAULT);
        final MutablePropertySources propertySources = environment.getPropertySources();
        final Binder binder = new Binder(ConfigurationPropertySources.from(propertySources),
                new PropertySourcesPlaceholdersResolver(propertySources),
                ApplicationConversionService.getSharedInstance());
        final DotenvxEncryptorConfigurationProperties config = new DotenvxEncryptorConfigurationProperties();

        final ResolvableType type = ResolvableType.forClass(DotenvxEncryptorConfigurationProperties.class);
        final Annotation annotation = AnnotationUtils.findAnnotation(DotenvxEncryptorConfigurationProperties.class,
                ConfigurationProperties.class);
        final Annotation[] annotations = new Annotation[]{annotation};
        final Bindable<?> target = Bindable.of(type).withExistingValue(config).withAnnotations(annotations);

        binder.bind("dotenvx.encryptor", target, handler);
        return config;
    }

    /**
     * Whether to use JDK/Cglib (depending on classpath availability) proxy with an AOP advice as a decorator for
     * existing {@link org.springframework.core.env.PropertySource} or just simply use targeted wrapper Classes. Default
     * Value is {@code false}.
     */
    private Boolean proxyPropertySources = false;

    /**
     * Define a list of {@link org.springframework.core.env.PropertySource} to skip from wrapping/proxying. Properties held
     * in classes on this list will not be eligible for decryption. Default Value is {@code empty list}.
     */
    private List<String> skipPropertySources = Collections.emptyList();

    /**
     * Specify the name of bean to override dotenvx-spring-boot's default properties based
     * {@link org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor}. Default Value is {@code dotenvxEncryptor}.
     */
    private String bean = "dotenvxEncryptor";


    @NestedConfigurationProperty
    private PropertyConfigurationProperties property = new PropertyConfigurationProperties();


    public static class PropertyConfigurationProperties {

        /**
         * Specify the name of the bean to be provided for a custom
         * {@link EncryptablePropertyDetector}. Default value is
         * {@code "encryptablePropertyDetector"}
         */
        private String detectorBean = "encryptablePropertyDetector";

        /**
         * Specify the name of the bean to be provided for a custom
         * {@link EncryptablePropertyResolver}. Default value is
         * {@code "encryptablePropertyResolver"}
         */
        private String resolverBean = "encryptablePropertyResolver";

        /**
         * Specify the name of the bean to be provided for a custom {@link EncryptablePropertyFilter}. Default value is
         * {@code "encryptablePropertyFilter"}
         */
        private String filterBean = "encryptablePropertyFilter";

        /**
         * Specify a custom {@link String} to identify as prefix of encrypted properties. Default value is
         * {@code "ENC("}
         */
        private String prefix = "encrypted:";

        public String getPrefix() {
            return prefix;
        }

        public FilterConfigurationProperties getFilter() {
            return filter;
        }

        @NestedConfigurationProperty
        private FilterConfigurationProperties filter = new FilterConfigurationProperties();

        public static class FilterConfigurationProperties {

            /**
             * Specify the property sources name patterns to be included for decryption
             * by{@link EncryptablePropertyFilter}. Default value is {@code null}
             */
            private List<String> includeSources = null;

            /**
             * Specify the property sources name patterns to be EXCLUDED for decryption
             * by{@link EncryptablePropertyFilter}. Default value is {@code null}
             */
            private List<String> excludeSources = null;

            /**
             * Specify the property name patterns to be included for decryption by{@link EncryptablePropertyFilter}.
             * Default value is {@code null}
             */
            private List<String> includeNames = null;

            /**
             * Specify the property name patterns to be EXCLUDED for decryption by{@link EncryptablePropertyFilter}.
             * Default value is {@code dotenvx\\.encryptor\\.*}
             */
            private List<String> excludeNames = singletonList("^dotenvx\\.encryptor\\.*");

            public List<String> getIncludeSources() {
                return includeSources;
            }

            public List<String> getExcludeSources() {
                return excludeSources;
            }

            public List<String> getIncludeNames() {
                return includeNames;
            }

            public List<String> getExcludeNames() {
                return excludeNames;
            }
        }
    }


    public PropertyConfigurationProperties getProperty() {
        return property;
    }
}

