package ycyh.uniclub.domain.group.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.entity.User;
import ycyh.uniclub.domain.school.entity.School;
import ycyh.uniclub.domain.recruitment.entity.Recruitment;
import ycyh.uniclub.domain.board.entity.Board;
import ycyh.uniclub.domain.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;
    
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
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Recruitment> recruitments = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();
    
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();
}

