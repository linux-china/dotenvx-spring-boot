package org.mvnsearch.dotenvx.spring.exception;

import java.io.Serial;

/**
 * <p>EncryptionException class.</p>
 *
 * @author Ulises Bocchio
 * @version $Id: $Id
 */
public class EncryptionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for DecryptionException.</p>
     *
     * @param message a {@link String} object
     */
    public EncryptionException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for DecryptionException.</p>
     *
     * @param message a {@link String} object
     * @param cause   a {@link Throwable} object
     */
    public EncryptionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
