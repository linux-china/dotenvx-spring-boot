package org.mvnsearch.dotenvx.spring.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.mvnsearch.dotenvx.ecies.Ecies;

import java.io.IOException;

/**
 * dotenvx json serializer for String prefixed with `private:`
 *
 * @author linux_china
 */
public class DotenvxGlobalJsonSerializer extends StdSerializer<String> {
    private final String publicKey;
    public static String prefix = "private:";

    protected DotenvxGlobalJsonSerializer(String publicKey) {
        super(String.class);
        this.publicKey = publicKey;
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (s != null && s.startsWith(prefix)) {
            jsonGenerator.writeString("encrypted:" + encryptData(s.substring(prefix.length())));
        } else {
            jsonGenerator.writeString(s);
        }
    }

    public String encryptData(String data) throws IOException {
        try {
            return Ecies.encrypt(publicKey, data);
        } catch (Exception e) {
            throw new IOException("Failed to encrypt data", e);
        }
    }

}
