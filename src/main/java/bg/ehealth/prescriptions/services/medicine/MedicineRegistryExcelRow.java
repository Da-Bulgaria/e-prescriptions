package bg.ehealth.prescriptions.services.medicine;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Valid
public class MedicineRegistryExcelRow {

    @NotBlank
    private final String id; //"Идентификатор на ЛП"
    @NotBlank
    private final String atcCode; // "Анатомо-терапевтичен код /АТС-код/"
    @NotBlank
    private final String inn; // "Международно непатентно наименование /INN/"
    @NotBlank
    private final String name; // "Наименование на лекарствения продукт"
    @NotBlank
    private final String icdCode; // "МКБ"
    @NotBlank
    private final String medicineStatus; // "Статус"

    public MedicineRegistryExcelRow(@NotBlank String id,
                                    @NotBlank String atcCode,
                                    @NotBlank String inn,
                                    @NotBlank String name,
                                    @NotBlank String icdCode,
                                    @NotBlank String medicineStatus) {
        this.id = id;
        this.atcCode = atcCode;
        this.inn = inn;
        this.name = name;
        this.icdCode = icdCode;
        this.medicineStatus = medicineStatus;
    }

    public String id() {
        return id;
    }

    public String atcCode() {
        return atcCode;
    }

    public String inn() {
        return inn;
    }

    public String name() {
        return name;
    }

    public String icdCode() {
        return icdCode;
    }

    public String medicineStatus() {
        return medicineStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineRegistryExcelRow that = (MedicineRegistryExcelRow) o;
        return id.equals(that.id) &&
                atcCode.equals(that.atcCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, atcCode);
    }

    @Override
    public String toString() {
        return "MedicineRegistryExcelRow{" +
                "id=" + id +
                ", atcCode='" + atcCode + '\'' +
                ", inn='" + inn + '\'' +
                ", name='" + name + '\'' +
                ", icdCode='" + icdCode + '\'' +
                ", medicineStatus=" + medicineStatus +
                '}';
    }
}
