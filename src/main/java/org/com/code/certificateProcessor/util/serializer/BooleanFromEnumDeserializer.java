package org.com.code.certificateProcessor.util.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class BooleanFromEnumDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null) return null;

        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("1") || value.equalsIgnoreCase("YES"))
            return true;
        else if (value.equalsIgnoreCase("FALSE") || value.equalsIgnoreCase("0") || value.equalsIgnoreCase("NO"))
            return false;
        else
            return null;
    }
}
