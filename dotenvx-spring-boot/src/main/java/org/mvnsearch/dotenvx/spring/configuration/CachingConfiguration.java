package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.EncryptablePropertySourceConverter;
import org.mvnsearch.dotenvx.spring.caching.RefreshScopeRefreshedEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>CachingConfiguration class.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
@Configuration
public class CachingConfiguration {
    /**
     * <p>refreshScopeRefreshedEventListener.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param converter   a {@link EncryptablePropertySourceConverter} object
     * @return a {@link org.mvnsearch.dotenvx.spring.caching.RefreshScopeRefreshedEventListener} object
     */
    @Bean
    public RefreshScopeRefreshedEventListener refreshScopeRefreshedEventListener(ConfigurableEnvironment environment, EncryptablePropertySourceConverter converter) {
        return new RefreshScopeRefreshedEventListener(environment, converter);
    }
}
