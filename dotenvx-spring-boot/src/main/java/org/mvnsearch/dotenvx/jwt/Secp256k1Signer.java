package org.mvnsearch.dotenvx.jwt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;


/**
 * secp256k1 signer
 */
public class Secp256k1Signer {
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * sign data with SHA256withECDSA
     *
     * @param data       data bytes
     * @param privateKey private key
     * @return signature bytes
     */
    public static byte[] signData(byte[] data, PrivateKey privateKey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * verify signature with  with SHA256withECDSA
     *
     * @param data           data
     * @param signatureBytes signature bytes
     * @param publicKey      public key
     * @return verified result
     */
    public static boolean verifySignature(byte[] data, byte[] signatureBytes, PublicKey publicKey)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withECDSA", BouncyCastleProvider.PROVIDER_NAME);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(signatureBytes);
    }
}
