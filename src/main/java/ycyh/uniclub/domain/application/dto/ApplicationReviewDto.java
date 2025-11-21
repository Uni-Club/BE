package ycyh.uniclub.domain.application.dto;

import lombok.Getter;
import lombok.Setter;
import ycyh.uniclub.domain.application.ApplicationStatus;

@Getter
@Setter
public class ApplicationReviewDto {
    private ApplicationStatus status;
    private String memo;
}
