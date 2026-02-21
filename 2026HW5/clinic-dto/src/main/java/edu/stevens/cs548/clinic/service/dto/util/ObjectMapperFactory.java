package edu.stevens.cs548.clinic.service.dto.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sigpwned.jackson.modules.jdk17.sealedclasses.Jdk17SealedClassesModule;

public class ObjectMapperFactory {

    public static void customize(ObjectMapper objectMapper) {
        objectMapper
                /*
                 * TODO add modules for Java Time and Sealed Classes
                 */

                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        customize(objectMapper);
        return objectMapper;
    }

}
