package ycyh.uniclub.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String name;
    private String phone;
    private String studentId;
    private Long schoolId;
    private String schoolName;
    private LocalDateTime createdAt;
    
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .studentId(user.getStudentId())
                .schoolId(user.getSchool() != null ? user.getSchool().getSchoolId() : null)
                .schoolName(user.getSchool() != null ? user.getSchool().getSchoolName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
