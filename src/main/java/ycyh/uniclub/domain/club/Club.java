package ycyh.uniclub.domain.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.school.School;
import ycyh.uniclub.domain.recruitment.Recruitment;
import ycyh.uniclub.domain.board.Board;
import ycyh.uniclub.domain.schedule.Schedule;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "club")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long clubId;
    
    @Column(name = "club_name", nullable = false, length = 100)
    private String clubName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<ClubMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Recruitment> recruitments = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Schedule> schedules = new ArrayList<>();

    @Column(name = "is_union")
    @Builder.Default
    private Boolean isUnion = false;
    
    // Helper method to get leader ID
    public Long getLeaderId() {
        return leader != null ? leader.getUserId() : null;
    }

    // Update mutable fields in-place to preserve collection relationships
    public void updateInfo(String clubName, String description) {
        if (clubName != null) {
            this.clubName = clubName;
        }
        if (description != null) {
            this.description = description;
        }
    }
}


