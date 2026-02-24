package ycyh.uniclub.domain.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubSearchDto {
    private String keyword;
    private Long schoolId;
    private String category;
    private String tags;
}


