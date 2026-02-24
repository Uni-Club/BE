package ycyh.uniclub.domain.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "club_member",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    @JsonIgnore
    private Club club;
    
    @Column(length = 50)
    @Builder.Default
    private String role = "부원";
    
    @Column(length = 30)
    @Builder.Default
    private String status = "active";
    
    @Column(name = "is_finance_admin")
    @Builder.Default
    private Boolean isFinanceAdmin = false;
    
    @Column(name = "joined_at")
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();
}


