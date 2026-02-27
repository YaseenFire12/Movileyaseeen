package edu.stevens.cs548.clinic.webapp;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.UUID;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
@Path("/patient")
public class PatientController {

    @Context
    private UriInfo uriInfo;

    private final IPatientService patientService;

    private final Logger logger;

    public PatientController(IPatientService patientService, Logger logger) {
        this.patientService = patientService;
        this.logger = logger;
    }

    @GET
    public TemplateInstance getPatients() {
        logger.info("Getting a list of all patients");
        try {
            return Templates.patients(uriInfo.getBaseUri(), patientService.getPatients());
        } catch (PatientServiceExn e) {
            throw new IllegalStateException("Fatal error while querying for all patients", e);
        }
    }

    @Path("{id}")
    @GET
    public TemplateInstance getPatient(@PathParam("id") String id) {
        logger.info("Looking up patient with id: " + id);
        try {
            return Templates.patient(uriInfo.getBaseUri(), patientService.getPatient(UUID.fromString(id)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Patient id is not a UUID: " + id, e);
        } catch (PatientServiceExn e) {
            throw new IllegalStateException("Fatal error while querying for patient " + id, e);
        }
    }

}
