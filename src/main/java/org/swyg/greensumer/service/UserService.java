package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.domain.SellerStoreEntity;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.User;
import org.swyg.greensumer.dto.request.UpdateUserRequest;
import org.swyg.greensumer.dto.request.UserSignUpRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.SellerStoreEntityRepository;
import org.swyg.greensumer.repository.UserCacheRepository;
import org.swyg.greensumer.repository.UserEntityRepository;
import org.swyg.greensumer.utils.JwtTokenUtils;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final StoreService storeService;
    private final AddressService addressService;
    private final SellerStoreEntityRepository sellerStoreEntityRepository;
    private final UserCacheRepository userCacheRepository;
    private final VerificationService verificationService;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret-key}") private String secretKey;
    @Value("${jwt.token.expired-time-ms}") private Long expiredTimeMs;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setExpiredTimeMs(Long expiredTimeMs) {
        this.expiredTimeMs = expiredTimeMs;
    }

    @Transactional
    public User signup(UserSignUpRequest request) {
        // 1. 회원가입된 유저인지 확인한다.
        String username = request.getUsername();

        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", username));
        });

        AddressEntity addressEntity = null;

        if(request.getAddress() != null){
            addressEntity = addressService.findAddressEntity(request.getAddress(), request.getAddress(), request.getLat(), request.getLat());
        }

        UserEntity userEntity = UserEntity.of(
                request.getUsername(),
                encoder.encode(request.getPassword()),
                request.getNickname(),
                request.getEmail(),
                addressEntity
        );

        // 3. 회원을 저장한다.
        UserEntity savedUser = userEntityRepository.save(userEntity);

        // 4. 유저가 Seller 인 경우
        if(savedUser.getRole() == UserRole.SELLER){
            mappingSellerAndStore(savedUser, addressEntity);
        }

        return User.fromEntity(savedUser);
    }

    private void mappingSellerAndStore(UserEntity userEntity, AddressEntity addressEntity) {
        StoreEntity storeEntity = storeService.searchStore(addressEntity);

        SellerStoreEntity sellerStoreEntity = SellerStoreEntity.of(storeEntity, userEntity);
        storeEntity.addSellerStore(sellerStoreEntity);
        sellerStoreEntityRepository.save(sellerStoreEntity);
    }

    public String login(String username, String password) {
        User user = loadUserByUsername(username);

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        userCacheRepository.setUser(user);

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

    @Transactional
    public User findUsername(String email, String code) {
        verificationService.checkMail(email, code);

        UserEntity user = userEntityRepository.findByEmail(email).orElseThrow(() ->
                new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", email)));

        verificationService.clear(email);

        return User.fromEntity(user);
    }

    @Transactional
    public void findPassword(String username, String email, String code, String password) {
        verificationService.checkMail(email, code);

        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        if (!encoder.matches(password, user.getPassword())) {
            throw new GreenSumerBackApplicationException(ErrorCode.SAME_AS_PREVIOUS_PASSWORD, String.format("%s same as before", password));
        }

        verificationService.clear(email);

        user.setPassword(encoder.encode(password));
        userEntityRepository.save(user);
    }

    @Transactional
    public User updateUserInfo(UpdateUserRequest request, String username) {
        if (request.getUsername() != username) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission to %s", username, request.getUsername()));
        }

        UserEntity userEntity = findByUsernameOrException(username);

        userEntity.setPassword(encoder.encode(request.getPassword()));
        userEntity.setNickname(request.getNickname());
        userEntity.setEmail(request.getEmail());

        if(userEntity.getRole() == UserRole.SELLER){
            AddressEntity addressEntity = addressService.updateAddress(userEntity.getAddressEntity().getId(), request.getAddress(), request.getAddress(), request.getLat(), request.getLat());
            userEntity.setAddressEntity(addressEntity);
        }

        return User.fromEntity(userEntity);
    }

    public UserEntity findByUsernameOrException(String username) {
        return userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });
    }

}

