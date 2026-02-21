package edu.stevens.cs548.clinic.webapp;

import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.UUID;
import org.jboss.logging.Logger;
import jakarta.inject.Inject;


@Path("/provider")
public class ProviderController {

    @Context
    private UriInfo uriInfo;

    private final IProviderService providerService;

    private final Logger logger;

    @Inject
    public ProviderController(IProviderService providerService, Logger logger) {
        this.providerService = providerService;
        this.logger = logger;
    }


    @GET
    public TemplateInstance getProviders() {
        logger.info("Getting a list of all providers");
        try {
            return Templates.providers(uriInfo.getBaseUri(), providerService.getProviders());
        } catch (ProviderServiceExn e) {
            throw new IllegalStateException("Fatal error while querying for all providers", e);
        }
    }

    @Path("{id}")
    @GET
    public TemplateInstance getProvider(@PathParam("id") String id) {
        logger.info("Looking up provider with id: " + id);
        try {
            return Templates.provider(uriInfo.getBaseUri(), providerService.getProvider(UUID.fromString(id)));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Provider id is not a UUID: " + id, e);
        } catch (ProviderServiceExn e) {
            throw new IllegalStateException("Fatal error while querying for provider " + id, e);
        }
    }

    @Path("{id}/treatment/{tid}")
    @GET
    public TemplateInstance getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
        logger.info("Looking up treatment with tid: " + tid);
        try {
            TreatmentDto treatmentDto = providerService.getTreatment(UUID.fromString(id), UUID.fromString(tid));
            switch(treatmentDto) {
                case DrugTreatmentDto drugTreatmentDto -> {
                    return Templates.drugTreatment(uriInfo.getBaseUri(), drugTreatmentDto);
                }
                case PhysiotherapyTreatmentDto physiotherapyTreatmentDto -> {
                    return Templates.physiotherapy(uriInfo.getBaseUri(), physiotherapyTreatmentDto);
                }
                case RadiologyTreatmentDto radiologyTreatmentDto -> {
                    return Templates.radiology(uriInfo.getBaseUri(), radiologyTreatmentDto);
                }
                case SurgeryTreatmentDto surgeryTreatmentDto -> {
                    return Templates.surgery(uriInfo.getBaseUri(), surgeryTreatmentDto);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Fatal error while parsing provider %s or treatment %s", id, tid), e);
        } catch (ProviderServiceExn e) {
            throw new IllegalStateException(String.format("Fatal error while querying for provider %s or treatment %s", id, tid), e);
        }
    }

}
