package bg.ehealth.prescriptions.persistence.model;

import bg.ehealth.prescriptions.persistence.model.enums.MedicineStatus;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Valid
@Entity
public class Medicine implements Identifiable<Long> {

    @Id
    @NotNull
    private Long id; //"Идентификатор на ЛП"

    @NotBlank
    @Column(nullable = false)
    private String atcCode; // "Анатомо-терапевтичен код /АТС-код/"

    @NotBlank
    @Column(nullable = false)
    private String inn; // "Международно непатентно наименование /INN/"

    @NotBlank
    @Column(nullable = false)
    private String name; // "Наименование на лекарствения продукт"

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String icdCode; // "МКБ"

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MedicineStatus medicineStatus; // "Статус"

    public Medicine() {
    }

    public Medicine(@NotBlank Long id,
                    @NotBlank String atcCode,
                    @NotBlank String inn,
                    @NotBlank String name,
                    @NotBlank String icdCode,
                    @NotNull MedicineStatus medicineStatus) {
        this.id = id;
        this.atcCode = atcCode;
        this.inn = inn;
        this.name = name;
        this.icdCode = icdCode;
        this.medicineStatus = medicineStatus;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAtcCode() {
        return atcCode;
    }

    public void setAtcCode(String atcCode) {
        this.atcCode = atcCode;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcdCode() {
        return icdCode;
    }

    public void setIcdCode(String icdCode) {
        this.icdCode = icdCode;
    }

    public MedicineStatus getMedicineStatus() {
        return medicineStatus;
    }

    public void setMedicineStatus(MedicineStatus medicineStatus) {
        this.medicineStatus = medicineStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return id.equals(medicine.id) &&
                atcCode.equals(medicine.atcCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, atcCode);
    }
}

