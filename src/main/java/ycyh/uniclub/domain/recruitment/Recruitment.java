package ycyh.uniclub.domain.recruitment;

import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.school.School;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.domain.application.Application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "recruitment")
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
    
    @Column(columnDefinition = "TEXT")
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
    
    @Column(name = "custom_fields", columnDefinition = "TEXT")
    private String customFields; // JSON string for field definitions

    // Setter methods for update
    public void setStatus(RecruitmentStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setApplyStart(LocalDateTime applyStart) {
        this.applyStart = applyStart;
    }

    public void setApplyEnd(LocalDateTime applyEnd) {
        this.applyEnd = applyEnd;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }
}


