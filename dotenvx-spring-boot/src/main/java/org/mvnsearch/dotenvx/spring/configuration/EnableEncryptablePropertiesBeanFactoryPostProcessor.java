package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.mvnsearch.dotenvx.spring.EncryptablePropertySourceConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.lang.NonNull;

/**
 * <p>{@link org.springframework.beans.factory.config.BeanFactoryPostProcessor} that wraps all {@link org.springframework.core.env.PropertySource} defined in the {@link org.springframework.core.env.Environment}
 * with {@link org.mvnsearch.dotenvx.spring.wrapper.EncryptablePropertySourceWrapper} and defines a default {@link
 * EncryptablePropertyResolver} for decrypting properties
 * that can be configured through the same properties it wraps.</p>
 * <p>It takes the lowest precedence so it does not interfere with Spring Boot's own post-processors</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EnableEncryptablePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private final ConfigurableEnvironment environment;
    private final EncryptablePropertySourceConverter converter;

    /**
     * <p>Constructor for EnableEncryptablePropertiesBeanFactoryPostProcessor.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param converter   a {@link EncryptablePropertySourceConverter} object
     */
    public EnableEncryptablePropertiesBeanFactoryPostProcessor(ConfigurableEnvironment environment, EncryptablePropertySourceConverter converter) {
        this.environment = environment;
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        MutablePropertySources propSources = environment.getPropertySources();
        converter.convertPropertySources(propSources);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
