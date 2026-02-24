package ycyh.uniclub.domain.schedule;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.club.Club;
import ycyh.uniclub.domain.club.ClubMember;
import ycyh.uniclub.domain.club.ClubMemberRepository;
import ycyh.uniclub.domain.club.ClubRepository;
import ycyh.uniclub.domain.notification.NotificationService;
import ycyh.uniclub.domain.notification.NotificationType;
import ycyh.uniclub.domain.user.User;

import org.springframework.http.HttpStatus;
import ycyh.uniclub.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final NotificationService notificationService;

    // 일정 생성
    public ScheduleResponseDto createSchedule(ScheduleCreateDto req, User creator) {

        validateTimeRange(req.getStartAt(), req.getEndAt());

        Club club = clubRepository.findById(req.getClubId())
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. id=" + req.getClubId()));

        Schedule schedule = Schedule.builder()
                .club(club)
                .title(req.getTitle())
                .description(req.getDescription())
                .startAt(req.getStartAt())
                .endAt((req.getEndAt()))
                .location((req.getLocation()))
                .createdBy(creator)
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        // 알림 트리거: 멤버들에게 일정 생성 알림
        String content = String.format("'%s' 동아리에 새 일정이 등록되었습니다: %s", club.getClubName(), saved.getTitle());
        String relatedUrl = String.format("/api/clubs/%d/schedules/%d", club.getClubId(), saved.getScheduleId()); // 라우팅에 맞게 조정
        notifyClubMembers(club.getClubId(), NotificationType.SCHEDULE_CREATED, content, relatedUrl);

        return toResponse(saved);
    }

    // 그룹별 일정 목록 조회
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByClub(Long clubId) {

        // 그룹이 실제로 존재하는지 검증
        clubRepository.findById(clubId)
                .orElseThrow(() -> new EntityNotFoundException("그룹을 찾을 수 없습니다. id=" + clubId));

        List<Schedule> schedules = scheduleRepository
                .findByClub_ClubIdOrderByStartAtAsc(clubId);

        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 특정 일정 상세 조회
    @Transactional(readOnly = true)
    public ScheduleResponseDto getScheduleDetail(Long clubId, Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        // URL의 clubId와 실제 일정의 clubId가 일치하는지 검증
        if (!schedule.getClub().getClubId().equals(clubId)) {
            throw new IllegalArgumentException("해당 그룹에 속한 일정이 아닙니다.");
        }

        return toResponse(schedule);
    }

    // 일정 수정
    public ScheduleResponseDto updateSchedule(Long clubId,
                                              Long scheduleId,
                                              ScheduleUpdateDto req,
                                              User user) {

        Schedule schedule = validateAndGetSchedule(clubId, scheduleId);

        validateScheduleOwner(schedule, user);
        validateTimeRange(req.getStartAt(), req.getEndAt());

        schedule.update(
                req.getTitle(),
                req.getDescription(),
                req.getStartAt(),
                req.getEndAt(),
                req.getLocation()
        );

        // 알림 트리거: 멤버들에게 일정 변경 알림
        String afterTitle = schedule.getTitle();
        String content = String.format("'%s' 동아리 일정이 변경되었습니다: %s", schedule.getClub().getClubName(), afterTitle);
        String relatedUrl = String.format("/api/clubs/%d/schedules/%d", clubId, scheduleId);
        notifyClubMembers(clubId, NotificationType.SCHEDULE_UPDATED, content, relatedUrl);

        return toResponse(schedule);
    }

    // 일정 삭제
    public void deleteSchedule(Long clubId, Long scheduleId, User user) {

        Schedule schedule = validateAndGetSchedule(clubId, scheduleId);

        validateScheduleOwner(schedule, user);

        String title = schedule.getTitle();
        String clubName = schedule.getClub().getClubName();

        scheduleRepository.delete(schedule);

        // 알림 트리거: 멤버들에게 일정 취소 알림
        String content = String.format("'%s' 동아리 일정이 취소되었습니다: %s", clubName, title);
        String relatedUrl = String.format("/api/clubs/%d/schedules", clubId);
        notifyClubMembers(clubId, NotificationType.SCHEDULE_DELETED, content, relatedUrl);
    }

    private ScheduleResponseDto toResponse(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .scheduleId(schedule.getScheduleId())
                .clubId(schedule.getClub().getClubId())
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
    private Schedule validateAndGetSchedule(Long clubId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new EntityNotFoundException("일정을 찾을 수 없습니다. id=" + scheduleId));

        if (!schedule.getClub().getClubId().equals(clubId)) {
            throw new IllegalArgumentException("해당 그룹에 속한 일정이 아닙니다.");
        }

        return schedule;
    }

    /**
     * 일정 작성자인지 검증하는 메서드
     */
    private void validateScheduleOwner(Schedule schedule, User user) {
        if (!schedule.getCreatedBy().getUserId().equals(user.getUserId())) {
            throw new CustomException("일정에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);
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

    /**
     * 알림 트리거 공통 메서드
     */
    private void notifyClubMembers(Long clubId, NotificationType type, String content, String relatedUrl) {
        List<ClubMember> members = clubMemberRepository.findByClubClubId(clubId);

        for (ClubMember member : members) {
            User receiver = member.getUser();
            notificationService.create(receiver, type, content, relatedUrl);
        }
    }

    // ============= FE 호환용 메서드 (scheduleId만으로 동작) =============

    // 일정 생성 (clubId를 body에서 받음)
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
