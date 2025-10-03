package org.mvnsearch.dotenvx.jwt;

import org.junit.jupiter.api.Test;
import org.mvnsearch.dotenvx.ecies.ECKeyPair;
import org.mvnsearch.dotenvx.ecies.Ecies;

import static org.assertj.core.api.Assertions.assertThat;


public class Secp256k1SignerTest {

    @Test
    public void testSign() throws Exception {
        final ECKeyPair keyPair = Ecies.generateEcKeyPair();
        byte[] payload = "Hello World!".getBytes();
        final byte[] signatureBytes = Secp256k1Signer.signData(payload, keyPair.getPrivate());
        final boolean result = Secp256k1Signer.verifySignature(payload, signatureBytes, keyPair.getPublic());
        assertThat(result).isTrue();
    }
}
