package ycyh.uniclub.domain.recruitment;

public enum ApplicationStatus {
    SUBMITTED("submitted"),
    UNDER_REVIEW("under_review"),
    APPROVED("approved"),
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


