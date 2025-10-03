package ycyh.uniclub.domain.group.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_member", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
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
