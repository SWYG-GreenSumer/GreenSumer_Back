package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.*;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.UserLoginResponse;
import org.swyg.greensumer.dto.response.UserSignUpResponse;
import org.swyg.greensumer.dto.response.UsernameResponse;
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
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        String token = userService.login(request.getUsername(), request.getPassword());
        return Response.success(UserLoginResponse.of(token));
    }

    @PostMapping("/mail")
    public Response<Void> sendMail(@RequestBody VerificationRequest request){
        verificationService.sendMail(request.getEmail());

        return Response.success();
    }

    @DeleteMapping("/mail")
    public Response<Void> checkMail(@RequestBody VerificationCheckRequest request){
        verificationService.checkMail(request.getEmail(), request.getCode());

        return Response.success();
    }

    @GetMapping("/{username}")
    public Response<Void> existUsername(@PathVariable String username){
        userService.existUsername(username);

        return Response.success();
    }

    @GetMapping("/find/username")
    public Response<UsernameResponse> findUsername(@RequestParam String email){
        return Response.success(userService.findUsername(email));
    }

    @PutMapping("/find/password")
    public Response<Void> findPassword(@RequestBody PasswordUpdateRequest request){
        userService.findPassword(request.getUsername(), request.getEmail(), request.getCode(), request.getPassword());

        return Response.success();
    }

    @PutMapping("/user")
    public Response<Void> updateUser(@RequestBody UpdateUserRequest request){
        userService.updateUserInfo(request);

        return Response.success();
    }

}
