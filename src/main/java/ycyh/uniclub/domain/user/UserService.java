package ycyh.uniclub.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ycyh.uniclub.domain.school.SchoolRepository;
import ycyh.uniclub.global.exception.CustomException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserResponseDto signup(UserSignupDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException("이미 존재하는 이메일입니다");
        }
        
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .phone(dto.getPhone())
                .studentId(dto.getStudentId())
                .build();
        
        if (dto.getSchoolId() != null) {
            schoolRepository.findById(dto.getSchoolId())
                    .ifPresent(user::setSchool);
        }
        
        User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));
    }
    
    public UserResponseDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));
        return UserResponseDto.from(user);
    }
    
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));
        
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getStudentId() != null) {
            user.setStudentId(dto.getStudentId());
        }
        if (dto.getSchoolId() != null) {
            schoolRepository.findById(dto.getSchoolId())
                    .ifPresent(user::setSchool);
        }
        
        return UserResponseDto.from(user);
    }
    
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException("기존 비밀번호가 일치하지 않습니다");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}


