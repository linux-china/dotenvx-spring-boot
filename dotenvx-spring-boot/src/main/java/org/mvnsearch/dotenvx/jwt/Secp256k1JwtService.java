package org.mvnsearch.dotenvx.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;

/**
 * secp 256k1 JWT service
 */
public class Secp256k1JwtService {

    /**
     * create JWT Token
     *
     * @param privateKey private key
     * @param claimsSet  claims set
     * @return generated JWT token
     * @throws JOSEException jose exception
     */
    public static String createJwtToken(ECPrivateKey privateKey, JWTClaimsSet claimsSet) throws JOSEException {
        // Create the JWS header with ES256K algorithm
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256K).type(JOSEObjectType.JWT).build();
        // Create the signed JWT
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        // Sign the JWT with the private key
        JWSSigner signer = new ECDSASigner(privateKey, Curve.SECP256K1);
        signer.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());
        signedJWT.sign(signer);
        // Serialize the JWT to a compact string
        return signedJWT.serialize();
    }

    /**
     * verify JWT token
     *
     * @param jwt       jwt token
     * @param publicKey public key
     * @return claims set
     * @throws JOSEException  jose exception
     * @throws ParseException JWT parse exception
     */
    public static JWTClaimsSet verifyJwt(String jwt, ECPublicKey publicKey) throws JOSEException, ParseException {
        // Parse the signed JWT
        SignedJWT signedJWT = SignedJWT.parse(jwt);
        // Create a verifier with the public key
        JWSVerifier verifier = new ECDSAVerifier(publicKey);
        verifier.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());
        // Verify the signature
        signedJWT.verify(verifier);
        // return claims set
        return signedJWT.getJWTClaimsSet();
    }
}
