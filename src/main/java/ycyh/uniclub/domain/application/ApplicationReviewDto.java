package ycyh.uniclub.domain.application;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationReviewDto {
    private ApplicationStatus status;
    private String memo;
}
