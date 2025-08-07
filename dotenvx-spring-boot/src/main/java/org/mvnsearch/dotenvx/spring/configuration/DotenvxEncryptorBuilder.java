package org.mvnsearch.dotenvx.spring.configuration;

import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptor;
import org.mvnsearch.dotenvx.spring.encryptor.DotenvxEncryptorImpl;
import org.springframework.lang.Nullable;

import java.util.HashMap;

/**
 * <p>DotenvxEncryptorBuilder class.</p>
 *
 * @author Sergio.U.Bocchio
 * @version $Id: $Id
 */
public class DotenvxEncryptorBuilder {

    @Nullable
    private String publicKeyHex;
    @Nullable
    private String privateKeyHex;
    private HashMap<String, String> profileKeyPairs = new HashMap<>();

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

    public DotenvxEncryptorBuilder() {
    }

    /**
     * set public key hex
     *
     * @param publicKeyHex public key hex
     * @return this builder
     */
    public DotenvxEncryptorBuilder withPrimaryKeyPair(@Nullable String publicKeyHex, @Nullable String privateKeyHex) {
        this.publicKeyHex = publicKeyHex;
        this.privateKeyHex = privateKeyHex;
        return this;
    }

    /**
     * set private key hex
     *
     * @param privateKeyHex private key hex
     * @return this builder
     */
    public DotenvxEncryptorBuilder withProfileKeyPair(@Nullable String publicKeyHex, @Nullable String privateKeyHex) {
        if (publicKeyHex != null && privateKeyHex != null) {
            this.profileKeyPairs.put(publicKeyHex, privateKeyHex);
        }
        return this;
    }

    /**
     * build a {@link DotenvxEncryptor} object
     *
     * @return a {@link DotenvxEncryptor} object
     */
    public DotenvxEncryptor build() {
        return new DotenvxEncryptorImpl(publicKeyHex, privateKeyHex, profileKeyPairs);
    }
}
