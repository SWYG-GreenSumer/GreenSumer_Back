package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.UpdateUserRequest;
import org.swyg.greensumer.dto.request.UserSignUpRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.UserCacheRepository;
import org.swyg.greensumer.repository.UserEntityRepository;
import org.swyg.greensumer.utils.JwtTokenUtils;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final UserCacheRepository userCacheRepository;
    private final VerificationService verificationService;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    @Transactional
    public User signup(UserSignUpRequest request) {
        String username = request.getUsername();
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });

        UserEntity userEntity = userEntityRepository.save(UserEntity.of(
                request.getUsername(),
                encoder.encode(request.getPassword()),
                request.getNickname(),
                request.getEmail(),
                request.getAddress(),
                request.getLat(),
                request.getLng()
        ));

        return User.fromEntity(userEntity);
    }

    public String login(String username, String password) {
        User user = loadUserByUsername(username);
        userCacheRepository.setUser(user);

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        return JwtTokenUtils.generateToken(username, secretKey, expiredTimeMs);
    }

    public User loadUserByUsername(String username) {
        return userCacheRepository.getUser(username).orElseGet(() ->
                userEntityRepository.findByUsername(username).map(User::fromEntity).orElseThrow(() ->
                        new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))));
    }

    @Transactional(readOnly = true)
    public void existUsername(String username) {
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });
    }

    @Transactional(readOnly = true)
    public User findUsername(String email) {
        UserEntity user = userEntityRepository.findByEmail(email).orElseThrow(() ->
                new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));

        return User.fromEntity(user);
    }

    @Transactional
    public void findPassword(String username, String email, String code, String password) {
        verificationService.checkMail(email, code);

        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        if(encoder.matches(password, user.getPassword())){
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s same as before", password));
        }

        user.setPassword(encoder.encode(password));
        userEntityRepository.save(user);
    }

    @Transactional
    public User updateUserInfo(UpdateUserRequest request, String username) {
        if(!request.getUsername().equals(username)){
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission to %s", username, request.getUsername()));
        }

        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });


        user.setPassword(encoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());

        String address = request.getAddress();
        if(address != null){
            user.setAddress(address);
        }

        UserEntity updatedUser = userEntityRepository.saveAndFlush(user);
        return User.fromEntity(updatedUser);
    }
}
