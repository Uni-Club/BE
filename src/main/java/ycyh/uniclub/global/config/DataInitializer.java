package ycyh.uniclub.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ycyh.uniclub.domain.board.entity.Board;
import ycyh.uniclub.domain.board.enums.BoardType;
import ycyh.uniclub.domain.group.entity.Group;
import ycyh.uniclub.domain.group.entity.GroupMember;
import ycyh.uniclub.domain.group.repository.GroupMemberRepository;
import ycyh.uniclub.domain.group.repository.GroupRepository;
import ycyh.uniclub.domain.recruitment.entity.Recruitment;
import ycyh.uniclub.domain.recruitment.enums.RecruitmentStatus;
import ycyh.uniclub.domain.recruitment.repository.RecruitmentRepository;
import ycyh.uniclub.domain.school.entity.School;
import ycyh.uniclub.domain.school.repository.SchoolRepository;
import ycyh.uniclub.domain.user.entity.User;
import ycyh.uniclub.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // 학교 데이터 초기화
        School university = schoolRepository.save(School.builder()
                .schoolName("대학교")
                .campusName("메인캠퍼스")
                .region("서울")
                .domain("university.ac.kr")
                .build());
        
        // 사용자 데이터 초기화
        User user1 = userRepository.save(User.builder()
                .email("test@university.ac.kr")
                .password(passwordEncoder.encode("password123"))
                .name("김민수")
                .phone("010-1234-5678")
                .studentId("2024-12345")
                .school(university)
                .build());
        
        User user2 = userRepository.save(User.builder()
                .email("admin@university.ac.kr")
                .password(passwordEncoder.encode("password123"))
                .name("이수진")
                .phone("010-2345-6789")
                .studentId("2024-23456")
                .school(university)
                .build());
        
        User user3 = userRepository.save(User.builder()
                .email("student@university.ac.kr")
                .password(passwordEncoder.encode("password123"))
                .name("박지훈")
                .phone("010-3456-7890")
                .studentId("2024-34567")
                .school(university)
                .build());
        
        // 그룹 데이터 초기화
        Group group1 = groupRepository.save(Group.builder()
                .groupName("GDSC")
                .description("Google Developer Student Clubs - 구글 개발자 학생 커뮤니티입니다. 웹/앱/AI 등 다양한 기술을 함께 학습하고 프로젝트를 진행합니다.")
                .leader(user1)
                .school(university)
                .build());
        
        Group group2 = groupRepository.save(Group.builder()
                .groupName("농구부")
                .description("함께 농구를 즐기며 실력을 향상시키는 동아리입니다. 매주 정기 연습과 친선 경기를 진행합니다.")
                .leader(user2)
                .school(university)
                .build());
        
        Group group3 = groupRepository.save(Group.builder()
                .groupName("나눔 봉사단")
                .description("지역사회와 함께하는 봉사 동아리입니다. 교육봉사, 환경봉사 등 다양한 활동을 진행합니다.")
                .leader(user3)
                .school(university)
                .build());
        
        Group group4 = groupRepository.save(Group.builder()
                .groupName("오케스트라")
                .description("클래식 음악을 사랑하는 사람들이 모여 아름다운 하모니를 만들어갑니다.")
                .leader(user1)
                .school(university)
                .build());
        
        Group group5 = groupRepository.save(Group.builder()
                .groupName("창업 동아리 STARTUP")
                .description("창업에 관심있는 학생들이 모여 아이디어를 현실로 만들어가는 동아리입니다.")
                .leader(user2)
                .school(university)
                .build());
        
        // 그룹 멤버 추가
        groupMemberRepository.save(GroupMember.builder()
                .user(user1)
                .group(group1)
                .role("리더")
                .status("active")
                .build());
        
        groupMemberRepository.save(GroupMember.builder()
                .user(user2)
                .group(group2)
                .role("리더")
                .status("active")
                .build());
        
        groupMemberRepository.save(GroupMember.builder()
                .user(user3)
                .group(group3)
                .role("리더")
                .status("active")
                .build());
        
        groupMemberRepository.save(GroupMember.builder()
                .user(user1)
                .group(group4)
                .role("리더")
                .status("active")
                .build());
        
        groupMemberRepository.save(GroupMember.builder()
                .user(user2)
                .group(group5)
                .role("리더")
                .status("active")
                .build());
        
        // 추가 멤버 등록
        groupMemberRepository.save(GroupMember.builder()
                .user(user2)
                .group(group1)
                .role("부원")
                .status("active")
                .build());
        
        groupMemberRepository.save(GroupMember.builder()
                .user(user3)
                .group(group1)
                .role("부원")
                .status("active")
                .build());
        
        // 모집공고 데이터 초기화
        recruitmentRepository.save(Recruitment.builder()
                .group(group1)
                .school(university)
                .title("2025년 GDSC 신입 멤버 모집")
                .content("구글 기술에 관심있는 학생 여러분을 환영합니다! 함께 성장하고 배워나갈 열정있는 멤버를 찾습니다.")
                .category("IT/프로그래밍")
                .tags("[\"프로그래밍\", \"웹개발\", \"앱개발\", \"AI\", \"머신러닝\"]")
                .applyStart(LocalDateTime.now())
                .applyEnd(LocalDateTime.now().plusDays(14))
                .status(RecruitmentStatus.PUBLISHED)
                .capacity(30)
                .views(0)
                .createdBy(user1)
                .build());
        
        recruitmentRepository.save(Recruitment.builder()
                .group(group2)
                .school(university)
                .title("농구부 신입부원 모집")
                .content("농구를 좋아하시는 분들 모두 환영합니다! 실력은 상관없습니다. 열정만 있다면 OK!")
                .category("스포츠")
                .tags("[\"농구\", \"운동\", \"스포츠\"]")
                .applyStart(LocalDateTime.now())
                .applyEnd(LocalDateTime.now().plusDays(7))
                .status(RecruitmentStatus.PUBLISHED)
                .capacity(15)
                .views(0)
                .createdBy(user2)
                .build());
        
        recruitmentRepository.save(Recruitment.builder()
                .group(group5)
                .school(university)
                .title("창업 동아리 STARTUP 팀원 모집")
                .content("창업에 도전하고 싶은 열정 넘치는 학생을 찾습니다. 함께 꿈을 현실로 만들어봐요!")
                .category("학술")
                .tags("[\"창업\", \"스타트업\", \"비즈니스\", \"기획\"]")
                .applyStart(LocalDateTime.now())
                .applyEnd(LocalDateTime.now().plusDays(10))
                .status(RecruitmentStatus.PUBLISHED)
                .capacity(20)
                .views(0)
                .createdBy(user2)
                .build());
        
        System.out.println("초기 데이터 생성 완료!");
    }
}
