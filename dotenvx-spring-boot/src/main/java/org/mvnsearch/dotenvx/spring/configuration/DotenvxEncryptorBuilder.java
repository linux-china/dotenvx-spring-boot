package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor;
import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptorImpl;
import org.springframework.lang.Nullable;

/**
 * <p>DotenvxEncryptorBuilder class.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
public class DotenvxEncryptorBuilder {

    private String publicKeyHex;
    private String privateKeyHex;

    /**
     * <p>Constructor for DotenvxEncryptorBuilder.</p>
     *
     * @param publicKeyHex  public key name
     * @param privateKeyHex private key name
     */
    public DotenvxEncryptorBuilder(@Nullable String publicKeyHex, @Nullable String privateKeyHex) {
        this.publicKeyHex = publicKeyHex;
        this.privateKeyHex = privateKeyHex;
    }

    /**
     * set public key hex
     *
     * @param publicKeyHex public key hex
     * @return this builder
     */
    public DotenvxEncryptorBuilder setPublicKeyHex(@Nullable String publicKeyHex) {
        this.publicKeyHex = publicKeyHex;
        return this;
    }

    /**
     * set private key hex
     *
     * @param privateKeyHex private key hex
     * @return this builder
     */
    public DotenvxEncryptorBuilder setPrivateKeyHex(@Nullable String privateKeyHex) {
        this.privateKeyHex = privateKeyHex;
        return this;
    }

    /**
     * build a {@link DotenvxEncryptor} object
     *
     * @return a {@link DotenvxEncryptor} object
     */
    public DotenvxEncryptor build() {
        return new DotenvxEncryptorImpl(publicKeyHex, privateKeyHex);
    }
}
