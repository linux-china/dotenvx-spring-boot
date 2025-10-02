package org.mvnsearch.dotenvx.ecies;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Ecies {

    private static final String CURVE_NAME = "secp256k1";
    private static final int UNCOMPRESSED_PUBLIC_KEY_SIZE = 65;
    private static final int AES_IV_LENGTH = 16;
    private static final int AES_TAG_LENGTH = 16;
    private static final int AES_IV_PLUS_TAG_LENGTH = AES_IV_LENGTH + AES_TAG_LENGTH;
    private static final int SECRET_KEY_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final BouncyCastleProvider BOUNCY_CASTLE_PROVIDER = new BouncyCastleProvider();

    /**
     * Generates new key pair consists of {@link ECPublicKey} and {@link ECPrivateKey}
     *
     * @return new EC key pair
     */
    public static ECKeyPair generateEcKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER);
        g.initialize(ecSpec, SECURE_RANDOM);
        KeyPair keyPair = g.generateKeyPair();
        return new ECKeyPair((BCECPublicKey) keyPair.getPublic(), (BCECPrivateKey) keyPair.getPrivate());
    }

    /**
     * Encrypts a given message with a given public key in hex
     *
     * @param publicKeyHex EC public key in hex
     * @param message      message to encrypt
     * @return encrypted message with base64 encoding
     */
    public static String encrypt(String publicKeyHex, String message) throws InvalidCipherTextException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKey = Hex.decode(publicKeyHex);
        byte[] encrypt = encrypt(publicKey, message.getBytes(StandardCharsets.UTF_8));
        return Base64.toBase64String(encrypt);
    }

    /**
     * Decrypts given ciphertext with a given private key
     *
     * @param privateKeyHex EC private key in hex
     * @param ciphertext    ciphered text in base64
     * @return decrypted message
     */
    public static String decrypt(String privateKeyHex, String ciphertext) throws InvalidCipherTextException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKey = Hex.decode(privateKeyHex);
        byte[] cipherBytes = Base64.decode(ciphertext);
        return new String(decrypt(privateKey, cipherBytes), StandardCharsets.UTF_8);
    }

    /**
     * Encrypts a given message with a given public key
     *
     * @param publicKeyBytes EC public key binary
     * @param message        message to encrypt binary
     * @return encrypted message binary
     */
    public static byte[] encrypt(byte[] publicKeyBytes, byte[] message) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidCipherTextException {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        KeyPair pair = generateEphemeralKey(ecSpec);

        ECPrivateKey ephemeralPrivateKey = (ECPrivateKey) pair.getPrivate();
        ECPublicKey ephemeralPublicKey = (ECPublicKey) pair.getPublic();

        //generate receiver PK
        KeyFactory keyFactory = getKeyFactory();
        ECNamedCurveSpec curvedParams = new ECNamedCurveSpec(CURVE_NAME, ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN());
        ECPublicKey publicKey = getEcPublicKey(curvedParams, publicKeyBytes, keyFactory);

        //Derive shared secret
        byte[] uncompressed = ephemeralPublicKey.getQ().getEncoded(false);
        byte[] multiply = publicKey.getQ().multiply(ephemeralPrivateKey.getD()).getEncoded(false);
        byte[] aesKey = hkdf(uncompressed, multiply);

        // AES encryption
        return aesEncrypt(message, ephemeralPublicKey, aesKey);
    }

    /**
     * Decrypts given ciphertext with a given private key
     *
     * @param privateKeyBytes EC private key binary
     * @param cipherBytes     cipher text binary
     * @return decrypted message binary
     */
    public static byte[] decrypt(byte[] privateKeyBytes, byte[] cipherBytes) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidCipherTextException {
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        KeyFactory keyFactory = getKeyFactory();
        ECNamedCurveSpec curvedParams = new ECNamedCurveSpec(CURVE_NAME, ecSpec.getCurve(), ecSpec.getG(), ecSpec.getN());

        //generate a receiver private key
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(1, privateKeyBytes), curvedParams);
        ECPrivateKey receiverPrivateKey = (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        //get sender pub key
        byte[] senderPubKeyByte = Arrays.copyOf(cipherBytes, UNCOMPRESSED_PUBLIC_KEY_SIZE);
        ECPublicKey senderPubKey = getEcPublicKey(curvedParams, senderPubKeyByte, keyFactory);

        //decapsulate
        byte[] uncompressed = senderPubKey.getQ().getEncoded(false);
        byte[] multiply = senderPubKey.getQ().multiply(receiverPrivateKey.getD()).getEncoded(false);
        byte[] aesKey = hkdf(uncompressed, multiply);

        // AES decryption
        return aesDecrypt(cipherBytes, aesKey);
    }

    private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("EC", BOUNCY_CASTLE_PROVIDER);
    }

    private static byte[] aesEncrypt(byte[] message, ECPublicKey ephemeralPubKey, byte[] aesKey) throws InvalidCipherTextException {
        final GCMModeCipher aesGcmBlockCipher = GCMBlockCipher.newInstance(AESEngine.newInstance());
        byte[] nonce = new byte[AES_IV_LENGTH];
        SECURE_RANDOM.nextBytes(nonce);

        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(aesKey), nonce);
        aesGcmBlockCipher.init(true, parametersWithIV);

        int outputSize = aesGcmBlockCipher.getOutputSize(message.length);

        byte[] encrypted = new byte[outputSize];
        int pos = aesGcmBlockCipher.processBytes(message, 0, message.length, encrypted, 0);
        aesGcmBlockCipher.doFinal(encrypted, pos);

        byte[] tag = Arrays.copyOfRange(encrypted, encrypted.length - nonce.length, encrypted.length);
        encrypted = Arrays.copyOfRange(encrypted, 0, encrypted.length - tag.length);

        byte[] ephemeralPkUncompressed = ephemeralPubKey.getQ().getEncoded(false);
        return org.bouncycastle.util.Arrays.concatenate(ephemeralPkUncompressed, nonce, tag, encrypted);
    }

    private static KeyPair generateEphemeralKey(ECNamedCurveParameterSpec ecSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER);
        g.initialize(ecSpec, SECURE_RANDOM);
        return g.generateKeyPair();
    }

    private static byte[] aesDecrypt(byte[] inputBytes, byte[] aesKey) throws InvalidCipherTextException {
        byte[] encrypted = Arrays.copyOfRange(inputBytes, UNCOMPRESSED_PUBLIC_KEY_SIZE, inputBytes.length);
        byte[] nonce = Arrays.copyOf(encrypted, AES_IV_LENGTH);
        byte[] tag = Arrays.copyOfRange(encrypted, AES_IV_LENGTH, AES_IV_PLUS_TAG_LENGTH);
        byte[] ciphered = Arrays.copyOfRange(encrypted, AES_IV_PLUS_TAG_LENGTH, encrypted.length);

        final GCMModeCipher aesGcmBlockCipher = GCMBlockCipher.newInstance(AESEngine.newInstance());
        ParametersWithIV parametersWithIV = new ParametersWithIV(new KeyParameter(aesKey), nonce);
        aesGcmBlockCipher.init(false, parametersWithIV);

        int outputSize = aesGcmBlockCipher.getOutputSize(ciphered.length + tag.length);
        byte[] decrypted = new byte[outputSize];
        int pos = aesGcmBlockCipher.processBytes(ciphered, 0, ciphered.length, decrypted, 0);
        pos += aesGcmBlockCipher.processBytes(tag, 0, tag.length, decrypted, pos);
        aesGcmBlockCipher.doFinal(decrypted, pos);
        return decrypted;
    }

    private static byte[] hkdf(byte[] uncompressed, byte[] multiply) {
        byte[] master = org.bouncycastle.util.Arrays.concatenate(uncompressed, multiply);
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
        hkdfBytesGenerator.init(new HKDFParameters(master, null, null));
        byte[] aesKey = new byte[SECRET_KEY_LENGTH];
        hkdfBytesGenerator.generateBytes(aesKey, 0, aesKey.length);
        return aesKey;
    }

    private static ECPublicKey getEcPublicKey(ECNamedCurveSpec curvedParams, byte[] senderPubKeyByte, KeyFactory keyFactory) throws InvalidKeySpecException {
        java.security.spec.ECPoint point = org.bouncycastle.jce.ECPointUtil.decodePoint(curvedParams.getCurve(), senderPubKeyByte);
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, curvedParams);
        return (ECPublicKey) keyFactory.generatePublic(pubKeySpec);
    }
}
