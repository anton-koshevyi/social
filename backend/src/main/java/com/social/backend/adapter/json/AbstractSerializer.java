package com.social.backend.adapter.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public abstract class AbstractSerializer<T> extends JsonSerializer<T> {
    public abstract Object beforeSerialize(T value);
    
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        Object response = this.beforeSerialize(value);
        gen.writeObject(response);
    }
}
