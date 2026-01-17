package ycyh.uniclub.domain.application;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationReviewDto {
    private ApplicationStatus status;
    private String memo;
    private String reviewNote; // FE 호환

    // FE 호환: reviewNote 또는 memo 둘 다 지원
    public String getReviewMessage() {
        return reviewNote != null ? reviewNote : memo;
    }
}
