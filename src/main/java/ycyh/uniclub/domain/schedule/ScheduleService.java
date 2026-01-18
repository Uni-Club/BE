package ycyh.uniclub.domain.schedule;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.group.GroupRepository;
import ycyh.uniclub.domain.user.User;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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

        validateTimeRange(req.getStartAt(), req.getEndAt());

        Group group = groupRepository.findById(req.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. id=" + req.getGroupId()));

        Schedule schedule = Schedule.builder()
                .group(group)
                .title(req.getTitle())
                .description(req.getDescription())
                .startAt(req.getStartAt())
                .endAt((req.getEndAt()))
                .location((req.getLocation()))
                .createdBy(creator)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        return toResponse(saved);
    }

    // 그룹별 일정 목록 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByGroup(Long groupId) {

        // 그룹이 실제로 존재하는지 검증
        groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. id=" + groupId));

        List<Schedule> schedules = scheduleRepository
                .findByGroup_GroupIdOrderByStartAtAsc(groupId);

        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 특정 일정 상세 조회
    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleDetail(Long groupId, Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        // URL의 groupId와 실제 일정의 groupId가 일치하는지 검증
        if (!schedule.getGroup().getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹에 속한 일정이 아닙니다.");
        }

        return toResponse(schedule);
    }

    // 일정 수정
    public ScheduleResponseDto updateSchedule(Long groupId,
                                              Long scheduleId,
                                              ScheduleUpdateDto req,
                                              User user) {

        Schedule schedule = validateAndGetSchedule(groupId, scheduleId);

        validateScheduleOwner(schedule, user);
        validateTimeRange(req.getStartAt(), req.getEndAt());

        schedule.update(
                req.getTitle(),
                req.getDescription(),
                req.getStartAt(),
                req.getEndAt(),
                req.getLocation()
        );

        return toResponse(schedule);
    }

    // 일정 삭제
    public void deleteSchedule(Long groupId, Long scheduleId, User user) {

        Schedule schedule = validateAndGetSchedule(groupId, scheduleId);

        validateScheduleOwner(schedule, user);

        scheduleRepository.delete(schedule);
    }

    private ScheduleResponseDto toResponse(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .groupId(schedule.getGroup().getGroupId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startAt(schedule.getStartAt())
                .endAt(schedule.getEndAt())
                .location(schedule.getLocation())
                .createdByUserId(schedule.getCreatedBy().getUserId())
                .createdByName(schedule.getCreatedBy().getName())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }

    // ================== 공통 검증 메서드 ==================

    /**
     * 일정 검증 & 조회 메서드
     */
    private Schedule validateAndGetSchedule(Long groupId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        if (!schedule.getGroup().getGroupId().equals(groupId)) {
            throw new IllegalArgumentException("해당 그룹에 속한 일정이 아닙니다.");
        }

        return schedule;
    }

    /**
     * 일정 작성자인지 검증하는 메서드
     */
    private void validateScheduleOwner(Schedule schedule, User user) {
        if (!schedule.getCreatedBy().getUserId().equals(user.getUserId())) {
            try {
                throw new AccessDeniedException("일정에 대한 권한이 없습니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 시작/종료 시간 검증 메서드
     */
    private void validateTimeRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이후일 수 없습니다.");
        }
    }

    // ============= FE 호환용 메서드 (scheduleId만으로 동작) =============

    // 일정 생성 (groupId를 body에서 받음)
    public ScheduleResponseDto createScheduleSimple(ScheduleCreateDto req, User creator) {
        return createSchedule(req, creator);
    }

    // 일정 상세 조회 (scheduleId만으로)
    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));
        return toResponse(schedule);
    }

    // 일정 수정 (scheduleId만으로)
    public ScheduleResponseDto updateScheduleById(Long scheduleId, ScheduleUpdateDto req, User user) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        validateScheduleOwner(schedule, user);
        validateTimeRange(req.getStartAt(), req.getEndAt());

        schedule.update(
                req.getTitle(),
                req.getDescription(),
                req.getStartAt(),
                req.getEndAt(),
                req.getLocation()
        );

        return toResponse(schedule);
    }

    // 일정 삭제 (scheduleId만으로)
    public void deleteScheduleById(Long scheduleId, User user) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        validateScheduleOwner(schedule, user);

        scheduleRepository.delete(schedule);
    }
}
