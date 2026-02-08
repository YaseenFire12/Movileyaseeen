package edu.stevens.cs548.clinic.data.main;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import edu.stevens.cs548.clinic.data.DrugTreatment;
import edu.stevens.cs548.clinic.data.IPatientFactory;
import edu.stevens.cs548.clinic.data.IProviderFactory;
import edu.stevens.cs548.clinic.data.ITreatmentFactory;
import edu.stevens.cs548.clinic.data.Patient;
import edu.stevens.cs548.clinic.data.PatientFactory;
import edu.stevens.cs548.clinic.data.PhysiotherapyTreatment;
import edu.stevens.cs548.clinic.data.Provider;
import edu.stevens.cs548.clinic.data.ProviderFactory;
import edu.stevens.cs548.clinic.data.RadiologyTreatment;
import edu.stevens.cs548.clinic.data.SurgeryTreatment;
import edu.stevens.cs548.clinic.data.Treatment;
import edu.stevens.cs548.clinic.data.TreatmentFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class App {
	
	/*
	 * JPA Properties
	 */
	private static final String JPA_SERVER_URL = "jakarta.persistence.jdbc.url";
	
	private static final String JPA_SERVER_USER = "jakarta.persistence.jdbc.user";
	
	private static final String JPA_SERVER_PASSWORD = "jakarta.persistence.jdbc.password";
	/*
	 * Application properties.
	 */
	private static final String DATABASE_PROPS_FILE = "/client.properties";
	
	private static final String DATABASE_SERVER_URL = "server.url";
	
	private static final String DATABASE_SERVER_USER = "server.user";
	
	private static final String DATABASE_SERVER_PASSWORD = "server.password";
	
	private static final String LOGGER_PROPS_FILE = "/logger.properties";
		

	private final Logger logger;
	
	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();
	
	/*
	 * Application domain.
	 */
	private static final String PERSISTENCE_UNIT = "clinic-domain";

	private static final String SEARCH_PATIENT_QUERY = "SearchPatientByPatientId";

	private static final String SEARCH_PROVIDER_QUERY = "SearchProviderByProviderId";

	private final IPatientFactory patientFactory = new PatientFactory();

	private final IProviderFactory providerFactory = new ProviderFactory();

	private final ITreatmentFactory treatmentFactory = new TreatmentFactory();
	
	
	private String serverUrl;
	
	private String serverUser;
	
	private String serverPassword;
		
	private EntityManagerFactory entityManagerFactory;
	

	public void severe(String s) {
		logger.severe(s);
	}

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}

	public void warning(String s) {
		logger.warning(s);
	}

	public void info(String s) {
		logger.info(s);
	}
	
	protected List<String> processArgs(String[] args) {
		List<String> commandLineArgs = new ArrayList<>();
		int ix = 0;
		Hashtable<String, String> opts = new Hashtable<>();

		while (ix < args.length) {
			if (args[ix].startsWith("--")) {
				String option = args[ix++].substring(2);
				if (ix == args.length || args[ix].startsWith("--"))
					severe("Missing argument for --" + option + " option.");
				else if (opts.containsKey(option))
					severe("Option \"" + option + "\" already set.");
				else
					opts.put(option, args[ix++]);
			} else {
				commandLineArgs.add(args[ix++]);
			}
		}
		/*
		 * Overrides of values from the configuration file.
		 */
		Enumeration<String> keys = opts.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement();
            switch (k) {
                case "server" -> serverUrl = opts.get("server");
                case "user" -> serverUser = opts.get("user");
                case "password" -> serverPassword = opts.get("password");
                case null, default -> severe("Unrecognized option: --" + k);
            }
		}

		return commandLineArgs;
	}
	


	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			new App(args);
		} catch (SecurityException | IOException e) {
			throw new IllegalStateException("Failure during initialization!", e);
		}
	}

	static void msg(String m) {
		System.out.print(m);
	}

	static void msgln(String m) {
		System.out.println(m);
	}

	static void err(String s) {
		System.err.println("** " + s);
	}

    public App(String[] args) throws SecurityException, IOException {

        Properties props = new Properties();
        try (InputStream inProps = getClass().getResourceAsStream(DATABASE_PROPS_FILE)) {
            props.load(inProps);
        }
        serverUrl = (String) props.get(DATABASE_SERVER_URL);
        serverUser = (String) props.get(DATABASE_SERVER_USER);
        serverPassword = (String) props.get(DATABASE_SERVER_PASSWORD);

        /*
         * Process overrides from the command line
         */
        processArgs(args);

        /*
         * Configure logging.
         */
        LogManager logManager = LogManager.getLogManager();
        try (InputStream inProps = getClass().getResourceAsStream(LOGGER_PROPS_FILE)) {
            logManager.readConfiguration(inProps);
        }
        logger = Logger.getLogger(App.class.getCanonicalName());

        connectToDatabase();

        while (true) {
            try {
                msg("cs548> ");
                String line = in.readLine();
                if (line == null) {
                    return;
                }
                String[] inputs = line.split("\\s+");
                if (inputs.length > 0) {
                    String cmd = inputs[0];
                    switch (cmd) {
                        case "" -> {
                        }
                        case "addpatient" -> addPatient();
                        case "addprovider" -> addProvider();
                        case "addtreatment" -> addTreatmentToDatabase();
                        case "help" -> help(inputs);
                        case "quit" -> {
                            return;
                        }
                        default -> msgln("Bad input.  Type \"help\" for more information.");
                    }
                }
            } catch (Exception e) {
                severe(e);
                err("Operation failed, see log file for details.");
            }
        }
    }
	
	private void connectToDatabase() {
		Map<String,String> properties = new HashMap<>();
		properties.put(JPA_SERVER_URL, serverUrl);
		properties.put(JPA_SERVER_USER, serverUser);
		properties.put(JPA_SERVER_PASSWORD, serverPassword);

        entityManagerFactory  = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
    }

    public interface DbUpdater {
        void exec(EntityManager entityManager) throws IOException;
    }

    private void runInTransaction(DbUpdater update) throws IOException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            update.exec(entityManager);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

	public void addPatient() throws IOException {
        runInTransaction(entityManager -> {

            Patient patient = patientFactory.createPatient();
            patient.setId(uuidGenerator.generate());
            msg("Name: ");
            patient.setName(in.readLine());

            patient.setDob(readDate("Patient DOB"));

            /*
             * Save the record in the database.
             */
            entityManager.persist(patient);

            msgln("Patient id: " + patient.getId());

        });
	}

	public void addProvider() throws IOException {
        runInTransaction(entityManager -> {

            Provider provider = providerFactory.createProvider();
            provider.setId(uuidGenerator.generate());
            msg("NPI: ");
            provider.setNpi(in.readLine());
            msg("Name: ");
            provider.setName(in.readLine());

            /*
             * Save the record in the database.
             */
            entityManager.persist(provider);

            msgln("Provider id: " + provider.getId());

        });
	}
	
	public void addTreatmentToDatabase() throws IOException {
        runInTransaction(entityManager -> {
            Treatment treatment = addTreatment(entityManager);
            if (treatment != null) {
                /*
                 * Save the record in the database.
                 */
                entityManager.persist(treatment);
            }
        });
	}
	
	public Treatment addTreatment(EntityManager entityManager) throws IOException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine();
		Treatment treatment = null;
		if ("D".equals(line)) {
			return addDrugTreatment(entityManager);
		} else if ("S".equals(line)) {
			treatment = addSurgeryTreatment(entityManager);
		} else if ("R".equals(line)) {
			treatment = addRadiologyTreatment(entityManager);
		} else if ("P".equals(line)) {
			treatment = addPhysiotherapyTreatment(entityManager);
		}

		if (treatment != null) {
			msgln("Adding follow-up treatments...");
			addTreatmentList(entityManager, treatment);
			msgln("...finished follow-up treatments");

		}
		return treatment;
    }

    /*
     * Use this to add a list of follow-up treatments.
     */
    public void addTreatmentList(EntityManager entityManager, Treatment parent) throws IOException {
        Treatment treatment = addTreatment(entityManager);
        while (treatment != null) {
            parent.addFollowupTreatment(treatment);
            treatment = addTreatment(entityManager);
        }
    }



    private void addBasicTreatmentInfo(EntityManager entityManager, Treatment treatment) throws IOException {
        treatment.setId(uuidGenerator.generate());

        msg("Patient ID: ");
        UUID patientId = UUID.fromString(in.readLine());
        Patient patient = getPatient(entityManager, patientId);
        patient.addTreatment(treatment);

        msg("Provider ID: ");
        UUID providerId = UUID.fromString(in.readLine());
        Provider provider = getProvider(entityManager, providerId);
        provider.addTreatment(treatment);

        msg("Diagnosis: ");
        treatment.setDiagnosis(in.readLine());
    }

	private DrugTreatment addDrugTreatment(EntityManager entityManager) throws IOException {
		DrugTreatment treatment = treatmentFactory.createDrugTreatment();

        addBasicTreatmentInfo(entityManager, treatment);

		msg("Drug: ");
		treatment.setDrug(in.readLine());
		
		msg("Dosage: ");
		treatment.setDosage(Float.parseFloat(in.readLine()));
		
		treatment.setStartDate(readDate("Start date"));
		
		treatment.setEndDate(readDate("End date"));
		
		msg("Frequency: ");
		treatment.setFrequency(Integer.parseInt(in.readLine()));

		return treatment;
	}

	private SurgeryTreatment addSurgeryTreatment(EntityManager entityManager) throws IOException {
        // TODO finish this

		return null;
	}

	private RadiologyTreatment addRadiologyTreatment(EntityManager entityManager) throws IOException {
        RadiologyTreatment treatment = null;  // TODO

        addBasicTreatmentInfo(entityManager,  treatment);

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.addTreatmentDate(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private PhysiotherapyTreatment addPhysiotherapyTreatment(EntityManager entityManager) throws IOException {
		PhysiotherapyTreatment treatment = null;  // TODO

        addBasicTreatmentInfo(entityManager, treatment);

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.addTreatmentDate(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private LocalDate readDate(String field) throws IOException {
		msg(String.format("%s (MM/dd/yyyy): ", field));
		String line = in.readLine();
		if (line == null || line.isBlank()) {
			return null;
		}
		return LocalDate.parse(line, dateFormatter);
	}

	private Patient getPatient(EntityManager entityManager, UUID patientId) throws IOException {
		TypedQuery<Patient> query = entityManager.createNamedQuery(SEARCH_PATIENT_QUERY, Patient.class);
		query.setParameter("patientId", patientId);
		List<Patient> results = query.getResultList();
		if (results.isEmpty()) {
			throw new IOException("Missing patient: " + patientId);
		}
		return results.getFirst();
	}

	private Provider getProvider(EntityManager entityManager, UUID providerId) throws IOException {
		TypedQuery<Provider> query = entityManager.createNamedQuery(SEARCH_PROVIDER_QUERY, Provider.class);
		query.setParameter("providerId", providerId);
		List<Provider> results = query.getResultList();
		if (results.isEmpty()) {
			throw new IOException("Missing provider: " + providerId);
		}
		return results.getFirst();
	}

	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  quit: exit the app");
		}
	}

}
