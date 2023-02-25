package org.swyg.greensumer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.TokenInfo;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.user.UserCacheRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.swyg.greensumer.fixture.Fixtures.getUser;
import static org.swyg.greensumer.fixture.Fixtures.userEntity;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("비즈니스 로직 - 유저")
@TestPropertySource(properties = {"jwt.token.expired-time-ms=2500000", "jwt.secret-key=abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde222"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class UserServiceTest {

    private UserService sut;

    @Mock private UserEntityRepositoryService userEntityRepositoryService;
    @Mock private VerificationService verificationService;
    @Mock private UserCacheRepository userCacheRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.secret-key}") String secretKey;
    @Value("${jwt.token.expired-time-ms}") Long expiredTimeMs;

    @BeforeEach
    void setUp() {
        sut = new UserService(userEntityRepositoryService, verificationService, bCryptPasswordEncoder);
        sut.setSecretKey(secretKey);
        sut.setExpiredTimeMs(expiredTimeMs);
    }

    @DisplayName("유저정보를 입력하면, 회원가입이 성공한다.")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenReturnUser() {
        // Given
        willDoNothing().given(userEntityRepositoryService).existUsername(any());
        willReturn(userEntity()).given(userEntityRepositoryService).save(any(UserEntity.class));

        // When
        sut.signup(UserSignUpRequest());

        //Then
        verify(userEntityRepositoryService, times(1)).existUsername(any());
        verify(userEntityRepositoryService, times(1)).save(any());
    }

    @DisplayName("중복된 아이디를 입력하면, 중복 에러가 발생한다.")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenThrowConflictException() {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME)).given(userEntityRepositoryService).existUsername(any());

        // When
        Throwable throwable = catchThrowable(() -> sut.signup(UserSignUpRequest()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("Username is duplicated");

        verify(userEntityRepositoryService, times(1)).existUsername(any());
        verify(userEntityRepositoryService, times(0)).save(any());
    }

    @DisplayName("아이디와 비밀번호를 입력하면, 로그인할 수 있다.")
    @Test
    void givenUsernameAndPassword_whenRequestingLogin_thenReturnToken() {
        // Given
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(any());
        willReturn(true).given(bCryptPasswordEncoder).matches(any(), any());
        willDoNothing().given(userEntityRepositoryService).setRefreshToken(any(), any());

        // When
        TokenInfo login = sut.login(UserLoginRequest());

        //Then
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(any());
        verify(bCryptPasswordEncoder, times(1)).matches(any(), any());
        verify(userEntityRepositoryService, times(1)).setRefreshToken(any(), any());
    }

    @DisplayName("회원가입하지 않는 계정으로 로그인하면, NOT FOUND 에러가 발생한다.")
    @Test
    void givenLoginInfo_whenRequestingLogin_thenThrowUserNotFoundException() {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userEntityRepositoryService).loadUserByUsername(any());

        // When
        Throwable throwable = catchThrowable(() -> sut.login(UserLoginRequest()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("User not founded");

        verify(userEntityRepositoryService, times(1)).loadUserByUsername(any());
        verify(bCryptPasswordEncoder, times(0)).matches(any(), any());
        verify(userEntityRepositoryService, times(0)).setRefreshToken(any(), any());
    }

    @DisplayName("로그인시 패스워드가 일치하지 않는 경우")
    @Test
    void givenLoginInfo_whenRequestingLogin_thenThrowInvalidPasswordException() {
        // Given
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(any());
        willReturn(false).given(bCryptPasswordEncoder).matches(any(), any());

        // When
        Throwable throwable = catchThrowable(() -> sut.login(UserLoginRequest()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("Password is invalid");

        verify(userEntityRepositoryService, times(1)).loadUserByUsername(any());
        verify(bCryptPasswordEncoder, times(1)).matches(any(), any());
        verify(userEntityRepositoryService, times(0)).setRefreshToken(any(), any());
    }

    @DisplayName("아이디 찾기 정상 요청")
    @Test
    void givenEmailAndCode_whenRequestingFindUsername_thenReturnUser() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        willReturn(userEntity()).given(userEntityRepositoryService).findByEmail(any());

        // When
        User user = sut.findUsername(any(), any());

        //Then
        assertThat(user).isNotNull();
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(1)).findByEmail(any());
    }

    @DisplayName("아이디 찾기 요청 시 인증 코드가 일치하지 않은 경우")
    @Test
    void givenEmailAndCode_whenRequestingFindUsername_thenThrowMailNotFoundException() {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.MAIL_NOT_FOUND)).given(verificationService).checkMail(any(), any());

        // When
        Throwable throwable = catchThrowable(() -> sut.findUsername(any(), any()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("Mail not founded");

        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(0)).findByEmail(any());
    }

    @DisplayName("아이디 찾기 요청 시 존재하지 않는 아이디인 경우")
    @Test
    void givenEmailAndCode_whenRequestingFindUsername_thenThrowUserNotFoundException() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userEntityRepositoryService).findByEmail(any());

        // When
        Throwable throwable = catchThrowable(() -> sut.findUsername(any(), any()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("User not founded");

        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(1)).findByEmail(any());
    }

    @DisplayName("비밀번호 찾기 정상 요청")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenReturnNothing() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        willReturn(userEntity()).given(userEntityRepositoryService).findByUsernameOrException(any());
        willReturn(false).given(bCryptPasswordEncoder).matches(anyString(), anyString());

        // When
        sut.findPassword(PasswordUpdateRequest());

        //Then
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(any());
        verify(bCryptPasswordEncoder, times(1)).matches(any(), any());
    }

    @DisplayName("비밀번호 찾기 요청 시 아이디가 존재하지 않는 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowUserNotFoundException() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userEntityRepositoryService).findByUsernameOrException(any());

        // When
        Throwable throwable = catchThrowable(() -> sut.findPassword(PasswordUpdateRequest()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("User not founded");

        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(any());
        verify(bCryptPasswordEncoder, times(0)).matches(any(), any());
    }

    @DisplayName("비밀번호 찾기 요청 시 비밀번호가 동일한 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowConflictException() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        willReturn(userEntity()).given(userEntityRepositoryService).findByUsernameOrException(any());
        willThrow(new GreenSumerBackApplicationException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD)).given(bCryptPasswordEncoder).matches(any(), any());

        // When
        Throwable throwable = catchThrowable(() -> sut.findPassword(PasswordUpdateRequest()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("Same as Previous password");

        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(any());
        verify(bCryptPasswordEncoder, times(1)).matches(any(), any());
    }


    @DisplayName("유저 정보 업데이트")
    @Test
    void givenUserUpdatedInfo_whenRequestingUpdateUser_thenReturnUser() {
        // Given
        willReturn(userEntity()).given(userEntityRepositoryService).findByUsernameOrException(any());

        // When
        sut.updateUserInfo(UpdateUserRequest(), userEntity().getUsername());

        //Then
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(any());
    }

}
