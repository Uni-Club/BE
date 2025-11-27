package ycyh.uniclub.domain.schedule;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.group.GroupRepository;
import ycyh.uniclub.domain.user.User;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;

    // 일정 생성
    public ScheduleResponseDto createSchedule(ScheduleCreateDto req, User creator) {

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. id=" + req.getGroupId()));

        Schedule schedule = Schedule.builder()
                .group(group)
                .title(req.getTitle())
                .description(req.getDescription())
                .date(req.getDate())
                .createdBy(creator)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        return toResponse(saved);
    }

    // 특정 그룹의 일정 목록 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByGroup(Long groupId) {

        // 그룹이 실제로 존재하는지 검증
        groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "그룹을 찾을 수 없습니다. id=" + groupId)
                );

        List<Schedule> schedules = scheduleRepository
                .findByGroup_GroupIdOrderByDateAsc(groupId);

        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ScheduleResponseDto toResponse(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .groupId(schedule.getGroup().getGroupId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .date(schedule.getDate())
                .createdByUserId(schedule.getCreatedBy().getUserId())
                .createdByName(schedule.getCreatedBy().getName())
                .build();
    }
}
