package edu.stevens.cs548.clinic.service;

import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import java.io.Serial;
import java.util.List;
import java.util.UUID;

public interface IProviderService {

    class ProviderServiceExn extends Exception {
        @Serial
        private static final long serialVersionUID = 1L;

        public ProviderServiceExn(String m, Exception e) {
            super(m, e);
        }

        public ProviderServiceExn(String m) {
            super(m);
        }
    }

    class ProviderNotFoundExn extends ProviderServiceExn {
        @Serial
        private static final long serialVersionUID = 1L;

        public ProviderNotFoundExn(String m, Exception e) {
            super(m, e);
        }

        public ProviderNotFoundExn(String m) {
            super(m);
        }
    }

    class TreatmentNotFoundExn extends ProviderServiceExn {
        @Serial
        private static final long serialVersionUID = 1L;

        public TreatmentNotFoundExn(String m, Exception e) {
            super(m, e);
        }

        public TreatmentNotFoundExn(String m) {
            super(m);
        }
    }

    UUID addProvider(ProviderDto dto) throws ProviderServiceExn;

    List<ProviderDto> getProviders() throws ProviderServiceExn;

    ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn;

    ProviderDto getProvider(UUID id) throws ProviderServiceExn;

    UUID addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn;

    TreatmentDto getTreatment(UUID providerId, UUID treatmentId) throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn;

    void removeAll() throws ProviderServiceExn;

}
