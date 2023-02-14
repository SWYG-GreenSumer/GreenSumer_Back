package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.user.UserCacheRepository;
import org.swyg.greensumer.repository.user.UserEntityRepository;

@RequiredArgsConstructor
@Service
public class UserEntityRepositoryService {

    private final UserEntityRepository userEntityRepository;
    private final UserCacheRepository userCacheRepository;

    public UserEntity findByUsernameOrException(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });
    }

    public User loadUserByUsername(String username) {
        return userCacheRepository.getUser(username).orElseGet(() ->
                userEntityRepository.findByUsername(username).map(User::fromEntity).orElseThrow(() ->
                        new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username))));
    }

    public void existUsername(String username) {
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });
    }

    public UserEntity findByEmail(String email) {
        return userEntityRepository.findByEmail(email).orElseThrow(() ->
                new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));
    }

    public UserEntity save(UserEntity userEntity) {
        return userEntityRepository.save(userEntity);
    }

    public void setRefreshToken(User user, String refreshToken) {
        userCacheRepository.setUser(user);
        userCacheRepository.setRefreshToken(user.getUsername(), refreshToken);
    }

    public String getRefreshToken(String username) {
        return userCacheRepository.getRefreshToken(username);
    }

    public void deleteRefreshToken(String username){
        userCacheRepository.deleteRefreshToken(username);
    }
}
