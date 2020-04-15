package bg.ehealth.prescriptions.services.pharmacy.excel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PharmacyExcelColumn {
    IDENTIFIER("№/№-промяна"),
    NAME("Име на фирма"),
    ADDRESS_CITY("Адрес на аптека - град"),
    ADDRESS("Адрес на аптека"),
    NULL("null")
    ;

    public static PharmacyExcelColumn fromString(String key) {
        return Arrays.stream(values())
                .filter(excelColumn -> excelColumn.getLocalizedName().equals(key.trim()))
                .findFirst()
                .orElse(NULL);
    }

    public static List<String> valuesAsString() {
        return Arrays.stream(values())
                .filter(column -> column != NULL)
                .map(PharmacyExcelColumn::getLocalizedName)
                .collect(Collectors.toList());
    }

    private final String localizedName;

    PharmacyExcelColumn(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

}
