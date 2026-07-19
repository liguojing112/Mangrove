package com.mangrove.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {

    private Long id;
    private String username;
    private String publicId;
    private String nickname;
    private String email;
    private String phone;
    private String avatarUrl;
    private String role;
    private String bio;
    private String favoriteStartDate;
    private String createdAt;
}
