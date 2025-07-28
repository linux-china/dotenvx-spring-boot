package org.mvnsearch.dotenvx.spring.caching;

import org.mvnsearch.dotenvx.spring.EncryptablePropertySource;
import org.mvnsearch.dotenvx.spring.EncryptablePropertySourceConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>RefreshScopeRefreshedEventListener class.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class RefreshScopeRefreshedEventListener implements ApplicationListener<ApplicationEvent>, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(RefreshScopeRefreshedEventListener.class);

    /**
     * Constant <code>EVENT_CLASS_NAMES</code>
     */
    public static final List<String> EVENT_CLASS_NAMES = Arrays.asList(
            "org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent",
            "org.springframework.cloud.context.environment.EnvironmentChangeEvent",
            "org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent"
    );
    private final ConfigurableEnvironment environment;
    private final EncryptablePropertySourceConverter converter;
    private final List<Class<?>> eventClasses = new ArrayList<>();
    private final Map<String, Boolean> eventTriggersCache = new ConcurrentHashMap<>();

    /**
     * <p>Constructor for RefreshScopeRefreshedEventListener.</p>
     *
     * @param environment a {@link org.springframework.core.env.ConfigurableEnvironment} object
     * @param converter   a {@link EncryptablePropertySourceConverter} object
     */
    public RefreshScopeRefreshedEventListener(ConfigurableEnvironment environment, EncryptablePropertySourceConverter converter) {
        this.environment = environment;
        this.converter = converter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (this.shouldTriggerRefresh(event)) {
            log.info("Refreshing cached encryptable property sources on {}", event.getClass().getSimpleName());
            refreshCachedProperties();
            decorateNewSources();
        }
    }

    private boolean shouldTriggerRefresh(ApplicationEvent event) {
        String className = event.getClass().getName();
        if (!eventTriggersCache.containsKey(className)) {
            eventTriggersCache.put(className, eventClasses.stream().anyMatch(clazz -> this.isAssignable(clazz, event)));
        }
        return eventTriggersCache.get(className);
    }

    private void decorateNewSources() {
        MutablePropertySources propSources = environment.getPropertySources();
        converter.convertPropertySources(propSources);
    }

    boolean isAssignable(Class<?> clazz, Object value) {
        return ClassUtils.isAssignableValue(clazz, value);
    }

    private void refreshCachedProperties() {
        PropertySources propertySources = environment.getPropertySources();
        propertySources.forEach(this::refreshPropertySource);
    }

    @SuppressWarnings("rawtypes")
    private void refreshPropertySource(PropertySource<?> propertySource) {
        if (propertySource instanceof CompositePropertySource cps) {
            cps.getPropertySources().forEach(this::refreshPropertySource);
        } else if (propertySource instanceof EncryptablePropertySource eps) {
            eps.refresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {

    }
}
