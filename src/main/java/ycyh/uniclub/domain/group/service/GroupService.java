package ycyh.uniclub.domain.group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.group.dto.GroupResponseDto;
import ycyh.uniclub.domain.group.dto.GroupSearchDto;
import ycyh.uniclub.domain.group.entity.Group;
import ycyh.uniclub.domain.group.repository.GroupRepository;
import ycyh.uniclub.global.exception.CustomException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    
    public List<GroupResponseDto> searchGroups(GroupSearchDto searchDto) {
        List<Group> groups = groupRepository.searchGroups(
                searchDto.getKeyword(), 
                searchDto.getSchoolId()
        );
        
        return groups.stream()
                .map(GroupResponseDto::from)
                .collect(Collectors.toList());
    }
    
    public GroupResponseDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException("그룹을 찾을 수 없습니다"));
        
        return GroupResponseDto.from(group);
    }
    
    public List<GroupResponseDto> getGroupsBySchool(Long schoolId) {
        List<Group> groups = groupRepository.findBySchoolSchoolId(schoolId);
        return groups.stream()
                .map(GroupResponseDto::from)
                .collect(Collectors.toList());
    }
}
