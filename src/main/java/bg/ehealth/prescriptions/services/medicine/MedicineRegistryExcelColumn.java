package bg.ehealth.prescriptions.services.medicine;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MedicineRegistryExcelColumn {
    ATC_CODE("Анатомо-терапевтичен код /АТС-код/"),
    INN("Международно непатентно наименование /INN/"),
    NAME("Наименование на лекарствения продукт"),
    ICD_CODE("МКБ"),
    STATUS("Статус"),
    IDENTIFIER("Идентификатор на ЛП"),
    NULL("null")
    ;

    public static MedicineRegistryExcelColumn fromString(String key) {
        return Arrays.stream(values())
                .filter(excelColumn -> excelColumn.getLocalizedName().equals(key.trim()))
                .findFirst()
                .orElse(NULL);
    }

    public static List<String> valuesAsString() {
        return Arrays.stream(values())
                .filter(column -> column != NULL)
                .map(MedicineRegistryExcelColumn::getLocalizedName)
                .collect(Collectors.toList());
    }

    private final String localizedName;

    MedicineRegistryExcelColumn(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

}
