package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameResponse {
    String username;

    public static UsernameResponse of(User user) {
        return new UsernameResponse(user.getUsername());
    }
}
