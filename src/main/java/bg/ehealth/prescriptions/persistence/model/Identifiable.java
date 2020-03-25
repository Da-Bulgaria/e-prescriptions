package bg.ehealth.prescriptions.persistence.model;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

    T getId();
}