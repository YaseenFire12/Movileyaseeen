package edu.stevens.cs548.clinic.service.init;

import edu.stevens.cs548.clinic.service.IPatientService;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDtoFactory;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional
public class InitBean {

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private final PatientDtoFactory patientFactory = new PatientDtoFactory();

	private final ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private final TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

	private final IPatientService patientService;

	private final IProviderService providerService;

	private final Logger logger;

	public InitBean(IPatientService patientService, IProviderService providerService, Logger logger) {
		this.patientService = patientService;
		this.providerService = providerService;
		this.logger = logger;
	}

	public void init(@Observes StartupEvent event) {
		/*
		 * Put your testing logic here. Use the logger to display testing output in the
		 * server logs.
		 */
		logger.info("Your name here: ");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Note that the service generates the external ids, when adding the entities.
			 */

			providerService.removeAll();
			patientService.removeAll();

			PatientDto john = patientFactory.createPatientDto();
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));

			logger.info("Adding patient John Doe...");
			john.setId(patientService.addPatient(john));

			PatientDto jane = patientFactory.createPatientDto();
			jane.setName("Jane Smith");
			jane.setDob(LocalDate.parse("1988-03-22"));

			logger.info("Adding patient Jane Smith...");
			jane.setId(patientService.addPatient(jane));

			ProviderDto drHouse = providerFactory.createProviderDto();
			drHouse.setName("Dr. Gregory House");
			drHouse.setNpi("1234567890");

			logger.info("Adding provider Dr. House...");
			drHouse.setId(providerService.addProvider(drHouse));

			ProviderDto drWilson = providerFactory.createProviderDto();
			drWilson.setName("Dr. James Wilson");
			drWilson.setNpi("0987654321");

			logger.info("Adding provider Dr. Wilson...");
			drWilson.setId(providerService.addProvider(drWilson));

			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(drHouse.getId());
			drug01.setProviderName(drHouse.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(500);
			drug01.setFrequency(3);
			drug01.setStartDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			drug01.setEndDate(LocalDate.ofInstant(Instant.now(), ZONE_ID).plusDays(7));
			logger.info("Adding John's drug treatment (Aspirin)...");
			providerService.addTreatment(drug01);

			RadiologyTreatmentDto radiology01 = treatmentFactory.createRadiologyTreatmentDto();
			radiology01.setPatientId(jane.getId());
			radiology01.setPatientName(jane.getName());
			radiology01.setProviderId(drWilson.getId());
			radiology01.setProviderName(drWilson.getName());
			radiology01.setDiagnosis("Possible fracture");
			List<LocalDate> radiologyDates = new ArrayList<>();
			radiologyDates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			radiology01.setTreatmentDates(radiologyDates);
			logger.info("Adding Jane's radiology treatment...");
			providerService.addTreatment(radiology01);

			SurgeryTreatmentDto surgery01 = treatmentFactory.createSurgeryTreatmentDto();
			surgery01.setPatientId(john.getId());
			surgery01.setPatientName(john.getName());
			surgery01.setProviderId(drHouse.getId());
			surgery01.setProviderName(drHouse.getName());
			surgery01.setDiagnosis("Appendicitis");
			surgery01.setSurgeryDate(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			surgery01.setDischargeInstructions("Rest for 2 weeks, no heavy lifting");
			logger.info("Adding John's surgery treatment...");
			providerService.addTreatment(surgery01);

			PhysiotherapyTreatmentDto physio01 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physio01.setPatientId(jane.getId());
			physio01.setPatientName(jane.getName());
			physio01.setProviderId(drWilson.getId());
			physio01.setProviderName(drWilson.getName());
			physio01.setDiagnosis("Knee pain");
			List<LocalDate> physioDates = new ArrayList<>();
			physioDates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID));
			physioDates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID).plusDays(7));
			physioDates.add(LocalDate.ofInstant(Instant.now(), ZONE_ID).plusDays(14));
			physio01.setTreatmentDates(physioDates);
			logger.info("Adding Jane's physiotherapy treatment...");
			providerService.addTreatment(physio01);

			// Now show in the logs what has been added

			Collection<PatientDto> patients = patientService.getPatients();
			for (PatientDto p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(),
						p.getDob().toString()));
				logTreatments(p.getTreatments());
			}

			Collection<ProviderDto> providers = providerService.getProviders();
			for (ProviderDto p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId().toString(), p.getNpi()));
				logTreatments(p.getTreatments());
			}

		} catch (Exception e) {

			throw new IllegalStateException("Failed to add record.", e);

		}

	}

	private void logTreatments(Collection<TreatmentDto> treatments) {
		for (TreatmentDto treatment : treatments) {
			switch (treatment) {
				case DrugTreatmentDto drugTreatmentDto -> logTreatment(drugTreatmentDto);
				case PhysiotherapyTreatmentDto physiotherapyTreatmentDto -> logTreatment(physiotherapyTreatmentDto);
				case RadiologyTreatmentDto radiologyTreatmentDto -> logTreatment(radiologyTreatmentDto);
				case SurgeryTreatmentDto surgeryTreatmentDto -> logTreatment(surgeryTreatmentDto);
			}
			if (!treatment.getFollowupTreatments().isEmpty()) {
				logger.info("============= Follow-up Treatments");
				logTreatments(treatment.getFollowupTreatments());
				logger.info("============= End Follow-up Treatments");
			}
		}
	}

	private void logTreatment(DrugTreatmentDto t) {
		logger.info(String.format("...Drug treatment for %s, drug %s", t.getPatientName(), t.getDrug()));
	}

	private void logTreatment(RadiologyTreatmentDto t) {
		logger.info(String.format("...Radiology treatment for %s", t.getPatientName()));
	}

	private void logTreatment(SurgeryTreatmentDto t) {
		logger.info(String.format("...Surgery treatment for %s", t.getPatientName()));
	}

	private void logTreatment(PhysiotherapyTreatmentDto t) {
		logger.info(String.format("...Physiotherapy treatment for %s", t.getPatientName()));
	}

}
