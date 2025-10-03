package ycyh.uniclub.domain.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.school.entity.School;
import ycyh.uniclub.domain.school.repository.SchoolRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SchoolService {
    private final SchoolRepository schoolRepository;
    
    public List<School> searchSchools(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return schoolRepository.findAll();
        }
        return schoolRepository.findBySchoolNameContaining(keyword);
    }
    
    public List<School> getSchoolsByRegion(String region) {
        return schoolRepository.findByRegion(region);
    }
    
    public School getSchoolById(Long schoolId) {
        return schoolRepository.findById(schoolId)
                .orElse(null);
    }
}

