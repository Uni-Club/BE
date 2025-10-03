package ycyh.uniclub.domain.school.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ycyh.uniclub.domain.school.entity.School;
import ycyh.uniclub.domain.school.service.SchoolService;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;
    
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
}
