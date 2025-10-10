package ycyh.uniclub.domain.group;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    
    @GetMapping("/search")
    public ResponseEntity<List<GroupResponseDto>> searchGroups(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags) {
        
        GroupSearchDto searchDto = GroupSearchDto.builder()
                .keyword(keyword)
                .schoolId(schoolId)
                .category(category)
                .tags(tags)
                .build();
        
        return ResponseEntity.ok(groupService.searchGroups(searchDto));
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponseDto> getGroupDetail(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupService.getGroupDetail(groupId));
    }
    
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<GroupResponseDto>> getGroupsBySchool(@PathVariable Long schoolId) {
        return ResponseEntity.ok(groupService.getGroupsBySchool(schoolId));
    }
}


