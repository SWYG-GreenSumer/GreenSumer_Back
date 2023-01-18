package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.*;
import org.swyg.greensumer.dto.response.*;
import org.swyg.greensumer.service.UserService;
import org.swyg.greensumer.service.VerificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final VerificationService verificationService;

    @PostMapping("/sign-up")
    public Response<UserSignUpResponse> signup(@RequestBody UserSignUpRequest request) {
        User user = userService.signup(request);
        return Response.success(UserSignUpResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getUsername(), request.getPassword());
        return Response.success(UserLoginResponse.of(token));
    }

    @PostMapping("/mail")
    public Response<Void> sendMail(@RequestBody VerificationRequest request) {
        verificationService.sendMail(request.getEmail());

        return Response.success();
    }

    @PutMapping("/mail")
    public Response<Void> checkMail(@RequestBody VerificationCheckRequest request) {
        verificationService.checkMail(request.getEmail(), request.getCode());

        return Response.success();
    }

    @GetMapping("/existUsername")
    public Response<Void> existUsername(@RequestParam String username) {
        userService.existUsername(username);

        return Response.success();
    }

    // TODO: 현재 이메일 인증 관련 정보를 캐시로 저장하고 있지 않고 있다. 그래서 아이디 찾기 시 저장된 인증 정보를 삭제해주어야하기 때문에 PUT으로 일단 처리해두었다.
    @PutMapping("/find/username")
    public Response<UsernameResponse> findUsername(@RequestBody UsernameRequest request) {
        User user = userService.findUsername(request.getEmail(), request.getCode());
        return Response.success(UsernameResponse.of(user));
    }

    @PutMapping("/find/password")
    public Response<Void> findPassword(@RequestBody PasswordUpdateRequest request) {
        userService.findPassword(request.getUsername(), request.getEmail(), request.getCode(), request.getPassword());

        return Response.success();
    }

    @PutMapping("/user")
    public Response<UpdateUserResponse> updateUser(@RequestBody UpdateUserRequest request, Authentication authentication) {
        User user = userService.updateUserInfo(request, authentication.getName());
        return Response.success(UpdateUserResponse.fromUser(user));
    }

}
