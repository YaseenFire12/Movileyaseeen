package edu.stevens.cs548.clinic.micro.domain;

import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class ProviderResponseExceptionMapper  implements ResponseExceptionMapper<ProviderServiceExn> {
    @Override
    public ProviderServiceExn toThrowable(Response response) {
        if (response.getStatus() >= 400) {
            return new ProviderServiceExn("Provider microservice responded with HTTP status "+response.getStatus());
        }
        return null;
    }
}
