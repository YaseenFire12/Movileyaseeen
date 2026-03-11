package edu.stevens.cs548.clinic.micro.domain.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stevens.cs548.clinic.service.dto.util.ObjectMapperFactory;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public class CustomObjectMapper implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        ObjectMapperFactory.customize(objectMapper);
    }

}
