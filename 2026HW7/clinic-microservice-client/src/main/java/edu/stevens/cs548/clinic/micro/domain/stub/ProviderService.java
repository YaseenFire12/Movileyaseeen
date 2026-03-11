package edu.stevens.cs548.clinic.micro.domain.stub;

import edu.stevens.cs548.clinic.micro.domain.IProviderMicroService;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderNotFoundExn;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

// TODO
public class ProviderService implements IProviderService {

    // TODO inject via constructor injection
    private final Logger logger;


	private static final String LOCATION = "location";
	
	// TODO

	IProviderMicroService providerMicroService;
	
	@Override
	public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
		logger.info(String.format("addProvider: Adding provider %s in microservice client!", dto.getName()));
		try (Response response = providerMicroService.addProvider(dto)) {
			String location = response.getHeaderString(LOCATION);
			if (location == null) {
				throw new IllegalStateException("Missing location response header!");
			}
			String[] uriSegments = URI.create(location).getPath().split("/");
			return UUID.fromString(uriSegments[uriSegments.length-1]);
		}
	}

	@Override
	public List<ProviderDto> getProviders() throws ProviderServiceExn {
		logger.info("getProviders: Getting all providers in microservice client!");
        return providerMicroService.getProviders();
	}

	@Override
	public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
		logger.info(String.format("getProvider: Getting provider %s in microservice client!", id.toString()));
        return providerMicroService.getProvider(id.toString(), Boolean.toString(includeTreatments));
	}

	@Override
	public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
		return getProvider(id, true);
	}

	@Override
	public UUID addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
		logger.info(String.format("addTreatment: Adding treatment for %s in microservice client!", dto.getPatientName()));
		try (Response response = providerMicroService.addTreatment(dto.getProviderId().toString(), dto)) {
			String location = response.getHeaderString(LOCATION);
			if (location == null) {
				throw new IllegalStateException("Missing location header after adding treatment!");
			}
			String[] uriSegments = URI.create(location).getPath().split("/");
			return UUID.fromString(uriSegments[uriSegments.length-1]);
		}
	}

	@Override
	public TreatmentDto getTreatment(UUID providerId, UUID treatmentId)
			throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
        return providerMicroService.getTreatment(providerId.toString(), treatmentId.toString());
	}

	@Override
	public void removeAll() throws ProviderServiceExn {
		logger.info("deleteProviders: Deleting all providers in microservice client!");
        providerMicroService.removeAll();
	}

}
