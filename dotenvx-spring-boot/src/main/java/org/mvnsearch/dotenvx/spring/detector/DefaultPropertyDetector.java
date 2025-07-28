package org.mvnsearch.dotenvx.spring.detector;

import org.mvnsearch.dotenvx.spring.EncryptablePropertyDetector;
import org.springframework.util.Assert;

/**
 * Default property detector that detects encrypted property values with the format "encrypted:base64_value".
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class DefaultPropertyDetector implements EncryptablePropertyDetector {

    private String prefix = "encrypted:";

    /**
     * <p>Constructor for DefaultPropertyDetector.</p>
     */
    public DefaultPropertyDetector() {
    }

    /**
     * <p>Constructor for DefaultPropertyDetector.</p>
     *
     * @param prefix a {@link java.lang.String} object
     */
    public DefaultPropertyDetector(String prefix) {
        Assert.notNull(prefix, "Prefix can't be null");
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEncrypted(String property) {
        if (property == null) {
            return false;
        }
        final String trimmedValue = property.trim();
        return (trimmedValue.startsWith(prefix));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String unwrapEncryptedValue(String property) {
        return property.substring(prefix.length());
    }
}
