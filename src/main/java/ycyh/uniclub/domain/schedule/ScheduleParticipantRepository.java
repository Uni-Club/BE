package ycyh.uniclub.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, ScheduleParticipantId> {
    // 예: 특정 일정의 참여자 목록
    // List<ScheduleParticipant> findBySchedule_ScheduleId(Long scheduleId);
}
