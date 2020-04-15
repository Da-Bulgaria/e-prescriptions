package bg.ehealth.prescriptions.services.medicine;

import bg.ehealth.prescriptions.persistence.model.Medicine;
import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;
import bg.ehealth.prescriptions.services.medicine.nonproprietary.NonProprietaryMedicineNamesService;
import com.google.common.base.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MedicineNamesRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(MedicineNamesRegistry.class);

    private final NonProprietaryMedicineNamesService nonProprietaryMedicineNamesService;
    private final MedicineService medicineService;
    private final Supplier<Set<String>> namesRegistry;

    public MedicineNamesRegistry(NonProprietaryMedicineNamesService nonProprietaryMedicineNamesService,
                                 MedicineService medicineService,
                                 @Value("${medicine.names.cache.period:3600}") long duration) {
        this.nonProprietaryMedicineNamesService = nonProprietaryMedicineNamesService;
        this.medicineService = medicineService;
        final Supplier<Set<String>> supplier = this::loadMedicineNames;
        if (duration == 0) {
            LOGGER.info("initializing MedicineNamesRegistry with no caching period");
            this.namesRegistry = supplier;
        } else {
            LOGGER.info("initializing MedicineNamesRegistry with caching period: {}", duration);
            this.namesRegistry = Suppliers.memoizeWithExpiration(supplier::get, duration, TimeUnit.SECONDS);
        }
    }

    public Set<String> medicineNames() {
        return namesRegistry.get();
    }

    private Set<String> loadMedicineNames() {
        return Stream.concat(
                nonProprietaryMedicineNamesService.nonProprietaryMedicineNames()
                        .stream()
                        .map(NonProprietaryMedicineName::getName),
                medicineService.medicines()
                        .stream()
                        .map(Medicine::getName)
        ).collect(Collectors.toSet());
    }
}
