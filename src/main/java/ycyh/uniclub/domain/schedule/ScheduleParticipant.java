package ycyh.uniclub.domain.schedule;

import jakarta.persistence.*;
import lombok.*;
import ycyh.uniclub.domain.user.User;

@Entity
@Getter
@Table(name = "schedule_participant")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String votedOption;
}

