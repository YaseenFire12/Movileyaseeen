package edu.stevens.cs548.clinic.micro.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="clinic-domain-api")
@RegisterProvider(PatientResponseExceptionMapper.class)
@Path("patient")
public interface IPatientMicroService {

    /*
     * Customized the object mapper using customizations from DTO factory.
     */
    @ClientObjectMapper
    static ObjectMapper objectMapper(ObjectMapper defaultObjectMapper) {
        ObjectMapper objectMapper = defaultObjectMapper.copy();
        ObjectMapperFactory.customize(objectMapper);
        return objectMapper;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addPatient(PatientDto dto);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<PatientDto> getPatients();

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    PatientDto getPatient(@PathParam("id") String id, @QueryParam("treatments") String includeTreatments);

    @GET
    @Path("{id}/treatment/{tid}")
    @Produces(MediaType.APPLICATION_JSON)
    TreatmentDto getTreatment(@PathParam("id") String patientId, @PathParam("tid") String treatmentId);

    @DELETE
    void removeAll();

}