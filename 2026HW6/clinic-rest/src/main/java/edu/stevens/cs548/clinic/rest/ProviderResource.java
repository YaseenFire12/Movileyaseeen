package edu.stevens.cs548.clinic.rest;

import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
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

@Path("provider")
@RequestScoped
public class ProviderResource extends ResourceBase {

	private final Logger logger = Logger.getLogger(ProviderResource.class.getCanonicalName());

	@Context
	private UriInfo uriInfo;

	public ProviderResource(IProviderService providerService) {
		this.providerService = providerService;
	}

	/*
	 * Return a provider DTO including the list of treatments they are
	 * administering.
	 */
	@GET
	@Path("{id}")
	@Produces("application/json")
	public Response getProvider(@PathParam("id") String id) {
		try {
			UUID providerId = UUID.fromString(id);
			ProviderDto provider = providerService.getProvider(providerId, true);
			ResponseBuilder responseBuilder = Response.ok(provider);
			/*
			 * Add links for treatments in response headers.
			 */
			for (TreatmentDto treatment : provider.getTreatments()) {
				responseBuilder.link(getTreatmentUri(uriInfo, treatment.getProviderId(), treatment.getId()), TREATMENT);
			}
			return responseBuilder.build();
		} catch (ProviderServiceExn e) {
			logger.info("Failed to find provider with id " + id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed provider id: " + id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Consumes("application/json")
	public Response addProvider(ProviderDto providerDto) {
		try {
			UUID id = providerService.addProvider(providerDto);
			URI providerUri = getProviderUri(uriInfo, id);
			return Response.created(providerUri).build();
		} catch (ProviderServiceExn e) {
			logger.log(Level.SEVERE, "Provider service request (addProvider) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	/*
	 * Return a provider DTO including the list of treatments they are
	 * administering.
	 */
	@GET
	@Path("{id}/treatment/{tid}")
	@Produces("application/json")
	public Response getTreatment(String id, String tid) {
		try {
			UUID providerId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			TreatmentDto treatment = providerService.getTreatment(providerId, treatmentId);
			ResponseBuilder responseBuilder = Response.ok(treatment);
			responseBuilder.link(getPatientUri(uriInfo, treatment.getPatientId()), PATIENT);
			responseBuilder.link(getProviderUri(uriInfo, treatment.getProviderId()), PROVIDER);

			return responseBuilder.build();
		} catch (ProviderServiceExn e) {
			logger.info("Failed to find provider with id " + id);
			return Response.status(Status.NOT_FOUND).build();
		} catch (IllegalArgumentException e) {
			logger.info("Badly formed provider id: " + id);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path("{id}/treatment")
	@Consumes("application/json")
	public Response addTreatment(String providerId, TreatmentDto treatmentDto) {
		String treatmentProvider = treatmentDto.getProviderId().toString();
		if (!providerId.equals(treatmentProvider)) {
			logger.severe(String.format("Provider in path %s does not match provider in body %s!", providerId,
					treatmentProvider));
			return Response.status(Status.BAD_REQUEST).build();
		}
		try {
			UUID id = providerService.addTreatment(treatmentDto);
			return Response.created(getTreatmentUri(uriInfo, UUID.fromString(providerId), id)).build();
		} catch (ProviderServiceExn | PatientServiceExn e) {
			logger.log(Level.SEVERE, "Provider service request (addTreatment) failed! ", e);
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
