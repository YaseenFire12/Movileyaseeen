package edu.stevens.cs548.clinic.micro.domain.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

// TODO

public class ReadinessCheck implements HealthCheck {
	
	private final Logger logger;

    private static final String DATABASE_HOST_PROPERTY = "database.host";

    private static final String DATABASE_PORT_PROPERTY = "database.port";

	private static final String READINESS_CHECK_NAME = "Database Readiness Check";
	
	private static final String ERROR_KEY = "error";

    public ReadinessCheck(Logger logger) {
        this.logger = logger;
    }

    // TODO inject config property
    String databaseHost;

    // TODO inject config property
    int databasePort;

	@Override
	public HealthCheckResponse call() {
		HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(READINESS_CHECK_NAME);
		try {
			
			pingServer();
			
			logger.info("Readiness check for database succeeded!");
			return responseBuilder.up().build();
			
		} catch (IOException e) {
			
			return responseBuilder.down().withData(ERROR_KEY, e.getMessage()).build();
			
		}
	}
	
	private void pingServer() throws UnknownHostException, IOException {
		try (Socket socket = new Socket(databaseHost, databasePort)) {
            logger.infof("Connected to database at host %s and port %d", databaseHost, databasePort);
        }
	}

}
