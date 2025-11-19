package ycyh.uniclub.domain.group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCreateDto {
    private String groupName;
    private String description;
    private Long schoolId;
    private Boolean isUnion;
}
