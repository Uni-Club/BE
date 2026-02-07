package ycyh.uniclub.domain.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetVerifyDto {
    private String email;
    private String code;
    private String newPassword;
}
