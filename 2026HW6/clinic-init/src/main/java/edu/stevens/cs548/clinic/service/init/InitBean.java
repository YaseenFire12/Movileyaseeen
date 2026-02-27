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
import java.util.Collection;
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

			PatientDto alice = patientFactory.createPatientDto();
			alice.setName("Alice Smith");
			alice.setDob(LocalDate.parse("1988-03-22"));

			logger.info("Adding patient Alice Smith...");
			alice.setId(patientService.addPatient(alice));

			ProviderDto jane = providerFactory.createProviderDto();
			jane.setName("Dr. Jane Wilson");
			jane.setNpi("1234567890");

			logger.info("Adding provider Dr. Jane Wilson...");
			jane.setId(providerService.addProvider(jane));

			ProviderDto bob = providerFactory.createProviderDto();
			bob.setName("Dr. Bob Johnson");
			bob.setNpi("9876543210");

			logger.info("Adding provider Dr. Bob Johnson...");
			bob.setId(providerService.addProvider(bob));

			DrugTreatmentDto drugTreatment = treatmentFactory.createDrugTreatmentDto();
			drugTreatment.setPatientId(john.getId());
			drugTreatment.setPatientName(john.getName());
			drugTreatment.setProviderId(jane.getId());
			drugTreatment.setProviderName(jane.getName());
			drugTreatment.setDiagnosis("Hypertension");
			drugTreatment.setDrug("Lisinopril");
			drugTreatment.setDosage(10);
			drugTreatment.setFrequency(1); // once daily
			drugTreatment.setStartDate(LocalDate.now().minusDays(30));
			drugTreatment.setEndDate(LocalDate.now().plusDays(60));

			logger.info("Adding Drug treatment for John...");
			providerService.addTreatment(drugTreatment);

			SurgeryTreatmentDto surgeryTreatment = treatmentFactory.createSurgeryTreatmentDto();
			surgeryTreatment.setPatientId(alice.getId());
			surgeryTreatment.setPatientName(alice.getName());
			surgeryTreatment.setProviderId(bob.getId());
			surgeryTreatment.setProviderName(bob.getName());
			surgeryTreatment.setDiagnosis("Appendicitis");
			surgeryTreatment.setSurgeryDate(LocalDate.now().minusDays(7));
			surgeryTreatment.setDischargeInstructions("Rest for 2 weeks. No heavy lifting. Follow-up in 10 days.");

			logger.info("Adding Surgery treatment for Alice...");
			providerService.addTreatment(surgeryTreatment);

			RadiologyTreatmentDto radiologyTreatment = treatmentFactory.createRadiologyTreatmentDto();
			radiologyTreatment.setPatientId(john.getId());
			radiologyTreatment.setPatientName(john.getName());
			radiologyTreatment.setProviderId(jane.getId());
			radiologyTreatment.setProviderName(jane.getName());
			radiologyTreatment.setDiagnosis("Chest pain evaluation");
			radiologyTreatment.getTreatmentDates().add(LocalDate.now().minusDays(14));
			radiologyTreatment.getTreatmentDates().add(LocalDate.now().minusDays(7));
			radiologyTreatment.getTreatmentDates().add(LocalDate.now());

			logger.info("Adding Radiology treatment for John...");
			providerService.addTreatment(radiologyTreatment);

			PhysiotherapyTreatmentDto physioTreatment = treatmentFactory.createPhysiotherapyTreatmentDto();
			physioTreatment.setPatientId(alice.getId());
			physioTreatment.setPatientName(alice.getName());
			physioTreatment.setProviderId(bob.getId());
			physioTreatment.setProviderName(bob.getName());
			physioTreatment.setDiagnosis("Lower back pain");
			physioTreatment.getTreatmentDates().add(LocalDate.now().minusDays(21));
			physioTreatment.getTreatmentDates().add(LocalDate.now().minusDays(14));
			physioTreatment.getTreatmentDates().add(LocalDate.now().minusDays(7));
			physioTreatment.getTreatmentDates().add(LocalDate.now());

			logger.info("Adding Physiotherapy treatment for Alice...");
			providerService.addTreatment(physioTreatment);

			DrugTreatmentDto primaryDrug = treatmentFactory.createDrugTreatmentDto();
			primaryDrug.setPatientId(alice.getId());
			primaryDrug.setPatientName(alice.getName());
			primaryDrug.setProviderId(jane.getId());
			primaryDrug.setProviderName(jane.getName());
			primaryDrug.setDiagnosis("Post-surgical infection");
			primaryDrug.setDrug("Amoxicillin");
			primaryDrug.setDosage(500);
			primaryDrug.setFrequency(3); // three times daily
			primaryDrug.setStartDate(LocalDate.now().minusDays(5));
			primaryDrug.setEndDate(LocalDate.now().plusDays(5));

			RadiologyTreatmentDto followupRadiology = treatmentFactory.createRadiologyTreatmentDto();
			followupRadiology.setPatientId(alice.getId());
			followupRadiology.setPatientName(alice.getName());
			followupRadiology.setProviderId(jane.getId());
			followupRadiology.setProviderName(jane.getName());
			followupRadiology.setDiagnosis("Post-treatment monitoring");
			followupRadiology.getTreatmentDates().add(LocalDate.now().plusDays(7));

			primaryDrug.getFollowupTreatments().add(followupRadiology);

			logger.info("Adding Drug treatment with Radiology follow-up for Alice...");
			providerService.addTreatment(primaryDrug);

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
