package edu.stevens.cs548.clinic.service.impl;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import edu.stevens.cs548.clinic.domain.*;
import edu.stevens.cs548.clinic.domain.IPatientDao.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDao.ProviderExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDao.TreatmentExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.dto.DrugTreatmentDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.TreatmentDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.jboss.logging.Logger;

/**
 * CDI Bean implementation class ProviderService
 */
// TODO
public class ProviderService implements IProviderService {

    // TODO inject with constructor injection

    @SuppressWarnings("unused")
    private final Logger logger;

    private final IProviderDao providerDao;

    private final IPatientDao patientDao;

    // End TODO

    private final IProviderFactory providerFactory = new ProviderFactory();

    private final ProviderDtoFactory providerDtoFactory = new ProviderDtoFactory();

    private final TimeBasedEpochGenerator uuidGenerator = Generators.timeBasedEpochGenerator();



    /**
     * @see IProviderService#addProvider(ProviderDto dto)
     */
    @Override
    public UUID addProvider(ProviderDto dto) throws ProviderServiceExn {
        logger.info("Adding provider " + dto.getName());
        // Use factory to create Provider entity, and persist with DAO
        try {
            Provider provider = providerFactory.createProvider();
            if (dto.getId() == null) {
                provider.setId(uuidGenerator.generate());
            } else {
                provider.setId(dto.getId());
            }
            provider.setName(dto.getName());
            provider.setNpi(dto.getNpi());
            providerDao.addProvider(provider);
            return provider.getId();
        } catch (ProviderExn e) {
            throw new ProviderServiceExn("Failed to add provider", e);
        }
    }

    public List<ProviderDto> getProviders() throws ProviderServiceExn {
        logger.info("Getting all providers");
        try {
            List<ProviderDto> dtos = new ArrayList<ProviderDto>();
            Collection<Provider> providers = providerDao.getProviders();
            for (Provider provider : providers) {
                dtos.add(providerToDto(provider, false));
            }
            return dtos;
        } catch (TreatmentExn e) {
            throw new ProviderServiceExn("Failed to export treatment", e);
        }
    }

    /**
     * @see IProviderService#getProvider(UUID)
     */
    @Override
    /*
     * The boolean flag indicates if related treatments should be loaded eagerly.
     */
    public ProviderDto getProvider(UUID id, boolean includeTreatments) throws ProviderServiceExn {
        logger.info("Getting provider " + id);
        // TODO use DAO to get Provider by key

    }

    @Override
    /*
     * By default, we eagerly load related treatments with a provider record.
     */
    public ProviderDto getProvider(UUID id) throws ProviderServiceExn {
        return getProvider(id, true);
    }

    private ProviderDto providerToDto(Provider provider, boolean includeTreatments) throws TreatmentExn {
        ProviderDto dto = providerDtoFactory.createProviderDto();
        dto.setId(provider.getId());
        dto.setName(provider.getName());
        dto.setNpi(provider.getNpi());
        if (includeTreatments) {
            dto.setTreatments(provider.exportTreatments(TreatmentExporter.exportWithoutFollowups()));
        }
        return dto;
    }

    private UUID addTreatmentImpl(TreatmentDto dto, Consumer<Treatment> parentFollowUps)
            throws PatientServiceExn, ProviderServiceExn {

        logger.info("Adding treatment " + dto.getId());

        try {
            /*
             * Set the primary key here.
             */
            if (dto.getId() == null) {
                dto.setId(uuidGenerator.generate());
            }

            Provider provider = providerDao.getProvider(dto.getProviderId());
            Patient patient = patientDao.getPatient(dto.getPatientId());

            /*
             * Provider aggregate imports the treatment information and creates the treatment entity,
             * returning a consumer to add follow-up treatments.
             */
            Consumer<Treatment> followUpsConsumer;

            switch (dto) {

                case DrugTreatmentDto drugTreatmentDto -> {

                    followUpsConsumer = provider.importDrugTreatment(dto.getId(), patient, provider, dto.getDiagnosis(),
                            drugTreatmentDto.getDrug(), drugTreatmentDto.getDosage(), drugTreatmentDto.getStartDate(),
                            drugTreatmentDto.getEndDate(), drugTreatmentDto.getFrequency(), parentFollowUps);
                }
                /*
                 * TODO Handle the other cases
                 */



            }

            /*
             * Now add the follow-up treatments to the treatment entity via the consumer.
             */
            for (TreatmentDto followUp : dto.getFollowupTreatments()) {
                addTreatmentImpl(followUp, followUpsConsumer);
            }

            return dto.getId();

        } catch (PatientExn e) {
            throw new PatientNotFoundExn("Could not find patient for " + dto.getPatientId(), e);

        } catch (ProviderExn e) {
            throw new ProviderNotFoundExn("Could not find provider for " + dto.getProviderId(), e);
        }
    }

    @Override
    public UUID addTreatment(TreatmentDto dto) throws PatientServiceExn, ProviderServiceExn {
        return addTreatmentImpl(dto, null);
    }

    @Override
    public TreatmentDto getTreatment(UUID providerId, UUID tid)
            throws ProviderNotFoundExn, TreatmentNotFoundExn, ProviderServiceExn {
        // Export treatment DTO from Provider aggregate
        try {
            Provider provider = providerDao.getProvider(providerId);
            return provider.exportTreatment(tid, TreatmentExporter.exportWithFollowups());

        } catch (TreatmentExn e) {
            throw new TreatmentNotFoundExn("Could not find treatment for " + tid, e);

        } catch (ProviderExn e) {
            throw new ProviderNotFoundExn("Could not find provider for " + providerId, e);
        }
    }


    @Override
    public void removeAll() throws ProviderServiceExn {
        providerDao.deleteProviders();
    }

}
