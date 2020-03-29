package bg.ehealth.prescriptions.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Medicine implements Identifiable<String> {

    @Id
    private String atcCode;
    
    @Column
    private String inn;
    
    @Column
    private String name;

    @Column
    private String icdCode;
    
    public String getId() {
        return atcCode;
    }

}
