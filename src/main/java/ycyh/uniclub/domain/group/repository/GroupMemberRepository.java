package ycyh.uniclub.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ycyh.uniclub.domain.group.entity.GroupMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroupGroupId(Long groupId);
    List<GroupMember> findByUserUserId(Long userId);
    Optional<GroupMember> findByUserUserIdAndGroupGroupId(Long userId, Long groupId);
    boolean existsByUserUserIdAndGroupGroupId(Long userId, Long groupId);
}
