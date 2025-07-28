package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.EncryptablePropertySourceConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Configuration class that registers a {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor} that wraps all {@link org.springframework.core.env.PropertySource} defined in the {@link org.springframework.core.env.Environment}
 * with {@link org.mvnsearch.dotenvx.spring.wrapper.EncryptablePropertySourceWrapper} and defines a default {@link org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor} for decrypting properties
 * that can be configured through the same properties it wraps.</p>
 *
 * @author Ulises Bocchio
 */
@Configuration
@Import({EncryptablePropertyResolverConfiguration.class, CachingConfiguration.class})
public class EnableEncryptablePropertiesConfiguration {

    /**
     * <p>enableEncryptablePropertySourcesPostProcessor.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param converter   a {@link EncryptablePropertySourceConverter} object
     * @return a {@link org.mvnsearch.dotenvx.spring.configuration.EnableEncryptablePropertiesBeanFactoryPostProcessor} object
     */
    @Bean
    public static EnableEncryptablePropertiesBeanFactoryPostProcessor enableEncryptablePropertySourcesPostProcessor(final ConfigurableEnvironment environment, EncryptablePropertySourceConverter converter) {
        return new EnableEncryptablePropertiesBeanFactoryPostProcessor(environment, converter);
    }
}
