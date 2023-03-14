package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.TokenInfo;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.*;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.UsernameResponse;
import org.swyg.greensumer.service.UserService;
import org.swyg.greensumer.service.VerificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final VerificationService verificationService;

    @GetMapping("/test")
    public Response<Void> test() {
        return Response.success();
    }

    @PostMapping("/sign-up")
    public Response<Void> signup(@RequestBody UserSignUpRequest request) {
        userService.signup(request);
        return Response.success();
    }

    @PostMapping("/login")
    public Response<TokenInfo> login(@RequestBody UserLoginRequest request) {
        TokenInfo tokens = userService.login(request);
        return Response.success(tokens);
    }

    @PostMapping("/rogin")
    public Response<TokenInfo> login2(@RequestBody UserLoginRequest request) {
        TokenInfo tokens = userService.login(request);
        return Response.success(tokens);
    }

    @DeleteMapping("/logout")
    public Response<Void> logout(@RequestBody UserLogoutRequest logout) {
        userService.logout(logout);
        return Response.success();
    }

    @PutMapping("/reissue")
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
    public Response<UsernameResponse> findUsername(@RequestParam String email, @RequestParam String code) {
        User user = userService.findUsername(email, code);
        return Response.success(UsernameResponse.of(user));
    }

    @PutMapping("/find/password")
    public Response<Void> findPassword(@RequestBody PasswordUpdateRequest request) {
        userService.findPassword(request);

        return Response.success();
    }

}
