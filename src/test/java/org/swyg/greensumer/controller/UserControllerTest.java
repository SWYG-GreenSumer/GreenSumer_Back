package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.UserService;
import org.swyg.greensumer.service.VerificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 유저")
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private UserService userService;
    @MockBean private VerificationService verificationService;

    public UserControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][POST] 회원가입 요청 - 정상 호출")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenReturnUserInfo() throws Exception {
        when(userService.signup(any())).thenReturn(getUser());

        mvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserSignUpRequest()))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 회원가입 요청 - 이미 존재하는 아이디인 경우")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenThrowDuplicatedUsernameException() throws Exception {
        when(userService.signup(any())).thenThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME));

        mvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserSignUpRequest()))
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][POST] 판매자로 회원가입 요청 - 존재하지 않은 주소 정보인 경우")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenThrowStoreNotFoundException() throws Exception {
        when(userService.signup(any())).thenThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND));

        mvc.perform(post("/api/v1/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserSignUpRequest()))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 로그인 요청 - 정상 호출")
    @Test
    void givenUserInfo_whenRequestingLogin_thenReturnToken() throws Exception {
        when(userService.login(any(), any())).thenReturn(getToken());

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserLoginRequest()))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 로그인 요청 - 회원가입이 되지 않은 유저인경우")
    @Test
    void givenUserInfo_whenRequestingLogin_thenThrowUserNotFoundException() throws Exception {
        when(userService.login(any(), any())).thenThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserLoginRequest()))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 로그인 요청 - 패스워드가 일치하지 않는 경우")
    @Test
    void givenUserInfo_whenRequestingLogin_thenThrowInvalidPasswordException() throws Exception {
        when(userService.login(any(), any())).thenThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getUserLoginRequest()))
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 이메일 인증 요청 - 정상 호출")
    @Test
    void givenEmail_whenRequestingVerification_thenReturnNothing() throws Exception {

        mvc.perform(post("/api/v1/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getVerificationRequest()))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 이메일 인증 요청 - 이메일 발송중 발생한 에러")
    @Test
    void givenEmail_whenRequestingVerification_thenThrowMailSendErrorException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.MAIL_SEND_ERROR)).when(verificationService).sendMail(any());

        mvc.perform(post("/api/v1/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getVerificationRequest()))
                )
                .andExpect(status().isInternalServerError());
    }

    @DisplayName("[view][PUT] 이메일 인증 확인 - 정상 호출")
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenReturnNothing() throws Exception {
        mvc.perform(put("/api/v1/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getVerificationCheckRequest()))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 이메일 인증 확인 - 이메일이 존재하지 않는 경우")
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenThrowMailNotFound() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.MAIL_NOT_FOUND)).when(verificationService).checkMail(any(), any());

        mvc.perform(put("/api/v1/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getVerificationCheckRequest()))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 이메일 인증 확인 - 인증 번호가 일치하지 않는 경우")
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenThrowInvalidVerificationCodeException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_VERIFICATION_CODE)).when(verificationService).checkMail(any(), any());

        mvc.perform(put("/api/v1/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getVerificationCheckRequest()))
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 아이디 중복조회 - 정상호출")
    @Test
    void givenUsername_whenRequestingDuplicateUsername_thenReturnNothing() throws Exception {
        String username = "username";
        mvc.perform(get("/api/v1/users/existUsername")
                        .param("username", username)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 아이디 중복조회 - 아이디가 중복된 경우")
    @Test
    void givenUsername_whenRequestingDuplicateUsername_thenThrowDuplicateUsernameException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME)).when(userService).existUsername(any());
        mvc.perform(get("/api/v1/users/existUsername")
                        .param("username", getUsername())
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][PUT] 아이디 찾기 - 정상호출")
    @Test
    void givenEmailAndCode_whenRequestingFindingUsername_thenReturnUsername() throws Exception {
        when(userService.findUsername(any(), any())).thenReturn(getUser());

        mvc.perform(put("/api/v1/users/find/username")
                        .content(objectMapper.writeValueAsBytes(getUsernameRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 아이디 찾기 - 등록된 이메일이 아닌 경우")
    @Test
    void givenEmailAndCode_whenRequestingFindingUsername_thenThrowUserNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).when(userService).findUsername(any(), any());

        mvc.perform(put("/api/v1/users/find/username")
                        .content(objectMapper.writeValueAsBytes(getUsernameRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 비밀번호 변경 요청 - 정상호출")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenReturnUsername() throws Exception {
        doNothing().when(userService).findPassword(any(), any(), any(), any());

        mvc.perform(put("/api/v1/users/find/password")
                        .content(objectMapper.writeValueAsBytes(getPasswordUpdateRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 비밀번호 변경 요청 - 등록된 이메일이 아닌 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowUserNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).when(userService).findPassword(any(), any(), any(), any());

        mvc.perform(put("/api/v1/users/find/password")
                        .content(objectMapper.writeValueAsBytes(getPasswordUpdateRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 비밀번호 변경 요청 - 이전 비밀번호와 동일한 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowSameAsPreviousPasswordException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD)).when(userService).findPassword(any(), any(), any(), any());

        mvc.perform(put("/api/v1/users/find/password")
                        .content(objectMapper.writeValueAsBytes(getPasswordUpdateRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 정상 호출")
    @WithMockUser
    @Test
    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenReturnUser() throws Exception {
        when(userService.updateUserInfo(any(), any())).thenReturn(getUser());

        mvc.perform(put("/api/v1/users/user")
                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowUnAuthorizedException() throws Exception {
        when(userService.updateUserInfo(any(), any())).thenReturn(getUser());

        mvc.perform(put("/api/v1/users/user")
                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 존재하지 않는 아이디인 경우")
    @WithMockUser
    @Test
    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowUserNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).when(userService).updateUserInfo(any(), any());

        mvc.perform(put("/api/v1/users/user")
                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 판매자인 사용자가 정보 변경 요청 - 주소가 존재하지 않은 경우")
    @WithMockUser
    @Test
    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowAddressNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.ADDRESS_NOT_FOUND)).when(userService).updateUserInfo(any(), any());

        mvc.perform(put("/api/v1/users/user")
                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

}
