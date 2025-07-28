package org.mvnsearch.dotenvx.spring.environment;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

/**
 * <p>EncryptableEnvironment interface.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
public interface EncryptableEnvironment extends ConfigurableEnvironment {
    /**
     * <p>getOriginalPropertySources.</p>
     *
     * @return a {@link org.springframework.core.env.MutablePropertySources} object
     */
    MutablePropertySources getOriginalPropertySources();

}
