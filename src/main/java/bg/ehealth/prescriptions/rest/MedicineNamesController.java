package bg.ehealth.prescriptions.rest;

import bg.ehealth.prescriptions.services.medicine.MedicineNamesRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MedicineNamesController {

    private final MedicineNamesRegistry registry;

    public MedicineNamesController(MedicineNamesRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/medicine")
    public Set<String> nameStartWith(@RequestParam String prefix, @RequestParam(defaultValue = "20") int size) {
        return registry.medicineNames().stream()
                .filter(name -> name.startsWith(prefix))
                .limit(size)
                .collect(Collectors.toSet());
    }
}
