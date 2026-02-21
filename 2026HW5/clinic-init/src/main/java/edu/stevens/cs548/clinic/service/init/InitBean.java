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

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

	@Inject
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
		logger.info("Yaseen Ismail - Assignment 5: ");

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
			logger.info("Adding patient John Doe....");
			john.setId(patientService.addPatient(john));

			PatientDto alice = patientFactory.createPatientDto();
			alice.setName("Alice Smith");
			alice.setDob(LocalDate.parse("1988-03-22"));
			logger.info("Adding patient Alice Smith....");
			alice.setId(patientService.addPatient(alice));

			PatientDto bob = patientFactory.createPatientDto();
			bob.setName("Bob Johnson");
			bob.setDob(LocalDate.parse("1975-11-10"));
			logger.info("Adding patient Bob Johnson....");
			bob.setId(patientService.addPatient(bob));

			ProviderDto drJane = providerFactory.createProviderDto();
			drJane.setName("Dr. Jane Doe");
			drJane.setNpi("1234567890");
			logger.info("Adding provider Dr. Jane Doe....");
			drJane.setId(providerService.addProvider(drJane));

			ProviderDto drSmith = providerFactory.createProviderDto();
			drSmith.setName("Dr. Robert Smith");
			drSmith.setNpi("0987654321");
			logger.info("Adding provider Dr. Robert Smith....");
			drSmith.setId(providerService.addProvider(drSmith));

			ProviderDto drBrown = providerFactory.createProviderDto();
			drBrown.setName("Dr. Emily Brown");
			drBrown.setNpi("1122334455");
			logger.info("Adding provider Dr. Emily Brown....");
			drBrown.setId(providerService.addProvider(drBrown));

			// Drug Treatment 1 for John
			DrugTreatmentDto drug01 = treatmentFactory.createDrugTreatmentDto();
			drug01.setPatientId(john.getId());
			drug01.setPatientName(john.getName());
			drug01.setProviderId(drJane.getId());
			drug01.setProviderName(drJane.getName());
			drug01.setDiagnosis("Headache");
			drug01.setDrug("Aspirin");
			drug01.setDosage(100);
			drug01.setFrequency(2);
			drug01.setStartDate(LocalDate.of(2026, 1, 15));
			drug01.setEndDate(LocalDate.of(2026, 1, 22));
			logger.info("Adding John's drug treatment (Aspirin)");
			providerService.addTreatment(drug01);

			// Drug Treatment 2 for Alice
			DrugTreatmentDto drug02 = treatmentFactory.createDrugTreatmentDto();
			drug02.setPatientId(alice.getId());
			drug02.setPatientName(alice.getName());
			drug02.setProviderId(drSmith.getId());
			drug02.setProviderName(drSmith.getName());
			drug02.setDiagnosis("Back Pain");
			drug02.setDrug("Morphine");
			drug02.setDosage(50);
			drug02.setFrequency(3);
			drug02.setStartDate(LocalDate.of(2026, 2, 1));
			drug02.setEndDate(LocalDate.of(2026, 2, 15));
			logger.info("Adding Alice's drug treatment (Morphine)");
			providerService.addTreatment(drug02);

			// Surgery Treatment for Bob with Follow-up
			SurgeryTreatmentDto surgery01 = treatmentFactory.createSurgeryTreatmentDto();
			surgery01.setPatientId(bob.getId());
			surgery01.setPatientName(bob.getName());
			surgery01.setProviderId(drBrown.getId());
			surgery01.setProviderName(drBrown.getName());
			surgery01.setDiagnosis("Appendicitis");
			surgery01.setSurgeryDate(LocalDate.of(2026, 3, 10));
			surgery01.setDischargeInstructions("Rest for 2 weeks, no heavy lifting");

			// Add follow-up drug treatment to surgery
			DrugTreatmentDto followup01 = treatmentFactory.createDrugTreatmentDto();
			followup01.setPatientId(bob.getId());
			followup01.setPatientName(bob.getName());
			followup01.setProviderId(drBrown.getId());
			followup01.setProviderName(drBrown.getName());
			followup01.setDiagnosis("Post-surgery pain management");
			followup01.setDrug("Ibuprofen");
			followup01.setDosage(200);
			followup01.setFrequency(3);
			followup01.setStartDate(LocalDate.of(2026, 3, 11));
			followup01.setEndDate(LocalDate.of(2026, 3, 25));
			surgery01.getFollowupTreatments().add(followup01);

			logger.info("Adding Bob's surgery treatment with follow-up");
			providerService.addTreatment(surgery01);

			// Radiology Treatment for John
			RadiologyTreatmentDto radiology01 = treatmentFactory.createRadiologyTreatmentDto();
			radiology01.setPatientId(john.getId());
			radiology01.setPatientName(john.getName());
			radiology01.setProviderId(drSmith.getId());
			radiology01.setProviderName(drSmith.getName());
			radiology01.setDiagnosis("Chest X-Ray");
			List<LocalDate> radiologyDates01 = new ArrayList<>();
			radiologyDates01.add(LocalDate.of(2026, 2, 5));
			radiologyDates01.add(LocalDate.of(2026, 2, 12));
			radiologyDates01.add(LocalDate.of(2026, 2, 19));
			radiology01.setTreatmentDates(radiologyDates01);
			logger.info("Adding John's radiology treatment");
			providerService.addTreatment(radiology01);

			// Radiology Treatment for Alice
			RadiologyTreatmentDto radiology02 = treatmentFactory.createRadiologyTreatmentDto();
			radiology02.setPatientId(alice.getId());
			radiology02.setPatientName(alice.getName());
			radiology02.setProviderId(drJane.getId());
			radiology02.setProviderName(drJane.getName());
			radiology02.setDiagnosis("MRI Scan");
			List<LocalDate> radiologyDates02 = new ArrayList<>();
			radiologyDates02.add(LocalDate.of(2026, 2, 20));
			radiologyDates02.add(LocalDate.of(2026, 2, 27));
			radiology02.setTreatmentDates(radiologyDates02);
			logger.info("Adding Alice's radiology treatment");
			providerService.addTreatment(radiology02);

			// Physiotherapy Treatment for Alice
			PhysiotherapyTreatmentDto physio01 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physio01.setPatientId(alice.getId());
			physio01.setPatientName(alice.getName());
			physio01.setProviderId(drBrown.getId());
			physio01.setProviderName(drBrown.getName());
			physio01.setDiagnosis("Back rehabilitation");
			List<LocalDate> physioDates01 = new ArrayList<>();
			physioDates01.add(LocalDate.of(2026, 2, 22));
			physioDates01.add(LocalDate.of(2026, 2, 25));
			physioDates01.add(LocalDate.of(2026, 2, 28));
			physio01.setTreatmentDates(physioDates01);
			logger.info("Adding Alice's physiotherapy treatment");
			providerService.addTreatment(physio01);

			// Physiotherapy Treatment for Bob
			PhysiotherapyTreatmentDto physio02 = treatmentFactory.createPhysiotherapyTreatmentDto();
			physio02.setPatientId(bob.getId());
			physio02.setPatientName(bob.getName());
			physio02.setProviderId(drJane.getId());
			physio02.setProviderName(drJane.getName());
			physio02.setDiagnosis("Post-surgery rehabilitation");
			List<LocalDate> physioDates02 = new ArrayList<>();
			physioDates02.add(LocalDate.of(2026, 3, 20));
			physioDates02.add(LocalDate.of(2026, 3, 27));
			physioDates02.add(LocalDate.of(2026, 4, 3));
			physio02.setTreatmentDates(physioDates02);
			logger.info("Adding Bob's physiotherapy treatment");
			providerService.addTreatment(physio02);

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
