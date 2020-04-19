package bg.ehealth.prescriptions.services.pharmacy.excel;

import com.google.common.base.Strings;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class PharmacyExcelRow {

    private final String identifier; // "№/№-промяна"
    private final String name; // "Име на фирма"
    private final String city; // "Адрес на аптека - град"
    private final String address; // "Адрес на аптека"

    public PharmacyExcelRow(@NotBlank String identifier,
                            @Nullable String name,
                            @NotBlank String city,
                            @NotBlank String address) {
        this.identifier = identifier.trim();
        if (name == null) {
            this.name = null;
        } else {
            this.name = name.trim();
        }
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
