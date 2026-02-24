package ycyh.uniclub.domain.club;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LeaveRequestDto {
    private Long requestId;
    private Long clubId;
    private String clubName;
    private Long userId;
    private String userName;
    private String userEmail;
    private String reason;
    private LeaveRequestStatus status;
    private String reviewerName;
    private String reviewNote;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    
    public static LeaveRequestDto from(LeaveRequest entity) {
        return LeaveRequestDto.builder()
                .requestId(entity.getRequestId())
                .clubId(entity.getClub().getClubId())
                .clubName(entity.getClub().getClubName())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getName())
                .userEmail(entity.getUser().getEmail())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .reviewerName(entity.getReviewer() != null ? entity.getReviewer().getName() : null)
                .reviewNote(entity.getReviewNote())
                .requestedAt(entity.getRequestedAt())
                .reviewedAt(entity.getReviewedAt())
                .build();
    }
}
