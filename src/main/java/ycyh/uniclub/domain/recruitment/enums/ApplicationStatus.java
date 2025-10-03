package ycyh.uniclub.domain.recruitment.enums;

public enum ApplicationStatus {
    SUBMITTED("submitted"),
    UNDER_REVIEW("under_review"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    CANCELLED("cancelled");
    
    private final String value;
    
    ApplicationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
