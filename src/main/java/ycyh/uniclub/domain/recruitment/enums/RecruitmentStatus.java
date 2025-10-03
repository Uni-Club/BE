package ycyh.uniclub.domain.recruitment.enums;

public enum RecruitmentStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    CLOSED("closed"),
    ARCHIVED("archived");
    
    private final String value;
    
    RecruitmentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
