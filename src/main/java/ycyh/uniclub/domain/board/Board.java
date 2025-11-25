package ycyh.uniclub.domain.board;

import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.group.Group;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "board")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "board_type", length = 20)
    @Builder.Default
    private BoardType boardType = BoardType.FREE;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private PostVisibility visibility = PostVisibility.GROUP_ONLY;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();
}


