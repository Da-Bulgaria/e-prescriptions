package bg.ehealth.prescriptions.persistence.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Builder
public class Pharamacist implements Identifiable<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String uid;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    private String middleName;
}
