package org.mvnsearch.dotenvx.spring;

import org.mvnsearch.dotenvx.spring.configuration.EnableEncryptablePropertiesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * dotenvx Spring Boot Auto Configuration
 *
 * @author linux_china
 */
@Configuration
@Import(EnableEncryptablePropertiesConfiguration.class)
public class DotenvxSpringBootAutoConfiguration {
}
