package ycyh.uniclub.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    List<Recruitment> findByStatus(RecruitmentStatus status);
    
    List<Recruitment> findByGroupGroupId(Long groupId);
    
    @Query("SELECT r FROM Recruitment r WHERE " +
           "(:keyword IS NULL OR r.title LIKE %:keyword% OR r.content LIKE %:keyword%) AND " +
           "(:schoolId IS NULL OR r.school.schoolId = :schoolId OR r.isUnion = true) AND " +
           "(:category IS NULL OR r.category = :category) AND " +
           "r.status = :status")
    List<Recruitment> searchRecruitments(@Param("keyword") String keyword, 
                                        @Param("schoolId") Long schoolId,
                                        @Param("category") String category,
                                        @Param("status") RecruitmentStatus status);
}


