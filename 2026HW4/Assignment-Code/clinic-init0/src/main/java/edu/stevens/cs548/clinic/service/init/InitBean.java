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
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import edu.stevens.cs548.clinic.domain.Treatment;
import org.jboss.logging.Logger;

@ApplicationScoped
@Transactional
public class InitBean implements ITreatmentExporter<Void> {

	private static final ZoneId ZONE_ID = ZoneOffset.UTC;

	private final PatientFactory patientFactory = new PatientFactory();
	private final ProviderFactory providerFactory = new ProviderFactory();
	private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();

	private final IPatientDao patientDao;
	private final IProviderDao providerDao;
	private final Logger logger;

	@Inject
	public InitBean(IPatientDao patientDao, IProviderDao providerDao, Logger logger) {
		this.patientDao = patientDao;
		this.providerDao = providerDao;
		this.logger = logger;
	}

	public void init(@Observes StartupEvent event) {
		logger.info("Yaseen Ismail - CS 548 Assignment 4");

		try {
			// Clear database
			providerDao.deleteProviders();
			patientDao.deletePatients();

			// Create 2 patients
			Patient john = patientFactory.createPatient();
			john.setId(uuidGenerator.generate());
			john.setName("John Doe");
			john.setDob(LocalDate.parse("1995-08-15"));
			patientDao.addPatient(john);

			Patient alice = patientFactory.createPatient();
			alice.setId(uuidGenerator.generate());
			alice.setName("Alice Smith");
			alice.setDob(LocalDate.parse("1990-03-20"));
			patientDao.addPatient(alice);

			// Create 2 providers
			Provider jane = providerFactory.createProvider();
			jane.setId(uuidGenerator.generate());
			jane.setName("Dr. Jane Doe");
			jane.setNpi("1234567890");
			providerDao.addProvider(jane);

			Provider bob = providerFactory.createProvider();
			bob.setId(uuidGenerator.generate());
			bob.setName("Dr. Bob Johnson");
			bob.setNpi("0987654321");
			providerDao.addProvider(bob);

			jane.importDrugTreatment(uuidGenerator.generate(), john, jane, "Headache", "Aspirin", 10,
					LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15), 3, null);

			Consumer<Treatment> radiologyConsumer = jane.importRadiology(uuidGenerator.generate(), alice, jane,
					"Cancer", List.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 8), LocalDate.of(2026, 2, 15)),
					null);

			bob.importDrugTreatment(uuidGenerator.generate(), alice, bob, "Pain management", "Morphine", 5,
					LocalDate.of(2026, 2, 20), LocalDate.of(2026, 3, 1), 2, radiologyConsumer);

			jane.importSurgery(uuidGenerator.generate(), john, jane, "Appendicitis",
					LocalDate.of(2026, 3, 10), "Rest for 2 weeks", null);

			bob.importPhysiotherapy(uuidGenerator.generate(), alice, bob, "Back pain",
					List.of(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 8), LocalDate.of(2026, 4, 15)), null);

			logger.info("=== PATIENTS ===");
			Collection<Patient> patients = patientDao.getPatients();
			for (Patient p : patients) {
				logger.info(String.format("Patient %s, ID %s, DOB %s", p.getName(), p.getId(), p.getDob()));
				p.exportTreatments(this);
			}

			logger.info("=== PROVIDERS ===");
			Collection<Provider> providers = providerDao.getProviders();
			for (Provider p : providers) {
				logger.info(String.format("Provider %s, ID %s, NPI %s", p.getName(), p.getId(), p.getNpi()));
				p.exportTreatments(this);
			}

		} catch (Exception e) {
			logger.error("Failed to initialize database", e);
			throw new IllegalStateException("Failed to add record.", e);
		}
	}

	@Override
	public Void exportDrugTreatment(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, String drug, float dosage, LocalDate start, LocalDate end, int frequency,
			Supplier<Collection<Void>> followups) {
		logger.info(String.format("  ...Drug treatment for %s, drug %s, dosage %.1f", patientName, drug, dosage));
		followups.get();
		return null;
	}

	@Override
	public Void exportRadiology(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, List<LocalDate> dates, Supplier<Collection<Void>> followups) {
		logger.info(String.format("  ...Radiology treatment for %s, %d dates", patientName, dates.size()));
		followups.get();
		return null;
	}

	@Override
	public Void exportSurgery(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, LocalDate date, String dischargeInstructions, Supplier<Collection<Void>> followups) {
		logger.info(String.format("  ...Surgery treatment for %s on %s", patientName, date));
		followups.get();
		return null;
	}

	@Override
	public Void exportPhysiotherapy(UUID tid, UUID patientId, String patientName, UUID providerId, String providerName,
			String diagnosis, List<LocalDate> dates, Supplier<Collection<Void>> followups) {
		logger.info(String.format("  ...Physiotherapy treatment for %s, %d dates", patientName, dates.size()));
		followups.get();
		return null;
	}

}