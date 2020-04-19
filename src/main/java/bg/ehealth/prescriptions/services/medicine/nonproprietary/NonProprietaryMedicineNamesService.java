package bg.ehealth.prescriptions.services.medicine.nonproprietary;

import bg.ehealth.prescriptions.gateways.cdc.CdcGateway;
import bg.ehealth.prescriptions.gateways.github.GithubGateway;
import bg.ehealth.prescriptions.persistence.NonProprietaryMedicineNameRepository;
import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;
import bg.ehealth.prescriptions.services.medicine.nonproprietary.cdc.MedicineXmlMedia;
import bg.ehealth.prescriptions.services.medicine.nonproprietary.pd.MedicineTsvTableMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NonProprietaryMedicineNamesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonProprietaryMedicineNamesService.class);

    private final MedicineTsvTableMedia medicineTsvTableMedia;
    private final MedicineXmlMedia medicineXmlMedia;
    private final CdcGateway cdcGateway;
    private final GithubGateway githubGateway;
    private final NonProprietaryMedicineNameRepository repository;

    public NonProprietaryMedicineNamesService(MedicineTsvTableMedia medicineTsvTableMedia,
                                              MedicineXmlMedia medicineXmlMedia,
                                              CdcGateway cdcGateway,
                                              GithubGateway githubGateway,
                                              NonProprietaryMedicineNameRepository repository) {
        this.medicineTsvTableMedia = medicineTsvTableMedia;
        this.medicineXmlMedia = medicineXmlMedia;
        this.cdcGateway = cdcGateway;
        this.githubGateway = githubGateway;
        this.repository = repository;
    }

    public @NotNull Set<NonProprietaryMedicineName> seedNonProprietaryMedicineNames() {
        return Stream.concat(
                medicineTsvTableMedia.medicineNames(githubGateway.pdTableContent()).stream(),
                medicineXmlMedia.medicineNames(cdcGateway.cdcMedicineRegistry()).stream()
        ).collect(Collectors.toSet());
    }

    public @NotNull List<NonProprietaryMedicineName> nonProprietaryMedicineNames() {
        return repository.findAll();
    }

    public List<NonProprietaryMedicineName> saveAll(Set<NonProprietaryMedicineName> names) {
        LOGGER.debug("Persisting {} non-proprietary medicine names...", names.size());
        return repository.saveAll(names);
    }

}
