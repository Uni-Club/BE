package ycyh.uniclub.domain.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ycyh.uniclub.domain.school.entity.School;

import java.util.List;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findBySchoolNameContaining(String schoolName);
    List<School> findByRegion(String region);
}
