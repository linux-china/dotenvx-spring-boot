package org.mvnsearch.dotenvx.ecies;

import org.junit.jupiter.api.Test;


public class EciesTest {

    @Test
    public void testGenerateKeyPair() throws Exception {
        final ECKeyPair ecKeyPair = Ecies.generateEcKeyPair();
        System.out.println("public key: " + ecKeyPair.getPublicHex(true));
        System.out.println("private key: " + ecKeyPair.getPrivateHex());
    }
}
