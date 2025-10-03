package org.mvnsearch.dotenvx.jwt;

import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

public class Secp256k1KeyParser {
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProviderSingleton.getInstance());
        }
    }

    /**
     * parse secp256k1 public key
     *
     * @param compressedPublicKeyBytes compressed public key bytes
     * @return public key
     * @throws Exception exception
     */
    public static ECPublicKey parseSecp256k1CompressedPublicKey(byte[] compressedPublicKeyBytes) throws Exception {
        // 1. Retrieve the Curve Parameters
        X9ECParameters x9ECParameters = SECNamedCurves.getByName("secp256k1");
        ECCurve curve = x9ECParameters.getCurve();
        // 2. Decode the Compressed Point
        ECPoint ecPoint = curve.decodePoint(compressedPublicKeyBytes);
        // 3. Construct the Public Key
        ECParameterSpec ecParameterSpec = new ECParameterSpec(
                x9ECParameters.getCurve(),
                x9ECParameters.getG(),
                x9ECParameters.getN(),
                x9ECParameters.getH()
        );
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return (ECPublicKey) keyFactory.generatePublic(pubSpec);
    }

    /**
     * parse secp 256k1 private key
     *
     * @param privateKeyBytes private key bytes
     * @return private key
     * @throws Exception exception
     */
    public static ECPrivateKey parseSecp256k1PrivateKey(byte[] privateKeyBytes) throws Exception {
        // Get the ECParameterSpec for secp256k1
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        // Convert the private key bytes to a BigInteger
        BigInteger privateKeyValue = new BigInteger(1, privateKeyBytes);
        // Create an ECPrivateKeySpec
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKeyValue, ecSpec);
        // Get a KeyFactory for EC
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        // Generate the PrivateKey object
        return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
    }

}
