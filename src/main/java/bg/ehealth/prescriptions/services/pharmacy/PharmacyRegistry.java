package bg.ehealth.prescriptions.services.pharmacy;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.List;

public interface PharmacyRegistry {

    @NotNull List<PharmacyRegistryExcelRow> pharmacies(@NotNull InputStream inputStream, List<String> columnNames);
}
