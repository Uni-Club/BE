package ycyh.uniclub.domain.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.recruitment.Recruitment;
import ycyh.uniclub.domain.club.Club;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "application")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_id", nullable = false)
    @JsonIgnore
    private Recruitment recruitment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnore
    private Club club;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;
    
    @Column(columnDefinition = "TEXT")
    private String motivation;
    
    @Column(columnDefinition = "TEXT")
    private String answers; // JSON string for custom field answers
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
    
    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;
    
    @Column(name = "applied_at")
    @Builder.Default
    private LocalDateTime appliedAt = LocalDateTime.now();
    
    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    // Setter methods for status update
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}
