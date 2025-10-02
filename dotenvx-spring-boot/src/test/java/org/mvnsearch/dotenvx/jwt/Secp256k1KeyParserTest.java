package org.mvnsearch.dotenvx.jwt;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.mvnsearch.dotenvx.jwt.Secp256k1KeyParser.parseSecp256k1CompressedPublicKey;
import static org.mvnsearch.dotenvx.jwt.Secp256k1KeyParser.parseSecp256k1PrivateKey;

public class Secp256k1KeyParserTest {

    @Test
    public void parseKeys() throws Exception {
        String publicKeyHex = "02e8d78f0da7fc3b529d503edd933ed8cdc79dbe5fd5d9bd480f1e63a09905f3b3";
        String privateKeyHex = "c4e79fecc6bfeb1fe3bf4d783ddf330339c1d89c875fd6edde04d7f1b6d28678";
        parseSecp256k1CompressedPublicKey(HexFormat.of().parseHex(publicKeyHex));
        parseSecp256k1PrivateKey(HexFormat.of().parseHex(privateKeyHex));
    }
}
