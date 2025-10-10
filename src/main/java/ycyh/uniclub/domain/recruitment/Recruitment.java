package ycyh.uniclub.domain.recruitment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.school.School;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recruitment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long recruitmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(length = 50)
    private String category;
    
    @Column(columnDefinition = "JSON")
    private String tags;
    
    @Column(name = "apply_start")
    private LocalDateTime applyStart;
    
    @Column(name = "apply_end")
    private LocalDateTime applyEnd;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private RecruitmentStatus status = RecruitmentStatus.DRAFT;
    
    private Integer capacity;
    
    @Builder.Default
    private Integer views = 0;

    @Column(name = "is_union")
    @Builder.Default
    private Boolean isUnion = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "recruitment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();
}


