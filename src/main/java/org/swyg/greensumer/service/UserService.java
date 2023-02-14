package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.domain.SellerStoreEntity;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.TokenInfo;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.*;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.store.SellerStoreEntityRepository;
import org.swyg.greensumer.utils.JwtTokenUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {

    private final StoreService storeService;
    private final AddressService addressService;
    private final UserEntityRepositoryService userEntityRepositoryService;
    private final SellerStoreEntityRepository sellerStoreEntityRepository;
    private final VerificationService verificationService;

    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setExpiredTimeMs(Long expiredTimeMs) {
        this.expiredTimeMs = expiredTimeMs;
    }

    @Transactional
    public User signup(UserSignUpRequest request) {
        userEntityRepositoryService.existUsername(request.getUsername());

        UserEntity userEntity = userEntityRepositoryService.save(UserEntity.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .fullname(request.getName())
                .email(request.getEmail())
                .birth(LocalDateTime.parse(request.getBirth()))
                .gender(request.isGender())
                .roles(Collections.singletonList(UserRole.USER.name()))
                .build());

        return User.fromEntity(userEntity);
    }

    private void mappingSellerAndStore(UserEntity userEntity, AddressEntity addressEntity) {
        StoreEntity storeEntity = storeService.searchStore(addressEntity);

        SellerStoreEntity sellerStoreEntity = SellerStoreEntity.of(storeEntity, userEntity);
        storeEntity.addSellerStore(sellerStoreEntity);
        sellerStoreEntityRepository.save(sellerStoreEntity);
    }

    public TokenInfo login(UserLoginRequest request) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(request.getUsername());
        User user = User.fromEntity(userEntity);

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        TokenInfo tokenInfo = JwtTokenUtils.createTokenInfo(user, secretKey);
        userEntityRepositoryService.setRefreshToken(user, tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public TokenInfo reissue(String accessToken, String refreshToken) {
        String username = JwtTokenUtils.getUsername(accessToken, secretKey);
        String refreshTokenFromRedis = userEntityRepositoryService.getRefreshToken(username);
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        User user = User.fromEntity(userEntity);

        if (ObjectUtils.isEmpty(refreshTokenFromRedis)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_TOKEN, "Invalid Access Token");
        }

        if (!refreshToken.equals(refreshTokenFromRedis.substring(1, refreshTokenFromRedis.length() - 1))) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, "Refresh Token is invalid.");
        }

        TokenInfo tokenInfo = JwtTokenUtils.createTokenInfo(user, secretKey);
        userEntityRepositoryService.setRefreshToken(user, tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    public void logout(UserLogoutRequest logout) {
        String username = JwtTokenUtils.getUsername(logout.getAccessToken(), secretKey);
        userEntityRepositoryService.deleteRefreshToken(username);
    }

    @Transactional(readOnly = true)
    public void existUsername(String username) {
        userEntityRepositoryService.existUsername(username);
    }

    @Transactional(readOnly = true)
    public User findUsername(String email, String code) {
        verificationService.checkMail(email, code);
        return User.fromEntity(userEntityRepositoryService.findByEmail(email));
    }

    @Transactional
    public void findPassword(String username, String email, String code, String password) {
        verificationService.checkMail(email, code);

        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        if (encoder.matches(password, userEntity.getPassword())) {
            throw new GreenSumerBackApplicationException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD, String.format("%s same as before", password));
        }

        userEntity.updatePassword(encoder.encode(password));
    }

    @Transactional
    public User updateUserInfo(UpdateUserRequest request, String username) {
        if (!Objects.equals(request.getUsername(), username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission to %s", username, request.getUsername()));
        }

        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        userEntity.updateUserInfo(encoder.encode(request.getPassword()), request.getNickname(), request.getEmail());

        return User.fromEntity(userEntity);
    }

    public void requestLevelUp(RequestLevelUp request, String username) {
        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);
        userEntity.updateRole(request.getRole());
    }
}

