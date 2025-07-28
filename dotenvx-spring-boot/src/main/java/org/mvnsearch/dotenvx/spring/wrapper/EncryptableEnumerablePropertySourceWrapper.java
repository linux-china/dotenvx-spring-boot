package org.mvnsearch.dotenvx.spring.wrapper;

import org.mvnsearch.dotenvx.spring.caching.CachingDelegateEncryptablePropertySource;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyFilter;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.mvnsearch.dotenvx.spring.EncryptablePropertySource;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * <p>EncryptableEnumerablePropertySourceWrapper class.</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EncryptableEnumerablePropertySourceWrapper<T> extends EnumerablePropertySource<T> implements EncryptablePropertySource<T> {
    private final CachingDelegateEncryptablePropertySource<T> encryptableDelegate;

    /**
     * <p>Constructor for EncryptableEnumerablePropertySourceWrapper.</p>
     *
     * @param delegate a {@link org.springframework.core.env.EnumerablePropertySource} object
     * @param resolver a {@link EncryptablePropertyResolver} object
     * @param filter a {@link EncryptablePropertyFilter} object
     */
    public EncryptableEnumerablePropertySourceWrapper(EnumerablePropertySource<T> delegate, EncryptablePropertyResolver resolver, EncryptablePropertyFilter filter) {
        super(delegate.getName(), delegate.getSource());
        encryptableDelegate = new CachingDelegateEncryptablePropertySource<>(delegate, resolver, filter);
    }

    /** {@inheritDoc} */
    @Override
    public Object getProperty(String name) {
        return encryptableDelegate.getProperty(name);
    }

    /** {@inheritDoc} */
    @Override
    public PropertySource<T> getDelegate() {
        return encryptableDelegate;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public String[] getPropertyNames() {
        return ((EnumerablePropertySource) encryptableDelegate.getDelegate()).getPropertyNames();
    }
}
