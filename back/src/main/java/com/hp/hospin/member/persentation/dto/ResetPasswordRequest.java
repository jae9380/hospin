package com.hp.hospin.member.persentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String email,
        @NotBlank @Size(min = 8, max = 20) String newPassword,
        @NotBlank String confirmNewPassword
) {}
