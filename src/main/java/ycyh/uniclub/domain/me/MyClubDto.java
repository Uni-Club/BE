package ycyh.uniclub.domain.me;

import lombok.Builder;
import lombok.Getter;
import ycyh.uniclub.domain.club.ClubMember;

@Getter
@Builder
public class MyClubDto {
    private Long clubId;
    private String clubName;
    private Integer memberCount;
    private String role;

    public static MyClubDto from(ClubMember gm) {
        return MyClubDto.builder()
                .clubId(gm.getClub().getClubId())
                .clubName(gm.getClub().getClubName())
                .memberCount(gm.getClub().getMembers() != null ? gm.getClub().getMembers().size() : 0)
                .role(gm.getRole())
                .build();
    }
}


