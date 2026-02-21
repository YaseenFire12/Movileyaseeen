package edu.stevens.cs548.clinic.webapp;

import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import io.quarkus.qute.TemplateExtension;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

@TemplateExtension(namespace = "tmt")
public class Extensions {

    static String patientLink (URI baseUri, UUID patientId) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        return uriBuilder.segment("patient", patientId.toString()).build().toString();
    }

    static String providerLink (URI baseUri, UUID providerId) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        return uriBuilder.segment("provider", providerId.toString()).build().toString();
    }

    static String treatmentLink (URI baseUri, UUID providerId, UUID treatmentId) {
        UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        return uriBuilder.segment("provider", providerId.toString(), "treatment", treatmentId.toString()).build().toString();
    }

    static String treatmentType(TreatmentDto treatmentDto) {
        switch (treatmentDto) {
            case DrugTreatmentDto drugTreatmentDto -> {
                return "DRUGTREATMENT";
            }
            case PhysiotherapyTreatmentDto physiotherapyTreatmentDto -> {
                return "PHYSIOTHERAPY";
            }
            case RadiologyTreatmentDto radiologyTreatmentDto -> {
                return "RADIOLOGY";
            }
            case SurgeryTreatmentDto surgeryTreatmentDto -> {
                return "SURGERY";
            }
        }
    }
}
