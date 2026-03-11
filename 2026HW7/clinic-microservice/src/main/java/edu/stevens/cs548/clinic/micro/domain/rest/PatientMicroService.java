package edu.stevens.cs548.clinic.micro.domain.rest;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IPatientFactory;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientFactory;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * REST version of Patient Service
 */
// TODO
public class PatientMicroService {

	// TODO
	private UriInfo uriInfo;
	
	private final IPatientFactory patientFactory = new PatientFactory();
	
	private final PatientDtoFactory patientDtoFactory = new PatientDtoFactory();

    private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();

    // TODO inject these fields via constructor injection
    private final Logger logger;

    private final IPatientDao patientDao;


	
	// TODO

	public Response addPatient(PatientDto dto) {
		try {
			logger.info(String.format("addPatient: Adding patient %s in microservice!", dto.getName()));
			Patient patient = patientFactory.createPatient();
			if (dto.getId() == null) {
				patient.setId(uuidGenerator.generate());
			} else {
				patient.setId(dto.getId());
			}
			patient.setName(dto.getName());
			patient.setDob(dto.getDob());
			patientDao.addPatient(patient);
			UUID id = patient.getId();
			URI uri = uriInfo.getBaseUriBuilder().path(id.toString()).build();
			return Response.created(uri).build();
		} catch (PatientExn e) {
			logger.fatal("Failed to add patient!", e);
			return Response.serverError().build();
		}
	}

	// TODO

	public List<PatientDto> getPatients() {
		try {
			logger.info("getPatients: Getting all patients in microservice!");
			Collection<Patient> patients = patientDao.getPatients();
			List<PatientDto> dtos = new ArrayList<>();
			for (Patient p : patients) {
				dtos.add(patientToDto(p, false));
			}
			return dtos;
		} catch (Exception e) {
			logger.fatal("Failed to get patients!", e);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	private PatientDto patientToDto(Patient patient, boolean includeTreatments) throws TreatmentExn {
		PatientDto dto = patientDtoFactory.createPatientDto();
		dto.setId(patient.getId());
		dto.setName(patient.getName());
		dto.setDob(patient.getDob());
		if (includeTreatments) {
			dto.setTreatments(patient.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
		}
		return dto;
	}
	
	// TODO

	public PatientDto getPatient(@PathParam("id") String id, @QueryParam("treatments") @DefaultValue("true") String treatments) {
		try {
			logger.infof("getPatient: Getting patient %s in microservice!", id);
			UUID patientId = UUID.fromString(id);
			boolean includeTreatments = Boolean.parseBoolean(treatments);

			// TODO use DAO to get patient by external key, create DTO that includes treatments

			
		} catch (PatientExn e) {
			logger.infof(e, "Failed to find patient with id %s", id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (Exception e) {
			logger.fatalf(e, "Failed to get patient %s!", id);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	// TODO

	public TreatmentDto getTreatment(@PathParam("id") String id, @PathParam("tid") String tid) {
		// Export treatment DTO from patient aggregate
		try {
			logger.infof("getTreatment: Getting treatment %s in microservice!", tid);
			UUID patientId = UUID.fromString(id);
			UUID treatmentId = UUID.fromString(tid);
			Patient patient = patientDao.getPatient(patientId);
			return patient.exportTreatment(treatmentId, TreatmentExporter.exportWithFollowups());
		} catch (PatientExn e) {
			logger.infof(e, "Failed to find patient with id %s", id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (TreatmentExn e) {
			logger.infof(e, "Failed to find treatment %s for patient %s", tid, id);
			throw new WebApplicationException(Status.NOT_FOUND);
		} catch (Exception e) {
			logger.fatalf(e, "Failed to get treatment %s!", tid);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	// TODO

	public void removeAll() {
		logger.info(String.format("deletePatients: Deleting all patients in microservice!"));
		patientDao.deletePatients();
	}

}
