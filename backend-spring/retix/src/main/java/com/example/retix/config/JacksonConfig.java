package com.example.retix.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public Module caseInsensitiveEnumModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Enum.class, new JsonDeserializer<Enum>() {
            @Override
            public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws java.io.IOException {
                String value = p.getText();
                if (value == null) {
                    return null;
                }
                Class<?> enumClass = ctxt.getContextualType().getRawClass();
                try {
                    @SuppressWarnings("unchecked")
                    Class<Enum> enumClazz = (Class<Enum>) enumClass;
                    return Enum.valueOf(enumClazz, value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new InvalidFormatException(p, "Invalid enum value", value, enumClass);
                }
            }
        });
        return module;
    }
}
