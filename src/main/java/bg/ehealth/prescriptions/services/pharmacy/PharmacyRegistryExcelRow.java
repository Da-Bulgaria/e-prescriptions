package bg.ehealth.prescriptions.services.pharmacy;

import com.google.common.base.Strings;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Valid
public class PharmacyRegistryExcelRow {

    @NotBlank
    private final String identifier;
    @Nullable
    private final String name;
    @NotBlank
    private final String city;
    @NotBlank
    private final String address;

    public PharmacyRegistryExcelRow(String identifier, String name, String city, String address) {
        this.identifier = identifier.trim();
        this.name = name.trim();
        this.city = city.trim();
        this.address = address.trim();
    }

    public String identifier() {
        return identifier;
    }

    public String name() {
        return name;
    }

    public String city() {
        return city;
    }

    public String address() {
        return address;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(identifier)
                && Strings.isNullOrEmpty(name)
                && Strings.isNullOrEmpty(city)
                && Strings.isNullOrEmpty(address);
    }

    @Override
    public String toString() {
        return "PharmacyRegistryExcelRow{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
