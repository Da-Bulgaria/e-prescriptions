package bg.ehealth.prescriptions.persistence.model.enums;

public enum MedicineStatus {
    ACTIVE("Активен"),
    NOT_ACTIVE("Не Активен")
    ;

    public static MedicineStatus fromString(String key) {
        if (ACTIVE.getLocalizedName().equalsIgnoreCase(key)) {
            return ACTIVE;
        } else {
            return NOT_ACTIVE;
        }
    }

    private final String localizedName;

    MedicineStatus(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getLocalizedName() {
        return localizedName;
    }

}
