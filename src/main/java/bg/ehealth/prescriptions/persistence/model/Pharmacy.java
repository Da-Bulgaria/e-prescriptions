package bg.ehealth.prescriptions.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Valid
@Entity
public class Pharmacy implements Identifiable<String> {

    @Id
    @NotBlank
    private String identifier;
    private String name;
    private String city;
    private String address;

    public Pharmacy() {
    }

    public Pharmacy(String identifier, String name, String city, String address) {
        this.identifier = identifier;
        this.name = name;
        this.city = city;
        this.address = address;
    }

    @Override
    public String getId() {
        return identifier;
    }

    public void setId(String id) {
        this.identifier = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pharmacy pharmacy = (Pharmacy) o;
        return identifier.equals(pharmacy.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
