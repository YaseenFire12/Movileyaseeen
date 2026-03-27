package edu.stevens.cs548.clinic.micro.domain;

import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class PatientResponseExceptionMapper implements ResponseExceptionMapper<PatientServiceExn> {
    @Override
    public PatientServiceExn toThrowable(Response response) {
        if (response.getStatus() >= 400) {
            return new PatientServiceExn("Patient microservice responded with HTTP status "+response.getStatus());
        }
        return null;
    }
}