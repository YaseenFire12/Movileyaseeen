package edu.stevens.cs548.clinic.json.schema;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import edu.stevens.cs548.clinic.service.dto.util.ObjectMapperFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

	public static final String PATIENTS = "patients";

	public static final String PROVIDERS = "providers";

	public static final String TREATMENTS = "treatments";

	public static final String BASE_URI = "https://cs548.stevens.edu/clinic";

    public static final String LOCATION_PREFIX = "classpath:schema/";

	public static final String PATIENT_SCHEMA = BASE_URI + "/patient";

    public static final String PATIENT_SCHEMA_LOCATION = LOCATION_PREFIX + "patient-schema.json";

	public static final String PROVIDER_SCHEMA = BASE_URI + "/provider";

    public static final String PROVIDER_SCHEMA_LOCATION = LOCATION_PREFIX + "provider-schema.json";

    public static final String TREATMENT_SCHEMA = BASE_URI + "/treatment";

    public static final String TREATMENT_SCHEMA_LOCATION = LOCATION_PREFIX + "treatment-schema.json";

    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	public void severe(String s) {
		logger.severe(s);
	}

	public void severe(Exception e) {
		logger.log(Level.SEVERE, "Error during processing!", e);
	}

	public void warning(String s) {
		logger.info(s);
	}

	public void info(String s) {
		logger.info(s);
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

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			new App(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public App(String[] args) throws IOException, ParseException {

        if (args.length != 1) {
            err("Usage: java -jar jsonschema.jar <filename>");
            System.exit(-1);
        }
        File input = new File(args[0]);
        if (!input.exists()) {
            err("File " + input + " does not exist!");
        }

        logger.info("Loading schemas....");

        HashMap<String,String> schemaMappings = HashMap.newHashMap(3);
        schemaMappings.put(PATIENT_SCHEMA, PATIENT_SCHEMA_LOCATION);
        schemaMappings.put(PROVIDER_SCHEMA, PROVIDER_SCHEMA_LOCATION);
        schemaMappings.put(TREATMENT_SCHEMA, TREATMENT_SCHEMA_LOCATION);

        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012,
                builder -> builder.schemaMappers(
                        schemaMappers -> schemaMappers.mappings(schemaMappings)));

        JsonSchema patientSchema = schemaFactory.getSchema(SchemaLocation.of(PATIENT_SCHEMA));

		JsonSchema providerSchema = schemaFactory.getSchema(SchemaLocation.of(PROVIDER_SCHEMA));

        JsonSchema treatmentSchema = schemaFactory.getSchema(SchemaLocation.of(TREATMENT_SCHEMA));

        patientSchema.initializeValidators();
        providerSchema.initializeValidators();
        treatmentSchema.initializeValidators();

        logger.info("Validating data....");

        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

        try (JsonParser jsonParser = mapper.getFactory().createParser(new BufferedReader(new FileReader(input)))) {

            if (jsonParser.nextToken() != JsonToken.START_OBJECT) {
                throw new ParseException("Expected start of object.", jsonParser.getTextOffset());
            }

            validateList(patientSchema, PATIENTS, jsonParser, mapper);

            validateList(providerSchema, PROVIDERS, jsonParser, mapper);

            validateList(treatmentSchema, TREATMENTS, jsonParser, mapper);

            if (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                throw new ParseException("Expected end of object.", jsonParser.getTextOffset());
            }
		}
		
	}

    private void validateList(JsonSchema schema,  String fieldName, JsonParser jsonParser, ObjectMapper mapper) throws IOException, ParseException {
        jsonParser.nextToken();
        if (!fieldName.equals(jsonParser.currentName())) {
            throw new ParseException("Expected field: " + fieldName, jsonParser.getTextOffset());
        }

        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw new ParseException("Expected start of array.", jsonParser.getTextOffset());
        }

        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            JsonNode json = mapper.readTree(jsonParser);
            msgln(json.toString());
            validate(schema, json);
        }
    }

    private void validate(JsonSchema schema, JsonNode json) {

        Set<ValidationMessage> validationResult = schema.validate(json);

        if (validationResult.isEmpty()) {
            msgln("Validation succeeded!");
        } else {
            msgln("Validation failed!");
            for (ValidationMessage message : validationResult) {
                msgln(message.getMessage());
            }
        }

        msgln("");

    }
}
