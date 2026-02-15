package edu.stevens.cs548.clinic.service.init;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import edu.stevens.cs548.clinic.domain.IPatientDao;
import edu.stevens.cs548.clinic.domain.IProviderDao;
import edu.stevens.cs548.clinic.domain.ITreatmentExporter;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientFactory;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional
public class InitBean implements ITreatmentExporter<Void> {

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private final PatientFactory patientFactory = new PatientFactory();

	private final ProviderFactory providerFactory = new ProviderFactory();

    private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();

    // TODO inject these fields (use constructor injection)

    private final IPatientDao patientDao;

    private final IProviderDao providerDao;

    private final Logger logger;



	/*
	 * Observer of application startup initializes the database
	 */
	public void init(@Observes StartupEvent event) {
        /*
         * Put your testing logic here. Use the logger to display testing output in the
         * server logs.
         */
        logger.info("Your name here: ");
        // System.err.println("Your name here!");

		try {

			/*
			 * Clear the database and populate with fresh data.
			 * 
			 * Deletion of all providers also ensures that all treatments are deleted.
			 */

			providerDao.deleteProviders();
			patientDao.deletePatients();

			Patient john = patientFactory.createPatient();
			john.setId(uuidGenerator.generate());
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));
			patientDao.addPatient(john);

			Provider jane = providerFactory.createProvider();
			jane.setId(uuidGenerator.generate());
			jane.setName("Jane Doe");
			jane.setNpi("1234");
			providerDao.addProvider(jane);

			jane.importDrugTreatment(uuidGenerator.generate(), john, jane, "Headache", "Aspirin", 10,
					LocalDate.ofInstant(Instant.now(), ZONE_ID), LocalDate.ofInstant(Instant.now(), ZONE_ID), 
					3, null);

			// TODO add more testing, including all treatment types and more patients and providers

			// Now show in the logs what has been added

			Collection<Patient> patients = patientDao.getPatients();
			for (Patient p : patients) {
				String dob = p.getDob().toString();
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId().toString(), dob));
				p.exportTreatments(this);
			}

			Collection<Provider> providers = providerDao.getProviders();
			for (Provider p : providers) {
				logger.info(String.format("Provider %s, ID %s", p.getName(), p.getId().toString()));
				p.exportTreatments(this);
			}

		} catch (Exception e) {
			;
			throw new IllegalStateException("Failed to add record.", e);

		}

	}


	@Override
	public Void exportDrugTreatment(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, String drug, float dosage, LocalDate start, LocalDate end, int frequency,
			Supplier<Collection<Void>> followups) {
		logger.info(String.format("...Drug treatment for %s, drug %s", patientName, drug));
		followups.get();
		return null;
	}

	@Override
	public Void exportRadiology(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, List<LocalDate> dates, Supplier<Collection<Void>> followups) {
		logger.info(String.format("...Radiology treatment for %s", patientName));
		followups.get();
		return null;
	}

	@Override
	public Void exportSurgery(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, LocalDate date, String dischargeInstructions, Supplier<Collection<Void>> followups) {
		logger.info(String.format("...Surgery treatment for %s", patientName));
		followups.get();
		return null;
	}

	@Override
	public Void exportPhysiotherapy(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, List<LocalDate> dates, Supplier<Collection<Void>> followups) {
		logger.info(String.format("...Physiotherapy treatment for %s", patientName));
		followups.get();
		return null;
	}

}
