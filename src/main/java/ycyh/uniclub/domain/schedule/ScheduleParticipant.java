package ycyh.uniclub.domain.schedule;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.User;

@Entity
@Table(name = "schedule_participant")
@IdClass(ScheduleParticipantId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "voted_option")
    private String votedOption;
}
