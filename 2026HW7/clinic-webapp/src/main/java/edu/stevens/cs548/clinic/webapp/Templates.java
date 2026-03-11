package edu.stevens.cs548.clinic.webapp;

import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.PhysiotherapyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.RadiologyTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.SurgeryTreatmentDto;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import java.net.URI;
import java.util.List;

@CheckedTemplate
public class Templates {
    public static native TemplateInstance patients(URI uri, List<PatientDto> patients);

    public static native TemplateInstance patient(URI uri,  PatientDto patient);

    public static native TemplateInstance providers(URI uri, List<ProviderDto> providers);

    public static native TemplateInstance provider(URI uri, ProviderDto provider);

    public static native TemplateInstance drugTreatment(URI uri, DrugTreatmentDto treatment);
    public static native TemplateInstance physiotherapy(URI uri, PhysiotherapyTreatmentDto treatment);
    public static native TemplateInstance radiology(URI uri, RadiologyTreatmentDto treatment);
    public static native TemplateInstance surgery(URI uri, SurgeryTreatmentDto treatment);
}
