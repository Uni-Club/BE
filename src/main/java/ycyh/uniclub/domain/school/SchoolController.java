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

    @GetMapping
    public ResponseEntity<List<School>> searchSchools(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(schoolService.searchSchools(keyword));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<School>> getSchoolsByRegion(@PathVariable String region) {
        return ResponseEntity.ok(schoolService.getSchoolsByRegion(region));
    }

    @GetMapping("/{schoolId}")
    public ResponseEntity<School> getSchoolById(@PathVariable Long schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolById(schoolId));
    }

    @GetMapping("/{schoolId}/groups")
    public ResponseEntity<List<GroupResponseDto>> getGroupsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(groupService.getGroupsBySchool(schoolId));
    }
}


