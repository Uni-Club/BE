package ycyh.uniclub.domain.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleParticipantId implements Serializable {

    private Long schedule;
    private Long user;
}
