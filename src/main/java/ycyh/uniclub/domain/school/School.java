package ycyh.uniclub.domain.school;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.domain.club.Club;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "school")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Long schoolId;
    
    @Column(name = "school_name", nullable = false, length = 150)
    private String schoolName;
    
    @Column(name = "campus_name", length = 150)
    private String campusName;
    
    @Column(length = 100)
    private String region;
    
    @Column(length = 120)
    private String domain;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<Club> clubs = new ArrayList<>();
}


