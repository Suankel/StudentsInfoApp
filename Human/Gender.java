package MainPackage.Human;

public enum Gender {

    MALE("Мужской"),
    FEMALE("Женский");

    private String type;

    Gender(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return type;
    }
}
