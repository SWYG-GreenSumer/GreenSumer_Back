package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.TokenInfo;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.UserLogoutRequest;
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
    public Response<TokenInfo> login(@RequestBody UserLoginRequest request) {
        TokenInfo tokens = userService.login(request);
        return Response.success(tokens);
    }

    @PostMapping("/logout")
    public Response<Void> logout(@RequestBody UserLogoutRequest logout) {
        userService.logout(logout);
        return Response.success();
    }

    @PostMapping("/reissue")
    public Response<TokenInfo> reissue(@RequestBody UserReissueRequest reissue) {
        TokenInfo tokens = userService.reissue(reissue.getAccessToken(), reissue.getRefreshToken());
        return Response.success(tokens);
    }

    @PostMapping("/mail")
    public Response<Void> sendMail(@RequestBody VerificationRequest request) {
        verificationService.sendMail(request.getEmail());
        return Response.success();
    }

    @GetMapping("/mail")
    public Response<Void> checkMail(@RequestBody VerificationCheckRequest request) {
        verificationService.checkMail(request.getEmail(), request.getCode());

        return Response.success();
    }

    @GetMapping("/existUsername")
    public Response<Void> existUsername(@RequestParam String username) {
        userService.existUsername(username);

        return Response.success();
    }

    @GetMapping("/find/username")
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
