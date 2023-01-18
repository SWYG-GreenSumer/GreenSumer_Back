package org.swyg.greensumer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.swyg.greensumer.domain.VerificationEntity;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.VerificationEntityRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.swyg.greensumer.fixture.Fixtures.getVerificationEntity;

@DisplayName("비즈니스 로직 - 메일 인증")
@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @InjectMocks private VerificationService verificationService;
    @Mock private VerificationEntityRepository verificationEntityRepository;
    @Mock private JavaMailSender mailSender;

    @DisplayName("메일 인증 전송 - 정상 호출")
    @Test
    void givenEmail_whenRequestingSendMail_thenReturnNothing() {
        // Given
        String email = "email";

        given(verificationEntityRepository.save(any())).willReturn(getVerificationEntity());

        // When
        verificationService.sendMail(email);

        //Then
        verify(verificationEntityRepository, times(1)).save(any());
    }

    @DisplayName("메일 인증 확인 - 정상 호출")
    @Test
    void givenEmailAndCode_whenRequestingCheckMailAndCode_thenReturnNothing() {
        // Given
        VerificationEntity verification = getVerificationEntity();
        String email = verification.getSubject();
        given(verificationEntityRepository.findBySubject(any())).willReturn(Optional.of(getVerificationEntity()));
        given(verificationEntityRepository.saveAndFlush(any())).willReturn(verification);

        // When
        verificationService.checkMail(email, verification.getCode());

        //Then
        verify(verificationEntityRepository, times(1)).findBySubject(any());
        verify(verificationEntityRepository, times(1)).saveAndFlush(any());
    }

    @DisplayName("메일 인증 확인 - 인증요청을 하지 않은 경우")
    @Test
    void givenEmailAndCode_whenRequestingCheckMailAndCode_thenThrowMailNotFoundException() {
        // Given
        VerificationEntity verification = getVerificationEntity();
        String email = verification.getSubject();
        given(verificationEntityRepository.findBySubject(email)).willReturn(Optional.empty());

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> verificationService.checkMail(email, verification.getCode()));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.MAIL_NOT_FOUND);
        verify(verificationEntityRepository, times(1)).findBySubject(email);
        verify(verificationEntityRepository, times(0)).saveAndFlush(verification);
    }

    @DisplayName("메일 인증 확인 - 인증번호가 일치하지 않은 경우")
    @Test
    void givenEmailAndCode_whenRequestingCheckMailAndCode_thenThrowInvalidVerificationCodeException() {
        // Given
        VerificationEntity verification = getVerificationEntity();
        String email = verification.getSubject();
        given(verificationEntityRepository.findBySubject(email)).willReturn(Optional.of(verification));


        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> verificationService.checkMail(email, "nonono"));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_VERIFICATION_CODE);
        verify(verificationEntityRepository, times(1)).findBySubject(email);
        verify(verificationEntityRepository, times(0)).saveAndFlush(verification);
    }

    @DisplayName("메일 인증 확인 - 유효기간을 벗어난 경우")
    @Test
    void givenEmailAndCode_whenRequestingCheckMailAndCode_thenThrowInvalidVerificationCode2Exception() {
        // Given
        VerificationEntity verification = getVerificationEntity();
        String email = verification.getSubject();
        verification.setExpiredAt(Timestamp.from(Instant.now()));

        given(verificationEntityRepository.findBySubject(email)).willReturn(Optional.of(verification));

        // When
        GreenSumerBackApplicationException e = assertThrows(GreenSumerBackApplicationException.class, () -> verificationService.checkMail(email, "nonono"));

        //Then
        assertEquals(e.getErrorCode(), ErrorCode.INVALID_VERIFICATION_CODE);
        verify(verificationEntityRepository, times(1)).findBySubject(email);
        verify(verificationEntityRepository, times(0)).saveAndFlush(verification);
    }
}