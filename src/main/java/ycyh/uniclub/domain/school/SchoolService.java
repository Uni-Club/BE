package ycyh.uniclub.domain.school;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.global.exception.CustomException;

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
                .orElseThrow(() -> new CustomException("학교를 찾을 수 없습니다", HttpStatus.NOT_FOUND));
    }
}


