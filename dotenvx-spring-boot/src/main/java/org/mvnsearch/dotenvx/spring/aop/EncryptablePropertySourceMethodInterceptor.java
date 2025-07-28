package org.mvnsearch.dotenvx.spring.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyFilter;
import org.mvnsearch.dotenvx.spring.EncryptablePropertyResolver;
import org.mvnsearch.dotenvx.spring.caching.CachingDelegateEncryptablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.NonNull;

/**
 * <p>EncryptablePropertySourceMethodInterceptor class.</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EncryptablePropertySourceMethodInterceptor<T> extends CachingDelegateEncryptablePropertySource<T> implements MethodInterceptor {

    /**
     * <p>Constructor for EncryptablePropertySourceMethodInterceptor.</p>
     *
     * @param delegate a {@link org.springframework.core.env.PropertySource} object
     * @param resolver a {@link EncryptablePropertyResolver} object
     * @param filter   a {@link EncryptablePropertyFilter} object
     */
    public EncryptablePropertySourceMethodInterceptor(PropertySource<T> delegate, EncryptablePropertyResolver resolver, EncryptablePropertyFilter filter) {
        super(delegate, resolver, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        if (isRefreshCall(invocation)) {
            refresh();
            return null;
        }
        if (isGetDelegateCall(invocation)) {
            return getDelegate();
        }
        if (isGetPropertyCall(invocation)) {
            return getProperty(getNameArgument(invocation));
        }
        return invocation.proceed();
    }

    private String getNameArgument(MethodInvocation invocation) {
        return (String) invocation.getArguments()[0];
    }

    private boolean isGetDelegateCall(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("getDelegate");
    }

    private boolean isRefreshCall(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("refresh");
    }

    private boolean isGetPropertyCall(MethodInvocation invocation) {
        return invocation.getMethod().getName().equals("getProperty")
                && invocation.getMethod().getParameters().length == 1
                && invocation.getMethod().getParameters()[0].getType() == String.class;
    }
}
