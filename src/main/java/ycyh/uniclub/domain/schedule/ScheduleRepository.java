package ycyh.uniclub.domain.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 그룹의 모든 일정을 날짜 오름차순으로 조회
    List<Schedule> findByGroup_GroupIdOrderByStartAtAsc(Long groupId);
}
