package org.mvnsearch.dotenvx.spring;

/**
 * An interface to resolve property values that may be encrypted.
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public interface EncryptablePropertyResolver {

    /**
     * Returns the unencrypted version of the value provided free on any prefixes/suffixes or any other metadata
     * surrounding the encrypted value. Or the actual same String if no encryption was detected.
     *
     * @param value the property value
     * @return either the same value if the value is not encrypted, or the decrypted version.
     */
    String resolvePropertyValue(String value);
}
