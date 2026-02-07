package ycyh.uniclub.domain.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private LeaveRequestStatus status = LeaveRequestStatus.PENDING;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
    
    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;
    
    @Column(name = "requested_at")
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}

enum LeaveRequestStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
