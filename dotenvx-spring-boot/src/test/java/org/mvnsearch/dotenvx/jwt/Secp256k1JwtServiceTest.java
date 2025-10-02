package org.mvnsearch.dotenvx.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.Test;
import org.mvnsearch.dotenvx.ecies.ECKeyPair;
import org.mvnsearch.dotenvx.ecies.Ecies;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


public class Secp256k1JwtServiceTest {

    @Test
    public void testGenerateJwt() throws Exception {
        final ECKeyPair keyPair = Ecies.generateEcKeyPair();
        String subject = "example-user";
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("dotenvx")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .build();
        final BCECPublicKey publicKey = keyPair.getPublic();
        final BCECPrivateKey privateKey = keyPair.getPrivate();
        final String jwtToken = Secp256k1JwtService.createJwtToken(privateKey, claimsSet);
        final JWTClaimsSet jwtClaimsSet = Secp256k1JwtService.verifyJwt(jwtToken, publicKey);
        assertThat(jwtClaimsSet.getSubject()).isEqualTo(subject);
    }
}
