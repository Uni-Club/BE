package ycyh.uniclub.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ycyh.uniclub.domain.group.entity.Group;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupNameContaining(String groupName);
    
    List<Group> findBySchoolSchoolId(Long schoolId);
    
    @Query("SELECT g FROM Group g WHERE " +
           "(:keyword IS NULL OR g.groupName LIKE %:keyword% OR g.description LIKE %:keyword%) AND " +
           "(:schoolId IS NULL OR g.school.schoolId = :schoolId)")
    List<Group> searchGroups(@Param("keyword") String keyword, @Param("schoolId") Long schoolId);
}

