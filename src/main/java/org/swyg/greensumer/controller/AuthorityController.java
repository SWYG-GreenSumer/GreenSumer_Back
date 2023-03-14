package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.RequestLevelUp;
import org.swyg.greensumer.dto.request.UpdateUserRequest;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.UpdateUserResponse;
import org.swyg.greensumer.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/update")
public class AuthorityController {

    private final UserService userService;

    @GetMapping("/test")
    public Response<Void> test() {
        return Response.success();
    }

    @PutMapping("/user")
    public Response<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest request, Authentication authentication) {
        User user = userService.updateUserInfo(request, authentication.getName());
        return Response.success(UpdateUserResponse.fromUser(user));
    }

    @PutMapping("/role")
    public Response<Void> requestLevelUp(@RequestBody RequestLevelUp request, Authentication authentication) {
        // TODO: 관리자에게 요청을 전송하여 관리자를 통해 요청을 수락받는 시스템으로 변경될 예정
        userService.requestLevelUp(request, authentication.getName());
        return Response.success();
    }

}
