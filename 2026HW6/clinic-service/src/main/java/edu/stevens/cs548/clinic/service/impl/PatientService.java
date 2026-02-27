package edu.stevens.cs548.clinic.service.impl;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IPatientFactory;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * CDI Bean implementation class PatientService
 */
@ApplicationScoped
@Transactional
public class PatientService implements IPatientService {

	@SuppressWarnings("unused")
	private final Logger logger;

	private final IPatientDao patientDao;

	private final IPatientFactory patientFactory;

	private final PatientDtoFactory patientDtoFactory;

	private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();

	public PatientService(Logger logger, IPatientDao patientDao, IPatientFactory patientFactory) {
		this.logger = logger;
		this.patientDao = patientDao;
		this.patientFactory = patientFactory;
		this.patientDtoFactory = new PatientDtoFactory();
	}

	/**
	 * Add a patient.
	 */
	@Override
	public UUID addPatient(PatientDto dto) throws PatientServiceExn {
		logger.info("Adding patient " + dto.getName());
		// Use factory to create patient entity, and persist with DAO
		try {
			Patient patient = patientFactory.createPatient();
			if (dto.getId() == null) {
				patient.setId(uuidGenerator.generate());
			} else {
				patient.setId(dto.getId());
			}
			patient.setName(dto.getName());
			patient.setDob(dto.getDob());
			patientDao.addPatient(patient);
			return patient.getId();
		} catch (PatientExn e) {
			throw new PatientServiceExn("Failed to add patient", e);
		}
	}

	@Override
	public List<PatientDto> getPatients() throws PatientServiceExn {
		logger.info("Getting all patients");
		Collection<Patient> patients = patientDao.getPatients();
		List<PatientDto> dtos = new ArrayList<>();
		try {
			for (Patient p : patients) {
				dtos.add(patientToDto(p, false));
			}
		} catch (TreatmentExn e) {
			throw new PatientServiceExn("Failed to export treatment", e);
		}
		return dtos;
	}

	@Override
	/*
	 * The boolean flag indicates if related treatments should be loaded eagerly.
	 */
	public PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientNotFoundExn {
		logger.info("Getting patient " + id);
		try {
			Patient patient = patientDao.getPatient(id, includeTreatments);
			return patientToDto(patient, includeTreatments);
		} catch (PatientExn e) {
			throw new PatientNotFoundExn("Failed to get patient", e);
		} catch (TreatmentExn e) {
			throw new PatientNotFoundExn("Failed to export treatments", e);
		}
	}

	@Override
	/*
	 * By default, we eagerly load related treatments with a provider record.
	 */
	public PatientDto getPatient(UUID id) throws PatientNotFoundExn {
		return getPatient(id, true);
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

	@Override
	public TreatmentDto getTreatment(UUID patientId, UUID treatmentId)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn {
		// Export treatment DTO from patient aggregate
		try {
			Patient patient = patientDao.getPatient(patientId);
			return patient.exportTreatment(treatmentId, TreatmentExporter.exportWithFollowups());
		} catch (PatientExn e) {
			throw new PatientNotFoundExn("Patient not found: " + patientId, e);
		} catch (TreatmentExn e) {
			throw new PatientServiceExn("Failed to export treatment", e);
		}
	}

	@Override
	public void removeAll() throws PatientServiceExn {
		patientDao.deletePatients();
	}

}
