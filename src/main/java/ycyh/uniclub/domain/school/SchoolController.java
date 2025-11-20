package ycyh.uniclub.domain.school;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.group.GroupResponseDto;
import ycyh.uniclub.domain.group.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;
    private final GroupService groupService;

    // 학교 검색 API
    @GetMapping
    public ResponseEntity<List<School>> searchSchools(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(schoolService.searchSchools(keyword));
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
    @GetMapping("/{schoolId}/groups")
    public ResponseEntity<List<GroupResponseDto>> getGroupsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(groupService.getGroupsBySchool(schoolId));
    }
}


