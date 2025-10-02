package org.mvnsearch.dotenvx.ecies;

import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

@SuppressWarnings("unused")
public final class ECKeyPair {

    private final BCECPrivateKey privateKey;
    private final BCECPublicKey publicKey;

    public ECKeyPair(BCECPublicKey publicKey, BCECPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public BCECPublicKey getPublic() {
        return publicKey;
    }

    public BCECPrivateKey getPrivate() {
        return privateKey;
    }

    public byte[] getPublicBinary(boolean encoded) {
        return publicKey.getQ().getEncoded(encoded);
    }

    public byte[] getPrivateBinary() {
        return privateKey.getD().toByteArray();
    }

    public String getPublicHex(boolean encoded) {
        return Hex.toHexString(getPublicBinary(encoded));
    }

    public String getPrivateHex() {
        return Hex.toHexString(getPrivateBinary());
    }
}
