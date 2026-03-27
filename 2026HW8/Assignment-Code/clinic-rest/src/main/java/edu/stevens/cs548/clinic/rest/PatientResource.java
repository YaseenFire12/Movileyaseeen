package edu.stevens.cs548.clinic.rest;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("patient")
@RequestScoped
public class PatientResource extends ResourceBase {

	private static final Logger logger = Logger.getLogger(PatientResource.class.getCanonicalName());

	@Context
	private UriInfo uriInfo;

	private final IPatientService patientService;

	public PatientResource(IPatientService patientService) {
		this.patientService = patientService;
	}

	@GET
	@Path("{id}")
	@Produces("application/json")
	public Response getPatient(@PathParam("id") String id) {
		try {
			UUID patientId = UUID.fromString(id);
			PatientDto patient = patientService.getPatient(patientId, true);
			ResponseBuilder responseBuilder = Response.ok(patient);
			for (TreatmentDto treatment : patient.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			return responseBuilder.build();
		} catch (PatientNotFoundExn e) {
			logger.info("Failed to find patient with id " + id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Patient service request (getPatient) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed patient id: " + id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Consumes("application/json")
	public Response addPatient(PatientDto patientDto) {
		try {
			UUID id = patientService.addPatient(patientDto);
			URI patientUri = getPatientUri(uriInfo, id);
			return Response.created(patientUri).build();
		} catch (PatientServiceExn e) {
			logger.log(Level.SEVERE, "Patient service request (addPatient) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
