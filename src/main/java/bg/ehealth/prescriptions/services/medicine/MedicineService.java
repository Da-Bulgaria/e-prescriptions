package bg.ehealth.prescriptions.services.medicine;

import bg.ehealth.prescriptions.persistence.MedicineRepository;
import bg.ehealth.prescriptions.persistence.model.Medicine;
import bg.ehealth.prescriptions.persistence.model.enums.MedicineStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MedicineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineService.class);

    private final MedicineRegistry registry;
    private final MedicineRepository repository;
    private final List<InputStream> inputStreams = new ArrayList<>();

    public MedicineService(MedicineRegistry registry, ResourceLoader resourceLoader, MedicineRepository repository) throws IOException {
        this.registry = registry;
        this.repository = repository;
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie1.xls").getInputStream());
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie2.xls").getInputStream());
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie3.xls").getInputStream());
    }

    public void importMedicines() {
        Set<Medicine> medicines = registry.medicines(inputStreams, MedicineRegistryExcelColumn.valuesAsString())
                .stream()
                .map(m -> new Medicine(Long.valueOf(m.id()), m.atcCode(), m.inn(), m.name(), m.icdCode(),
                        MedicineStatus.fromString(m.medicineStatus())))
                .collect(Collectors.toSet());

        LOGGER.debug("Persisting {} medicines...", medicines.size());
        repository.saveAll(medicines);
        LOGGER.debug("done.");
    }

}
