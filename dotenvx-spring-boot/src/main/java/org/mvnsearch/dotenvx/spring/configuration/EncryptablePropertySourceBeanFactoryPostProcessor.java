package org.mvnsearch.dotenvx.spring.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.NonNull;

/**
 * <p>EncryptablePropertySourceBeanFactoryPostProcessor class.</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EncryptablePropertySourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private final ConfigurableEnvironment env;

    /**
     * <p>Constructor for EncryptablePropertySourceBeanFactoryPostProcessor.</p>
     *
     * @param env a {@link org.springframework.core.env.ConfigurableEnvironment} object
     */
    public EncryptablePropertySourceBeanFactoryPostProcessor(ConfigurableEnvironment env) {
        this.env = env;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
