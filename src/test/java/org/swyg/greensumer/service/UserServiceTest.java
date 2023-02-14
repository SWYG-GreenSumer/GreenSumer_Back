package org.swyg.greensumer.service;

import org.junit.jupiter.api.Assertions;
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
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.UpdateUserRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.store.SellerStoreEntityRepository;
import org.swyg.greensumer.repository.user.UserCacheRepository;
import org.swyg.greensumer.repository.user.UserEntityRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.getUpdateUserRequest;
import static org.swyg.greensumer.fixture.RequestFixture.getUserSignUpRequest;

@DisplayName("비즈니스 로직 - 유저")
@TestPropertySource(properties = {"jwt.token.expired-time-ms=2500000", "jwt.secret-key=abcdeabcdeabcdeabcdeabcdeabcdeabcdeabcde222"})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class UserServiceTest {

    private UserService sut;

    @Mock private UserEntityRepository userEntityRepository;
    @Mock private VerificationService verificationService;
    @Mock private UserCacheRepository userCacheRepository;
    @Mock private BCryptPasswordEncoder encoder;
    @Mock private StoreService storeService;
    @Mock private AddressService addressService;
    @Mock private SellerStoreEntityRepository sellerStoreEntityRepository;

    @Value("${jwt.secret-key}") String secretKey;
    @Value("${jwt.token.expired-time-ms}") Long expiredTimeMs;

    @BeforeEach
    void setUp() {
        sut = new UserService(userEntityRepository, storeService, addressService, sellerStoreEntityRepository, userCacheRepository, verificationService, encoder);
        sut.setSecretKey(secretKey);
        sut.setExpiredTimeMs(expiredTimeMs);
    }

    @DisplayName("유저정보를 입력하면, 회원가입이 성공한다.")
    @Test
    void givenUserInfo_whenRequestingSignUp_thenReturnUser() {
        // Given
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());
        given(addressService.findAddressEntity(any(), any(), any(), any())).willReturn(getAddressEntity());
        given(userEntityRepository.save(any())).willReturn(createSignUpUserAccount());
        given(storeService.searchStore(any())).willReturn(getStoreEntity());
        given(sellerStoreEntityRepository.save(any())).willReturn(getSellerStoreEntity());

        // When
        User user = sut.signup(getUserSignUpRequest());

        //Then
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(addressService, times(1)).findAddressEntity(any(), any(), any(), any());
        verify(userEntityRepository, times(1)).save(any());
        verify(storeService, times(1)).searchStore(any());
        verify(sellerStoreEntityRepository, times(1)).save(any());
    }

    @DisplayName("토큰이 없는 상태로 아이디와 비밀번호를 입력하면, 로그인할 수 있다.")
    @Test
    void givenUsernameAndPassword_whenRequestingLogin_thenReturnToken() {
        // Given
        given(userCacheRepository.getUser(any())).willReturn(Optional.empty());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(mock(UserEntity.class)));
        willDoNothing().given(userCacheRepository).setUser(any());
        given(encoder.matches(any(), any())).willReturn(true);

        // when
        Assertions.assertDoesNotThrow(() -> sut.login(getUsername(), getPassword()));

        // then
        verify(userCacheRepository, times(1)).getUser(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(userCacheRepository, times(1)).setUser(any());
        verify(encoder, times(1)).matches(any(), any());
    }

    @DisplayName("토큰을 가진 유저가 로그인하는 경우, 로그인 성공한다.")
    @Test
    void givenUsernameAndPassword_whenRequestingLoginWithToken_thenReturnToken() {
        // Given
        given(userCacheRepository.getUser(any())).willReturn(Optional.of(getUser()));
        willDoNothing().given(userCacheRepository).setUser(any());
        given(encoder.matches(any(), any())).willReturn(true);

        // when
        Assertions.assertDoesNotThrow(() -> sut.login(getUsername(), getPassword()));

        // then
        verify(userCacheRepository, times(1)).getUser(any());
        verify(userCacheRepository, times(1)).setUser(any());
        verify(encoder, times(1)).matches(any(), any());
    }

    @DisplayName("유저의 아이디가 중복돠면 에러가 발생한다.")
    @Test
    void givenUsername_whenRequestingIsDuplicateUsername_thenThrowDuplicateException() {
        // Given
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(mock(UserEntity.class)));

        // When
        GreenSumerBackApplicationException exception = assertThrows(GreenSumerBackApplicationException.class, () -> sut.existUsername(getUsername()));

        //Then
        assertEquals(exception.getErrorCode(), ErrorCode.DUPLICATED_USERNAME);
        verify(userEntityRepository, times(1)).findByUsername(getUsername());
    }

    @DisplayName("로그인시 존재하지 않는 아이디를 로그인한 경우 에러가 발생한다.")
    @Test
    void givenLoginInfo_whenRequestingLogin_thenThrowUserNotFoundException() {
        // Given
        given(userCacheRepository.getUser(any())).willReturn(Optional.empty());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());


        // when
        GreenSumerBackApplicationException exception = assertThrows(GreenSumerBackApplicationException.class, () -> sut.login(getUsername(), getPassword()));

        //Then
        assertEquals(exception.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(userCacheRepository, times(1)).getUser(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
    }

    @DisplayName("로그인시 패스워드가 일치하지 않는 경우")
    @Test
    void givenLoginInfo_whenRequestingLogin_thenThrowInvalidPasswordException() {
        // Given
        given(userCacheRepository.getUser(any())).willReturn(Optional.empty());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(mock(UserEntity.class)));
        given(encoder.matches(any(), any())).willReturn(false);

        // when
        GreenSumerBackApplicationException exception = assertThrows(GreenSumerBackApplicationException.class, () -> sut.login(getUsername(), getPassword()));

        //Then
        assertEquals(exception.getErrorCode(), ErrorCode.INVALID_PASSWORD);
        verify(userCacheRepository, times(1)).getUser(any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(encoder).matches(any(), any());
    }

    @DisplayName("아이디 찾기 정상 요청")
    @Test
    void givenEmailAndCode_whenRequestingFindUsername_thenReturnUser() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        given(userEntityRepository.findByEmail(any())).willReturn(Optional.of(mock(UserEntity.class)));
        willDoNothing().given(verificationService).clear(any());

        // When
        User user = sut.findUsername(any(), any());

        //Then
        assertThat(user)
                .isNotNull();
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepository, times(1)).findByEmail(any());
        verify(verificationService, times(1)).clear(any());
    }

    @DisplayName("아이디 찾기 요청 시 존재하지 않는 아이디인 경우")
    @Test
    void givenEmailAndCode_whenRequestingFindUsername_thenThrowUserNotFoundException() {
        // Given
        willDoNothing().given(verificationService).checkMail(any(), any());
        given(userEntityRepository.findByEmail(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.findUsername(any(), any()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepository, times(1)).findByEmail(any());
        verify(verificationService, times(0)).clear(any());
    }

    @DisplayName("비밀번호 찾기 정상 요청")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenReturnNothing() {
        // Given
        UserEntity user = createUserEntity();
        willDoNothing().given(verificationService).checkMail(any(), any());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(user));
        given(encoder.matches(any(), any())).willReturn(true);
        willDoNothing().given(verificationService).clear(any());
        given(userEntityRepository.save(any())).willReturn(user);

        // When
        sut.findPassword(user.getUsername(), user.getEmail(), "code", "modifiedPassword");

        //Then
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(encoder, times(1)).matches(any(), any());
        verify(verificationService, times(1)).clear(any());
        verify(userEntityRepository, times(1)).save(user);
    }

    @DisplayName("비밀번호 찾기 요청 시 아이디가 존재하지 않는 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowUserNotFoundException() {
        // Given
        UserEntity user = createUserEntity();
        willDoNothing().given(verificationService).checkMail(any(), any());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.findPassword(user.getUsername(), user.getEmail(), "code", "modifiedPassword"));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(encoder, times(0)).matches(any(), any());
        verify(verificationService, times(0)).clear(any());
        verify(userEntityRepository, times(0)).save(user);
    }

    @DisplayName("비밀번호 찾기 요청 시 비밀번호가 동일한 경우")
    @Test
    void givenPasswordUpdateRequest_whenRequestingUpdatePassword_thenThrowConflictException() {
        // Given
        UserEntity user = createUserEntity();
        willDoNothing().given(verificationService).checkMail(any(), any());
        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(user));
        given(encoder.matches(any(), any())).willReturn(false);

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.findPassword(user.getUsername(), user.getEmail(), "code", "modifiedPassword"));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.SAME_AS_PREVIOUS_PASSWORD);
        verify(verificationService, times(1)).checkMail(any(), any());
        verify(userEntityRepository, times(1)).findByUsername(any());
        verify(encoder, times(1)).matches(any(), any());
        verify(verificationService, times(0)).clear(any());
        verify(userEntityRepository, times(0)).save(user);
    }

    @DisplayName("유저 정보 업데이트")
    @Test
    void givenUserUpdatedInfo_whenRequestingUpdateUser_thenReturnUser() {
        // Given
        String username = getUsername();
        UpdateUserRequest request = getUpdateUserRequest();
        UserEntity userEntity = getUserEntity();

        given(userEntityRepository.findByUsername(username)).willReturn(Optional.of(userEntity));
        given(userEntityRepository.saveAndFlush(userEntity)).willReturn(userEntity);

        // When
        User user = sut.updateUserInfo(request, username);

        //Then
        assertThat(userEntity.getUsername()).isEqualTo(username);
        assertThat(user).isNotNull()
                .hasFieldOrPropertyWithValue("nickname", request.getNickname())
                .hasFieldOrPropertyWithValue("email", request.getEmail());

        verify(userEntityRepository, times(1)).findByUsername(username);
        verify(userEntityRepository, times(1)).saveAndFlush(userEntity);
    }

    @DisplayName("유저 정보 업데이트 요청 시, 본인이 아닌 경우")
    @Test
    void givenUpdatedUserInfo_whenRequestingUpdateUser_thenThrowsInvalidPermissionException() {
        // Given
        String username = "other";
        UpdateUserRequest request = getUpdateUserRequest();
        UserEntity userEntity = getUserEntity();

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> sut.updateUserInfo(request, username));

        //Then
        assertThat(userEntity.getUsername()).isNotEqualTo(username);
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        verify(userEntityRepository, times(0)).findByUsername(username);
        verify(userEntityRepository, times(0)).saveAndFlush(userEntity);
    }

}
