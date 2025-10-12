package ycyh.uniclub.domain.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByGroupGroupId(Long groupId);
    List<Application> findByApplicantUserId(Long userId);
    List<Application> findByRecruitmentRecruitmentId(Long recruitmentId);
    Optional<Application> findByRecruitmentRecruitmentIdAndApplicantUserId(Long recruitmentId, Long userId);
    boolean existsByRecruitmentRecruitmentIdAndApplicantUserId(Long recruitmentId, Long userId);
}
