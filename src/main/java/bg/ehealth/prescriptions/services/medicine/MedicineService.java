package bg.ehealth.prescriptions.services.medicine;

import bg.ehealth.prescriptions.persistence.MedicineRepository;
import bg.ehealth.prescriptions.persistence.model.Medicine;
import bg.ehealth.prescriptions.services.medicine.excel.MedicineExcelSheet;
import bg.ehealth.prescriptions.services.medicine.excel.MedicineExcelColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.ehealth.prescriptions.persistence.model.enums.MedicineStatus.*;

@Service
public class MedicineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineService.class);

    private final MedicineExcelSheet registry;
    private final MedicineRepository repository;
    private final List<InputStream> inputStreams = new ArrayList<>();
    private final int rowNumberScan;

    public MedicineService(MedicineExcelSheet registry,
                           ResourceLoader resourceLoader,
                           MedicineRepository repository,
                           @Value("${medicine.excel.parser.columns.forwardRowNumberScan:15}") int rowNumberScan) throws IOException {
        this.registry = registry;
        this.repository = repository;
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie1.xls").getInputStream());
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie2.xls").getInputStream());
        this.inputStreams.add(resourceLoader
                .getResource("classpath:medicines/positivelist/prilozhenie3.xls").getInputStream());
        this.rowNumberScan = rowNumberScan;
    }

    public Set<Medicine> seedMedicines() {
        return registry.medicines(inputStreams, MedicineExcelColumn.valuesAsString(), rowNumberScan)
                .stream()
                .map(medicineRegistryExcelRow ->
                        new Medicine(
                                Long.valueOf(medicineRegistryExcelRow.id()),
                                medicineRegistryExcelRow.atcCode(),
                                medicineRegistryExcelRow.inn(),
                                medicineRegistryExcelRow.name(),
                                medicineRegistryExcelRow.icdCode(),
                                fromString(medicineRegistryExcelRow.medicineStatus())
                        )
                )
                .filter(medicine -> ACTIVE.equals(medicine.getMedicineStatus()))
                .collect(Collectors.toSet());
    }

    public List<Medicine> medicines() {
        return repository.findAll();
    }

    public List<Medicine> saveAll(Set<Medicine> medicines) {
        LOGGER.debug("Persisting {} medicines...", medicines.size());
        return repository.saveAll(medicines);
    }

}
