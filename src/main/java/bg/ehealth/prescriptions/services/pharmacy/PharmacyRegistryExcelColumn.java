package bg.ehealth.prescriptions.services.pharmacy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PharmacyRegistryExcelColumn {
    IDENTIFIER("№/№-промяна"),
    NAME("Име на фирма"),
    ADDRESS_CITY("Адрес на аптека - град"),
    ADDRESS("Адрес на аптека"),
    NULL("null")
    ;

    public static PharmacyRegistryExcelColumn fromString(String key) {
        return Arrays.stream(values())
                .filter(excelColumn -> excelColumn.getLocalizedName().equals(key.trim()))
                .findFirst()
                .orElse(NULL);
    }

    public static List<String> valuesAsString() {
        return Arrays.stream(values())
                .filter(column -> column != NULL)
                .map(PharmacyRegistryExcelColumn::getLocalizedName)
                .collect(Collectors.toList());
    }

    private final String localizedName;

    PharmacyRegistryExcelColumn(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

}
