package ycyh.uniclub.domain.school;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.club.ClubResponseDto;
import ycyh.uniclub.domain.club.ClubService;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;
    private final ClubService clubService;

    // 학교 검색 API
    @GetMapping
    public ResponseEntity<List<SchooleResponseDto>> searchSchools(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region
    ) {
        List<School> schools;

        // region이 있으면 region 우선
        if (region != null && !region.trim().isEmpty()) {
            schools = schoolService.getSchoolsByRegion(region);
        } else {
            schools = schoolService.searchSchools(keyword);
        }

        List<SchooleResponseDto> result = schools.stream()
                .map(SchooleResponseDto::from)
                .toList();

        return ResponseEntity.ok(result);
    }

    // 지역별 학교 조회 API
    @GetMapping("/region/{region}")
    public ResponseEntity<List<School>> getSchoolsByRegion(@PathVariable String region) {
        return ResponseEntity.ok(schoolService.getSchoolsByRegion(region));
    }

    // 학교 상세 조회 API
    @GetMapping("/{schoolId}")
    public ResponseEntity<School> getSchoolById(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolById(schoolId));
    }

    // 학교별 동아리 목록 조회 API
    @GetMapping("/{schoolId}/clubs")
    public ResponseEntity<List<ClubResponseDto>> getClubsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(clubService.getClubsBySchool(schoolId));
    }
}


