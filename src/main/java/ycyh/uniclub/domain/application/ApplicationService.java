package ycyh.uniclub.domain.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.Group;
import ycyh.uniclub.domain.group.GroupMember;
import ycyh.uniclub.domain.group.GroupMemberRepository;
import ycyh.uniclub.domain.group.GroupRepository;
import ycyh.uniclub.domain.recruitment.Recruitment;
import ycyh.uniclub.domain.recruitment.RecruitmentRepository;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.global.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Transactional
    public ApplicationResponseDto submit(ApplicationSubmitDto dto, User applicant) {
        // 모집공고 확인
        Recruitment recruitment = recruitmentRepository.findById(dto.getRecruitmentId())
                .orElseThrow(() -> new CustomException("모집공고를 찾을 수 없습니다"));
        
        // 중복 지원 체크
        if (applicationRepository.existsByRecruitmentRecruitmentIdAndApplicantUserId(
                dto.getRecruitmentId(), applicant.getUserId())) {
            throw new CustomException("이미 지원한 모집공고입니다");
        }
        
        // 지원서 생성
        Application application = Application.builder()
                .recruitment(recruitment)
                .group(recruitment.getGroup())
                .applicant(applicant)
                .motivation(dto.getMotivation())
                .answers(convertToJson(dto.getAnswers()))
                .status(ApplicationStatus.SUBMITTED)
                .build();
        
        Application saved = applicationRepository.save(application);
        return ApplicationResponseDto.from(saved);
    }
    
    public List<ApplicationResponseDto> getByGroup(Long groupId, User user) {
        // 권한 체크: 그룹 관리자만 지원서 목록 조회 가능
        if (!isGroupAdmin(user, groupId)) {
            throw new CustomException("지원서 목록을 조회할 권한이 없습니다");
        }
        
        return applicationRepository.findByGroupGroupId(groupId)
                .stream()
                .map(ApplicationResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public ApplicationResponseDto getById(Long id, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException("지원서를 찾을 수 없습니다"));
        
        // 권한 체크: 지원자 본인 또는 그룹 관리자만 조회 가능
        boolean isApplicant = application.getApplicant().getUserId().equals(user.getUserId());
        boolean isAdmin = isGroupAdmin(user, application.getGroup().getGroupId());
        
        if (!isApplicant && !isAdmin) {
            throw new CustomException("지원서를 조회할 권한이 없습니다");
        }
        
        return ApplicationResponseDto.from(application);
    }
    
    @Transactional
    public ApplicationResponseDto review(Long id, ApplicationReviewDto dto, User reviewer) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException("지원서를 찾을 수 없습니다"));
        
        // 권한 체크: 그룹 관리자만 심사 가능
        if (!isGroupAdmin(reviewer, application.getGroup().getGroupId())) {
            throw new CustomException("지원서를 심사할 권한이 없습니다");
        }
        
        application.setStatus(dto.getStatus());
        application.setReviewer(reviewer);
        application.setReviewNote(dto.getReviewMessage()); // FE 호환: reviewNote 또는 memo
        application.setDecidedAt(LocalDateTime.now());
        
        // 승인시 자동으로 그룹 멤버로 추가
        if (dto.getStatus() == ApplicationStatus.ACCEPTED) {
            GroupMember member = GroupMember.builder()
                    .user(application.getApplicant())
                    .group(application.getGroup())
                    .role("부원")
                    .status("active")
                    .build();
            groupMemberRepository.save(member);
        }
        
        return ApplicationResponseDto.from(application);
    }
    
    public List<ApplicationResponseDto> getMyApplications(User user) {
        return applicationRepository.findByApplicantUserId(user.getUserId())
                .stream()
                .map(ApplicationResponseDto::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void cancel(Long id, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException("지원서를 찾을 수 없습니다"));
        
        // 본인 지원서인지 확인
        if (!application.getApplicant().getUserId().equals(user.getUserId())) {
            throw new CustomException("본인의 지원서만 취소할 수 있습니다");
        }
        
        // 이미 처리된 지원서는 취소 불가
        if (application.getStatus() != ApplicationStatus.SUBMITTED && 
            application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new CustomException("이미 처리된 지원서는 취소할 수 없습니다");
        }
        
        application.setStatus(ApplicationStatus.CANCELLED);
        application.setDecidedAt(LocalDateTime.now());
        applicationRepository.save(application);
    }
    
    public ApplicationResponseDto getMyApplicationStatus(Long recruitmentId, User user) {
        return applicationRepository.findByRecruitmentRecruitmentIdAndApplicantUserId(recruitmentId, user.getUserId())
                .map(ApplicationResponseDto::from)
                .orElse(null);
    }
    
    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    private boolean isGroupAdmin(User user, Long groupId) {
        // 그룹 리더인지 확인
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null && group.getLeader() != null && 
            group.getLeader().getUserId().equals(user.getUserId())) {
            return true;
        }
        
        // 그룹 멤버 권한 확인
        return groupMemberRepository.findByUserUserIdAndGroupGroupId(user.getUserId(), groupId)
                .map(member -> {
                    String role = member.getRole();
                    return "회장".equals(role) || "부회장".equals(role) || "관리자".equals(role);
                })
                .orElse(false);
    }
}
