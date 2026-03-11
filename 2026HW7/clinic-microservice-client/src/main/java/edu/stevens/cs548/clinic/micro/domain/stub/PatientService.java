package edu.stevens.cs548.clinic.micro.domain.stub;

import edu.stevens.cs548.clinic.micro.domain.IPatientMicroService;
import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

// TODO
public class PatientService implements IPatientService {

	private final Logger logger;

    public PatientService(Logger logger) {
        this.logger = logger;
    }
	
	private static final String LOCATION = "Location";
	
	// TODO inject a REST client stub
	IPatientMicroService patientMicroService;

    @Override
    public UUID addPatient(PatientDto dto) throws PatientServiceExn {
        logger.info(String.format("addPatient: Adding patient %s in microservice client!", dto.getName()));
        try (Response response = patientMicroService.addPatient(dto)) {
            String location = response.getHeaderString(LOCATION);
            if (location == null) {
                throw new IllegalStateException("Missing location response header!");
            }
            String[] uriSegments = URI.create(location).getPath().split("/");
            return UUID.fromString(uriSegments[uriSegments.length - 1]);
        }
    }

	@Override
	public List<PatientDto> getPatients() throws PatientServiceExn {
		logger.info("getPatients: Getting all patients in microservice client!");
        return patientMicroService.getPatients();
	}

	@Override
	public PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientServiceExn {
        logger.info(String.format("getPatient: Getting patient %s in microservice client!", id.toString()));
        return patientMicroService.getPatient(id.toString(), Boolean.toString(includeTreatments));
    }

	@Override
	public PatientDto getPatient(UUID id) throws PatientServiceExn {
		return getPatient(id, true);
	}

	@Override
	public TreatmentDto getTreatment(UUID patientId, UUID treatmentId)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn {
		logger.info(String.format("getTreatment: Getting treatment %s in microservice client!", treatmentId.toString()));
        return patientMicroService.getTreatment(patientId.toString(), treatmentId.toString());
	}

	@Override
	public void removeAll() throws PatientServiceExn {
		logger.info("deletePatients: Deleting all patients in microservice client!");
        patientMicroService.removeAll();
	}

}
