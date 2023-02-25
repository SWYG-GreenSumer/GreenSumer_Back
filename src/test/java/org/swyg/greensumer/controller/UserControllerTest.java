package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.config.SecurityConfig;
import org.swyg.greensumer.dto.TokenInfo;
import org.swyg.greensumer.dto.request.PasswordUpdateRequest;
import org.swyg.greensumer.dto.request.UserLoginRequest;
import org.swyg.greensumer.dto.request.UserLogoutRequest;
import org.swyg.greensumer.dto.request.UserSignUpRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.UserService;
import org.swyg.greensumer.service.VerificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.getTokenInfo;
import static org.swyg.greensumer.fixture.Fixtures.getUser;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 유저")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class, SecurityConfig.class})
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
    @WithMockUser
    @Test
    void givenUserInfo_whenRequestingSignUp_thenReturnUserInfo() throws Exception {
        // given
        willDoNothing().given(userService).signup(any(UserSignUpRequest.class));

        // when & then
        mvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserSignUpRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());

        verify(userService, times(1)).signup(any(UserSignUpRequest.class));
    }

    @DisplayName("[view][POST] 회원가입 요청 - 이미 존재하는 아이디인 경우")
    @WithMockUser
    @Test
    void givenUserInfo_whenRequestingSignUp_thenThrowDuplicatedUsernameException() throws Exception {
        // given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME)).given(userService).signup(any(UserSignUpRequest.class));

        // when & then
        mvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserSignUpRequest()))
                        .with(csrf())
                )
                .andExpect(status().isConflict());

        verify(userService, times(1)).signup(any(UserSignUpRequest.class));
    }

    @DisplayName("[view][POST] 로그인 요청 - 정상 호출")
    @WithMockUser
    @Test
    void givenUserInfo_whenRequestingLogin_thenReturnToken() throws Exception {
        // given
        given(userService.login(any(UserLoginRequest.class))).willReturn(any(TokenInfo.class));

        // when & then
        mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserLoginRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());

        verify(userService, times(1)).login(any(UserLoginRequest.class));
    }

    @DisplayName("[view][POST] 로그인 요청 - 회원가입이 되지 않은 유저인경우")
    @WithMockUser
    @Test
    void givenUserInfo_whenRequestingLogin_thenThrowUserNotFoundException() throws Exception {
        // given
        given(userService.login(any(UserLoginRequest.class))).willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserLoginRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());

        verify(userService, times(1)).login(any(UserLoginRequest.class));
    }

    @DisplayName("[view][POST] 로그인 요청 - 패스워드가 일치하지 않는 경우")
    @WithMockUser
    @Test
    void givenUserInfo_whenRequestingLogin_thenThrowInvalidPasswordException() throws Exception {
        // given
        given(userService.login(any(UserLoginRequest.class))).willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PASSWORD));

        // when & then
        mvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(UserLoginRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 이메일 인증 요청 - 정상 호출")
    @WithMockUser
    @Test
    void givenEmail_whenRequestingVerification_thenReturnNothing() throws Exception {
        // given
        willDoNothing().given(verificationService).sendMail(anyString());

        // when & then
        mvc.perform(post("/api/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerificationRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());

        verify(verificationService, times(1)).sendMail(any(String.class));
    }

    @DisplayName("[view][GET] 이메일 인증 확인 - 정상 호출")
    @WithMockUser
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenReturnNothing() throws Exception {
        willDoNothing().given(verificationService).checkMail(anyString(), anyString());

        mvc.perform(get("/api/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerificationCheckRequest()))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 이메일 인증 확인 - 이메일이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenThrowMailNotFound() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.MAIL_NOT_FOUND)).given(verificationService).checkMail(anyString(), anyString());

        mvc.perform(get("/api/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerificationCheckRequest()))
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][GET] 이메일 인증 확인 - 인증 번호가 일치하지 않는 경우")
    @WithMockUser
    @Test
    void givenEmailAndCode_whenRequestingCheckCode_thenThrowInvalidVerificationCodeException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_VERIFICATION_CODE)).given(verificationService).checkMail(anyString(), anyString());

        mvc.perform(get("/api/users/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(VerificationCheckRequest()))
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 아이디 중복조회 - 정상호출")
    @WithMockUser
    @Test
    void givenUsername_whenRequestingDuplicateUsername_thenReturnNothing() throws Exception {
        willDoNothing().given(userService).existUsername(anyString());

        mvc.perform(get("/api/users/existUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "username")
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 아이디 중복조회 - 아이디가 중복된 경우")
    @WithMockUser
    @Test
    void givenUsername_whenRequestingDuplicateUsername_thenThrowDuplicateUsernameException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME)).given(userService).existUsername(anyString());

        mvc.perform(get("/api/users/existUsername")
                        .param("username", "username")
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][GET] 아이디 찾기 - 정상호출")
    @WithMockUser
    @Test
    void givenEmailAndCode_whenRequestingFindingUsername_thenReturnUsername() throws Exception {
        given(userService.findUsername(anyString(), anyString())).willReturn(getUser());

        mvc.perform(get("/api/users/find/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email")
                        .param("code", "code")
                        .with(csrf())
                )
                .andExpect(status().isOk());

        verify(userService, times(1)).findUsername(anyString(), anyString());
    }

    @DisplayName("[view][GET] 아이디 찾기 - 등록된 이메일이 아닌 경우")
    @WithMockUser
    @Test
    void givenEmailAndCode_whenRequestingFindingUsername_thenThrowUserNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userService).findUsername(anyString(), anyString());

        mvc.perform(get("/api/users/find/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", "email@email.com")
                        .param("code", "code")
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 비밀번호 찾기 - 정상호출")
    @WithMockUser
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenReturnUsername() throws Exception {
        willDoNothing().given(userService).findPassword(any(PasswordUpdateRequest.class));

        mvc.perform(put("/api/users/find/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(PasswordUpdateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 비밀번호 찾기 - 등록된 이메일이 아닌 경우")
    @WithMockUser
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowUserNotFoundException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userService).findPassword(any(PasswordUpdateRequest.class));

        mvc.perform(put("/api/users/find/password")
                        .content(objectMapper.writeValueAsBytes(PasswordUpdateRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 비밀번호 변경 요청 - 이전 비밀번호와 동일한 경우")
    @WithMockUser
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowSameAsPreviousPasswordException() throws Exception {
        willThrow(new GreenSumerBackApplicationException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD)).given(userService).findPassword(any(PasswordUpdateRequest.class));

        mvc.perform(put("/api/users/find/password")
                        .content(objectMapper.writeValueAsBytes(PasswordUpdateRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][DELETE] 로그아웃 - 정상호출")
    @WithMockUser
    @Test
    void givenAccessTokenAndRefreshToken_whenRequestingLogout_thenReturnNothing() throws Exception {
        willDoNothing().given(userService).logout(any(UserLogoutRequest.class));

        mvc.perform(delete("/api/users/logout")
                        .content(objectMapper.writeValueAsBytes(UserLogoutRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] Access Token 재발급 - 정상호출")
    @WithMockUser
    @Test
    void givenAccessTokenAndRefreshToken_whenRequestingReIssueAccessToken_thenReturnToken() throws Exception {
        given(userService.reissue(anyString(), anyString())).willReturn(getTokenInfo());

        mvc.perform(put("/api/users/reissue")
                        .content(objectMapper.writeValueAsBytes(UserReissueRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

//    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 정상 호출")
//    @WithMockUser
//    @Test
//    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenReturnUser() throws Exception {
//        when(userService.updateUserInfo(any(), any())).thenReturn(getUser());
//
//        mvc.perform(put("/api/v1/users/user")
//                        .content(objectMapper.writeValueAsBytes(PasswordUpdateRequest()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isOk());
//    }
//
//    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 로그인하지 않은 경우")
//    @WithAnonymousUser
//    @Test
//    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowUnAuthorizedException() throws Exception {
//        when(userService.updateUserInfo(any(), any())).thenReturn(getUser());
//
//        mvc.perform(put("/api/v1/users/user")
//                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isUnauthorized());
//    }
//
//    @DisplayName("[view][PUT] 사용자 정보 변경 요청 - 존재하지 않는 아이디인 경우")
//    @WithMockUser
//    @Test
//    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowUserNotFoundException() throws Exception {
//        doThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).when(userService).updateUserInfo(any(), any());
//
//        mvc.perform(put("/api/v1/users/user")
//                        .content(objectMapper.writeValueAsBytes(getUpdateUserRequest()))
//                        .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isNotFound());
//    }

}
