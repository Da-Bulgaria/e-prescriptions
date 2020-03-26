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
public class Organization implements Identifiable<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    // TODO: add other necessary fields
}
