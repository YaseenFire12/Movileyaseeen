package edu.stevens.cs548.clinic.micro.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.ObjectMapperFactory;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.jackson.ClientObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

// TODO annotate
public interface IProviderMicroService {

    /*
     * Customized the object mapper using customizations from DTO factory.
     */
    @ClientObjectMapper
    static ObjectMapper objectMapper(ObjectMapper defaultObjectMapper) {
        return IPatientMicroService.objectMapper(defaultObjectMapper);
    }

	// TODO

    Response addProvider(ProviderDto dto);

	// TODO

    List<ProviderDto> getProviders();

	// TODO

    ProviderDto getProvider(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);

	// TODO

    Response addTreatment(@PathParam("id") String id, TreatmentDto dto);

	// TODO

    TreatmentDto getTreatment(@PathParam("id") String providerId, @PathParam("tid") String treatmentId);

	// TODO

    void removeAll();
		
}
