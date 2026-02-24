package ycyh.uniclub.domain.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubCreateDto {
    private String clubName;
    private String description;
    private Long schoolId;
    private Boolean isUnion;
}
