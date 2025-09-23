package org.mvnsearch.dotenvx.spring.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.mvnsearch.dotenvx.ecies.Ecies;

import java.io.IOException;

/**
 * dotenvx json deserializer for String prefixed with `encrypted:`
 *
 * @author linux_china
 */

public class DotenvxGlobalJsonDeserializer extends StdDeserializer<String> {
    private final String privateKey;

    protected DotenvxGlobalJsonDeserializer(String privateKey) {
        super(String.class);
        this.privateKey = privateKey;
    }

    public String decryptData(String data) throws IOException {
        try {
            return Ecies.decrypt(privateKey, data.substring(10));
        } catch (Exception e) {
            throw new IOException("Failed to encrypt data", e);
        }
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        String text = node.asText();
        if (text != null && text.startsWith("encrypted:")) {
            return decryptData(text);
        } else {
            return text;
        }
    }
}
