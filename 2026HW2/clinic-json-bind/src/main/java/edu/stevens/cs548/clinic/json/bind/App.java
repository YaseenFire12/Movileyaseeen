package edu.stevens.cs548.clinic.json.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import edu.stevens.cs548.clinic.service.dto.util.ObjectMapperFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	private final PatientDtoFactory patientFactory = new PatientDtoFactory();

	private final ProviderDtoFactory providerFactory = new ProviderDtoFactory();

	private final TreatmentDtoFactory treatmentFactory = new TreatmentDtoFactory();

    private final ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
	
	private final List<PatientDto> patients = new ArrayList<>();

	private final List<ProviderDto> providers = new ArrayList<>();

	private final List<TreatmentDto> treatments = new ArrayList<>();

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}


	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		new App(args);
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

	public App(String[] args) {

		// Main command-line interface loop

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
                        case "load" -> load(inputs);
                        case "save" -> save(inputs);
                        case "addpatient" -> addPatient();
                        case "addprovider" -> addProvider();
                        case "addtreatment" -> addOneTreatment();
                        case "list" -> list(inputs);
                        case "help" -> help(inputs);
                        case "quit" -> {
                            return;
                        }
                        default -> msgln("Bad input.  Type \"help\" for more information.");
                    }
				}
			} catch (Exception e) {
				severe(e);
			}
		}
	}

	public void load(String[] inputs) throws IOException, ParseException {
		if (inputs.length != 2) {
			err("Usage: load <filename>");
		}
		File input = new File(inputs[1]);
		if (!input.exists()) {
			err("File " + input + " does not exist!");
		}

        try (JsonParser jsonParser = mapper.getFactory().createParser(new BufferedReader(new FileReader(input)))) {

            if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                throw new ParseException("Expected start of object.", jsonParser.getTextOffset());
            }

            /*
             * Parse the list of patients.
             */
            jsonParser.nextToken();
            if (!PATIENTS.equals(jsonParser.currentName())) {
                throw new ParseException("Expected field: " + PATIENTS, jsonParser.getTextOffset());
            }

            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new ParseException("Expected start of array.", jsonParser.getTextOffset());
            }

            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                PatientDto patient = mapper.readValue(jsonParser, PatientDto.class);
                patients.add(patient);
            }

            /*
             * Parse the list of providers.
             */
            jsonParser.nextToken();
            if (!PROVIDERS.equals(jsonParser.currentName())) {
                throw new ParseException("Expected field: " + PROVIDERS, jsonParser.getTextOffset());
            }

            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new ParseException("Expected start of array.", jsonParser.getTextOffset());
            }

            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                ProviderDto provider = mapper.readValue(jsonParser, ProviderDto.class);
                providers.add(provider);
            }

            /*
             * TODO Load the treatment information
             */



            if (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                throw new ParseException("Expected end of object.", jsonParser.getTextOffset());
            }
        }
	}

	public void save(String[] inputs) throws IOException {
		if (inputs.length != 2) {
			err("Usage: save <filename>");
		}
		File output = new File(inputs[1]);
		try (JsonGenerator wr = mapper.getFactory().createGenerator(new BufferedWriter(new FileWriter(output)))) {
			wr.writeStartObject();

            wr.writeArrayFieldStart(PATIENTS);
			for (PatientDto patient : patients) {
                mapper.writeValue(wr, patient);
			}
            wr.writeEndArray();

            wr.writeArrayFieldStart(PROVIDERS);
            for (ProviderDto provider : providers) {
                mapper.writeValue(wr, provider);
            }
            wr.writeEndArray();

			/*
			 * TODO Save the treatment information.
			 */


            wr.writeEndObject();
		}
	}

	public void addPatient() throws IOException {
		PatientDto patient = patientFactory.createPatientDto();
		patient.setId(UUID.randomUUID());
		msg("Name: ");
		patient.setName(in.readLine());
		patient.setDob(readDate("Patient DOB"));
		patients.add(patient);
	}

	public void addProvider() throws IOException {
		ProviderDto provider = providerFactory.createProviderDto();
		provider.setId(UUID.randomUUID());
		msg("NPI: ");
		provider.setNpi(in.readLine());
		msg("Name: ");
		provider.setName(in.readLine());
		providers.add(provider);
	}
	
	/* 
	 * Use this to add a list of follow-up treatments.
	 */
	public void addTreatmentList(Collection<TreatmentDto> treatments) throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		while (treatment != null) {
			treatments.add(treatment);
			treatment = addTreatment();
		}
	}

	public void addOneTreatment() throws IOException, ParseException {
		TreatmentDto treatment = addTreatment();
		if (treatment != null) {
			treatments.add(treatment);
		}
	}
	
	public TreatmentDto addTreatment() throws IOException, ParseException {
		msg("What form of treatment: [D]rug, [S]urgery, [R]adiology, [P]hysiotherapy? ");
		String line = in.readLine().toUpperCase();
		TreatmentDto treatment = switch (line) {
            case "D" -> addDrugTreatment();
            case "S" -> addSurgeryTreatment();
            case "R" -> addRadiologyTreatment();
            case "P" -> addPhysiotherapyTreatment();
            default -> null;
        };

        if (treatment != null) {
			msgln("Adding follow-up treatments...");
			addTreatmentList(treatment.getFollowupTreatments());
			msgln("...finished follow-up treatments");

			addPatientTreatment(treatment);
			addPProviderTreatment(treatment);
		}
		return treatment;
	}

	private DrugTreatmentDto addDrugTreatment() throws IOException {
		DrugTreatmentDto treatment = treatmentFactory.createDrugTreatmentDto();

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());
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

	private SurgeryTreatmentDto addSurgeryTreatment() throws IOException {
		// TODO call factory method
        SurgeryTreatmentDto treatment = null;

		// TODO finish this



		return treatment;
	}

	private RadiologyTreatmentDto addRadiologyTreatment() throws IOException {
        // TODO call factory method
        RadiologyTreatmentDto treatment = null;

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
			date = readDate("Treatment date");
		}

		return treatment;
	}

	private PhysiotherapyTreatmentDto addPhysiotherapyTreatment() throws IOException {
        // TODO call factory method
        PhysiotherapyTreatmentDto treatment = null;

		treatment.setId(UUID.randomUUID());
		msg("Patient ID: ");
		treatment.setPatientId(UUID.fromString(in.readLine()));
		msg("Patient Name: ");
		treatment.setPatientName(in.readLine());
		msg("Provider ID: ");
		treatment.setProviderId(UUID.fromString(in.readLine()));
		msg("Provider Name: ");
		treatment.setProviderName(in.readLine());
		msg("Diagnosis: ");
		treatment.setDiagnosis(in.readLine());

		LocalDate date = readDate("Treatment date");
		while (date != null) {
			treatment.getTreatmentDates().add(date);
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

	private void addPatientTreatment(TreatmentDto treatment) {
		UUID patientId = treatment.getPatientId();
		for (PatientDto patient : patients) {
			if (patient.getId().equals(patientId)) {
				patient.addTreatment(treatment);
				return;
			}
		}
		msgln("No such patient id: " + patientId);
	}

	private void addPProviderTreatment(TreatmentDto treatment) {
		UUID providerId = treatment.getProviderId();
		for (ProviderDto provider : providers) {
			if (provider.getId().equals(providerId)) {
				provider.addTreatment(treatment);
				return;
			}
		}
		msgln("No such provider id: " + providerId);
	}

	public void list(String[] inputs) {
        try {
            msgln("Patients:");
            for (PatientDto patient : patients) {
                msgln(mapper.writeValueAsString(patient));
            }

            msgln("Providers:");
            for (ProviderDto provider : providers) {
                msgln(mapper.writeValueAsString(provider));
            }

            msgln("Treatments:");
            for (TreatmentDto treatment : treatments) {
                msgln(mapper.writeValueAsString(treatment));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void help(String[] inputs) {
		if (inputs.length == 1) {
			msgln("Commands are:");
			msgln("  load filename: load database from a file");
			msgln("  save filename: save database to a file");
			msgln("  addpatient: add a patient");
			msgln("  addprovider: add a provider");
			msgln("  addtreatment: add a treatment");
			msgln("  list: display database content");
			msgln("  quit: exit the app");
		}
	}

}
