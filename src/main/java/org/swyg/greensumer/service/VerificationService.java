package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.VerificationEntity;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.VerificationEntityRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationService {

    static final String writer = "bang77060986@gmail.com";
    static final String mailSubject = "[GreenSumer] 인증번호 발송";

    private final JavaMailSender mailSender;
    private final VerificationEntityRepository verificationEntityRepository;

    public void sendMail(String email) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            String code = generateRandom();
            message.setFrom(writer);
            message.setTo(email);
            message.setSubject(mailSubject);
            message.setText(createText(code));

            mailSender.send(message);

            VerificationEntity verification = verificationEntityRepository.save(VerificationEntity.of(email, code));
            log.info("send Mail to {}", verification.getSubject());
        }catch (Exception e){
            throw new GreenSumerBackApplicationException(ErrorCode.MAIL_SEND_ERROR, String.format("%s cant sent Mail", email));
        }
    }

    public void checkMail(String subject, String value){
        // TODO: DB에 많은 영향을 줄 것으로 판단되어 캐시로 변경할 것
        VerificationEntity verification = verificationEntityRepository.findBySubject(subject).orElseThrow(
                () -> new GreenSumerBackApplicationException(ErrorCode.MAIL_NOT_FOUND, String.format("%s not founded", subject))
        );

        Timestamp now = Timestamp.from(Instant.now());

        if(!(verification.getCode().equals(value) && now.before(verification.getExpiredAt()) && now.after(verification.getStartedAt()))){
            verificationEntityRepository.deleteById(verification.getId());
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_VERIFICATION_CODE, String.format("%s is invalid", value));
        }
    }

    private String createText(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append("본 메일은 GreenSumer 사이트의 회원가입을 위한 이메일 인증입니다.").append("\n");
        sb.append("인증번호: ").append(value).append("\n");
        sb.append("감사합니다.").append("\n").append("Greensumer 서비스 담당자");
        return sb.toString();
    }

    private String generateRandom() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter((x) -> (x <= 57 || x >= 65) && (x <= 90 || x >= 97))
                .limit((long)targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
