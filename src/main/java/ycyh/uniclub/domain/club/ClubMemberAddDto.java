package ycyh.uniclub.domain.club;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubMemberAddDto {
    private Long userId;
    private String role; // "회장", "부회장", "관리자", "부원"
}
