package edu.stevens.cs548.clinic.rest.client.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.ObjectMapperFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WebClient {

    protected final Logger logger = Logger.getLogger(WebClient.class.getCanonicalName());

    /*
     * HTTP response header for 201 status
     */
    private static final String LOCATION = "Location";

    /*
     * The client stub used for Web service calls.
     */
    private final IServerApi client;

    public WebClient(URI baseUri) {
        /*
         * Create the HTTP client stub.
         */
        OkHttpClient httpClient = new OkHttpClient.Builder().build();

        /*
         * Gson converter
         */
        ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUri.toString())
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        /*
         * Create the stub that will be used for Web service calls
         */
        client = retrofit.create(IServerApi.class);
    }

    public URI addPatient(PatientDto patientDto) throws IOException {
        Response<Void> response = client.addPatient(patientDto).execute();
        if (response.isSuccessful()) {
            return URI.create(Objects.requireNonNull(response.headers().get(LOCATION)));
        } else {
            throw new IOException("Web service (POST /.../patient) failure: " + response.code());
        }
    }

    public URI addProvider(ProviderDto providerDto) throws IOException {
        Response<Void> response = client.addProvider(providerDto).execute();
        if (response.isSuccessful()) {
            return URI.create(Objects.requireNonNull(response.headers().get(LOCATION)));
        } else {
            throw new IOException("Web service (POST /.../provider) failure: " + response.code());
        }
    }

    public URI addTreatment(TreatmentDto treatmentDto) throws IOException {
        Response<Void> response = client.addTreatment(
                treatmentDto.getProviderId().toString(),
                treatmentDto).execute();

        if (response.isSuccessful()) {
            return URI.create(Objects.requireNonNull(response.headers().get(LOCATION)));
        } else {
            throw new IOException("Web service (POST /.../treatment) failure: " + response.code());
        }
    }

}
