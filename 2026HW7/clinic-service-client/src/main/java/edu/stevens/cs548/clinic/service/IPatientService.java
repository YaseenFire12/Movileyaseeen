package edu.stevens.cs548.clinic.service;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

import edu.stevens.cs548.clinic.service.dto.PatientDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;

public interface IPatientService {

    class PatientServiceExn extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        public PatientServiceExn(String m, Exception e) {
            super(m, e);
        }

        public PatientServiceExn(String m) {
            super(m);
        }
    }

    class PatientNotFoundExn extends PatientServiceExn {
        @Serial
        private static final long serialVersionUID = 1L;

        public PatientNotFoundExn(String m, Exception e) {
            super(m, e);
        }

        public PatientNotFoundExn(String m) {
            super(m);
        }

    }

    class TreatmentNotFoundExn extends PatientServiceExn {
        @Serial
        private static final long serialVersionUID = 1L;

        public TreatmentNotFoundExn(String m, Exception e) {
            super(m, e);
        }

        public TreatmentNotFoundExn(String m) {
            super(m);
        }
    }
	
    UUID addPatient(PatientDto dto) throws PatientServiceExn;

    List<PatientDto> getPatients() throws PatientServiceExn;
	
    PatientDto getPatient(UUID id, boolean includeTreatments) throws PatientServiceExn;
	
    PatientDto getPatient(UUID id) throws PatientServiceExn;
	
    TreatmentDto getTreatment(UUID patientId, UUID treatmentId) throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn;

    void removeAll() throws PatientServiceExn;
	
}
