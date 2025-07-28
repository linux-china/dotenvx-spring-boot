package org.mvnsearch.dotenvx.spring.filter;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyFilter;
import org.mvnsearch.dotenvx.spring.properties.DotenvxEncryptorConfigurationProperties;
import org.mvnsearch.dotenvx.spring.util.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Optional;

import static org.mvnsearch.dotenvx.spring.util.Functional.tap;

/**
 * <p>DefaultLazyPropertyFilter class.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
public class DefaultLazyPropertyFilter implements EncryptablePropertyFilter {
    private static final Logger log = LoggerFactory.getLogger(DefaultLazyPropertyFilter.class);

    private final Singleton<EncryptablePropertyFilter> singleton;

    /**
     * <p>Constructor for DefaultLazyPropertyFilter.</p>
     *
     * @param e                    a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param customFilterBeanName a {@link java.lang.String} object
     * @param isCustom             a boolean
     * @param bf                   a {@link org.springframework.beans.factory.BeanFactory} object
     */
    public DefaultLazyPropertyFilter(ConfigurableEnvironment e, String customFilterBeanName, boolean isCustom, BeanFactory bf) {
        singleton = new Singleton<>(() ->
                Optional.of(customFilterBeanName)
                        .filter(bf::containsBean)
                        .map(name -> (EncryptablePropertyFilter) bf.getBean(name))
                        .map(tap(bean -> log.info("Found Custom Filter Bean {} with name: {}", bean, customFilterBeanName)))
                        .orElseGet(() -> {
                            if (isCustom) {
                                throw new IllegalStateException(String.format("Property Filter custom Bean not found with name '%s'", customFilterBeanName));
                            }

                            log.info("Property Filter custom Bean not found with name '{}'. Initializing Default Property Filter", customFilterBeanName);
                            return createDefault(e);
                        }));
    }

    private DefaultPropertyFilter createDefault(ConfigurableEnvironment environment) {
        DotenvxEncryptorConfigurationProperties props = DotenvxEncryptorConfigurationProperties.bindConfigProps(environment);
        final DotenvxEncryptorConfigurationProperties.PropertyConfigurationProperties.FilterConfigurationProperties filterConfig = props.getProperty().getFilter();
        return new DefaultPropertyFilter(filterConfig.getIncludeSources(), filterConfig.getExcludeSources(),
                filterConfig.getIncludeNames(), filterConfig.getExcludeNames());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldInclude(PropertySource<?> source, String name) {
        return singleton.get().shouldInclude(source, name);
    }
}
