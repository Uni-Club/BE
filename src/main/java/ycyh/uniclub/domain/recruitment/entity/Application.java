package ycyh.uniclub.domain.recruitment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.group.entity.Group;
import ycyh.uniclub.domain.user.entity.User;
import ycyh.uniclub.domain.recruitment.enums.ApplicationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "application",
       uniqueConstraints = @UniqueConstraint(columnNames = {"recruitment_id", "applicant_id"}))
@Data
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
    private Recruitment recruitment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;
    
    @Column(columnDefinition = "TEXT")
    private String motivation;
    
    @Column(columnDefinition = "JSON")
    private String answers;
    
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
}

