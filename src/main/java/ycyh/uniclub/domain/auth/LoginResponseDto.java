package ycyh.uniclub.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ycyh.uniclub.domain.user.UserResponseDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserResponseDto user;
}


