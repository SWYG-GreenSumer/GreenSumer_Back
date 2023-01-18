package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    private Integer id;
    private String username;
    private String email;
    private String nickname;
    private String address;
    private Double lat;
    private Double lng;
    private UserRole role;

    public static UpdateUserResponse fromUser(User user) {
        return new UpdateUserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getAddress().getAddress(),
                user.getAddress().getLat(),
                user.getAddress().getLng(),
                user.getRole()
        );
    }
}
