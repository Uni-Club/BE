package ycyh.uniclub.domain.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveRequestReviewDto {
    private String action; // "APPROVE" or "REJECT"
    private String reviewNote;
}
