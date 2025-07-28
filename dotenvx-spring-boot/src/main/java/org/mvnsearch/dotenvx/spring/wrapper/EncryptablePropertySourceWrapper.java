package org.mvnsearch.dotenvx.spring.wrapper;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyFilter;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.mvnsearch.dotenvx.spring.EncryptablePropertySource;
import org.mvnsearch.dotenvx.spring.caching.CachingDelegateEncryptablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.NonNull;

/**
 * <p>Wrapper for {@link org.springframework.core.env.PropertySource} instances that simply delegates the {@link #getProperty} method
 * to the {@link org.springframework.core.env.PropertySource} delegate instance to retrieve properties, while checking if the resulting
 * property is encrypted or not using the Dotenvx convention of surrounding encrypted values with "ENC()".</p>
 * <p>When an encrypted property is detected, it is decrypted using the provided {@link org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor}</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EncryptablePropertySourceWrapper<T> extends PropertySource<T> implements EncryptablePropertySource<T> {
    private final CachingDelegateEncryptablePropertySource<T> encryptableDelegate;

    /**
     * <p>Constructor for EncryptablePropertySourceWrapper.</p>
     *
     * @param delegate a {@link org.springframework.core.env.PropertySource} object
     * @param resolver a {@link EncryptablePropertyResolver} object
     * @param filter   a {@link EncryptablePropertyFilter} object
     */
    public EncryptablePropertySourceWrapper(PropertySource<T> delegate, EncryptablePropertyResolver resolver, EncryptablePropertyFilter filter) {
        super(delegate.getName(), delegate.getSource());
        encryptableDelegate = new CachingDelegateEncryptablePropertySource<>(delegate, resolver, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getProperty(@NonNull String name) {
        return encryptableDelegate.getProperty(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertySource<T> getDelegate() {
        return encryptableDelegate;
    }
}
