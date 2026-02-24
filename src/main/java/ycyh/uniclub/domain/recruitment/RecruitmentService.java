package ycyh.uniclub.domain.recruitment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.club.Club;
import ycyh.uniclub.domain.club.ClubAuthorizationService;
import ycyh.uniclub.domain.club.ClubRepository;
import ycyh.uniclub.domain.club.ClubMemberRepository;
import ycyh.uniclub.domain.user.User;
import ycyh.uniclub.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final ClubRepository clubRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubAuthorizationService clubAuthorizationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RecruitmentResponseDto> getByClub(Long clubId) {
        return recruitmentRepository.findByClubClubId(clubId)
                .stream()
                .map(RecruitmentResponseDto::from)
                .collect(Collectors.toList());
    }

    public RecruitmentResponseDto getById(Long id) {
        Recruitment entity = recruitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("모집공고를 찾을 수 없습니다"));
        return RecruitmentResponseDto.from(entity);
    }

    public List<RecruitmentResponseDto> search(String keyword, Long schoolId, String category, RecruitmentStatus status) {
        List<Recruitment> list = recruitmentRepository.searchRecruitments(keyword, schoolId, category, status);
        return list.stream().map(RecruitmentResponseDto::from).collect(Collectors.toList());
    }
    
    @Transactional
    public RecruitmentResponseDto create(RecruitmentCreateDto dto, User user) {
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        // 권한 체크: 그룹 리더 또는 관리자만 모집공고 생성 가능
        if (!clubAuthorizationService.isClubAdmin(user, club)) {
            throw new CustomException("모집공고를 생성할 권한이 없습니다");
        }
        
        Recruitment recruitment = Recruitment.builder()
                .club(club)
                .school(club.getSchool())
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .tags(convertToJson(dto.getTags()))
                .applyStart(dto.getApplyStart())
                .applyEnd(dto.getApplyEnd())
                .capacity(dto.getCapacity())
                .status(RecruitmentStatus.PUBLISHED)  // 기본값을 PUBLISHED로 변경
                .createdBy(user)
                .build();
        
        // 커스텀 필드 저장
        if (dto.getCustomFields() != null) {
            recruitment.setCustomFields(convertToJson(dto.getCustomFields()));
        }
        
        Recruitment saved = recruitmentRepository.save(recruitment);
        return RecruitmentResponseDto.from(saved);
    }
    
    @Transactional
    public RecruitmentResponseDto updateStatus(Long id, RecruitmentStatus status, User user) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException("모집공고를 찾을 수 없습니다"));
        
        // 권한 체크: 그룹 리더 또는 관리자만 상태 변경 가능
        if (!clubAuthorizationService.isClubAdmin(user, recruitment.getClub())) {
            throw new CustomException("모집공고 상태를 변경할 권한이 없습니다");
        }
        
        recruitment.setStatus(status);
        return RecruitmentResponseDto.from(recruitment);
    }
    
    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    @Transactional
    public RecruitmentResponseDto update(Long id, RecruitmentUpdateDto dto, User user) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException("모집공고를 찾을 수 없습니다"));
        
        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(user, recruitment.getClub())) {
            throw new CustomException("모집공고를 수정할 권한이 없습니다");
        }
        
        // 업데이트
        if (dto.getTitle() != null) recruitment.setTitle(dto.getTitle());
        if (dto.getContent() != null) recruitment.setContent(dto.getContent());
        if (dto.getCategory() != null) recruitment.setCategory(dto.getCategory());
        if (dto.getTags() != null) recruitment.setTags(convertToJson(dto.getTags()));
        if (dto.getApplyStart() != null) recruitment.setApplyStart(dto.getApplyStart());
        if (dto.getApplyEnd() != null) recruitment.setApplyEnd(dto.getApplyEnd());
        if (dto.getCapacity() != null) recruitment.setCapacity(dto.getCapacity());
        if (dto.getCustomFields() != null) recruitment.setCustomFields(convertToJson(dto.getCustomFields()));
        
        return RecruitmentResponseDto.from(recruitment);
    }
    
    @Transactional
    public void delete(Long id, User user) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException("모집공고를 찾을 수 없습니다"));
        
        // 권한 체크
        if (!clubAuthorizationService.isClubAdmin(user, recruitment.getClub())) {
            throw new CustomException("모집공고를 삭제할 권한이 없습니다");
        }
        
        recruitmentRepository.delete(recruitment);
    }
    
}


