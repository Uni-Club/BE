package ycyh.uniclub.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByClubNameContaining(String clubName);

    List<Club> findBySchoolSchoolId(Long schoolId);

    boolean existsByClubNameAndSchoolSchoolId(String clubName, Long schoolId);

    @Query("SELECT g FROM Club g WHERE " +
           "(:keyword IS NULL OR g.clubName LIKE %:keyword% OR g.description LIKE %:keyword%) AND " +
           "(:schoolId IS NULL OR g.school.schoolId = :schoolId OR g.isUnion = true)")
    List<Club> searchClubs(@Param("keyword") String keyword, @Param("schoolId") Long schoolId);
}


