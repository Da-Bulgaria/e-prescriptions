package bg.ehealth.prescriptions.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class NonProprietaryMedicineName implements Identifiable<Long>{

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    public NonProprietaryMedicineName() {
    }

    public NonProprietaryMedicineName(@NotBlank String name) {
        this.name = name.toLowerCase().trim();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
