package org.swyg.greensumer.fixture;

import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.request.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class RequestFixture {
    private static final Integer productId = 1;
    private static final Long STORE_ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final String NICKNAME = "nickname";
    private static final String TITLE = "title";
    private static final String BIRTH = "2000-10-01";
    private static final String PHONE = "010-1234-1234";
    private static final boolean GENDER = true;
    private static final String CONTENT = "content";
    private static final String image = "image";
    private static final String COMMENT = "comment";
    private static final String NAME = "name";
    private static final String storeType = StoreType.FOOD.toString();
    private static final String DESCRIPTION = "description";
    private static final String ADDRESS = "address";
    private static final String HOURS = "hours";
    private static final Double LAT = 123.123123123;
    private static final Double LNG = 123.123123123;
    private static final String logo = "logo";
    private static final String CODE = "code";
    private static final String ROADNAME = "roadname";
    private static final int PRICE = 5000;
    private static final int STOCK = 5;
    private static final int modifiedPrice = PRICE + 1000;
    private static final int modifiedStock = STOCK + 1;
    private static final String URL = "http://www.naver.cion";
    private static final String AccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRlc3QxIiwiaWF0IjoxNjc2NzkwNDgwLCJleHAiOjE2NzY3OTIyODB9.NhUIC2NSpLU1dBpoyQWjGU9O-nNfMczerYl-naPrihk";
    private static final String RefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzY3OTA0ODAsImV4cCI6MTY3NzM5NTI4MH0.Bk5sjSRHUaM-H0A1y_OloAwvX0G0HcJOLRND_H6yws0";
    private static final List<Long> IMAGES = List.of(1L, 2L, 3L);
    private static final List<Long> PRODUCTS = List.of(1L, 2L, 3L);
    private static final String RATING = "★★★★★";

    public static UserSignUpRequest UserSignUpRequest() {
        return UserSignUpRequest.of(USERNAME, PASSWORD, NAME, BIRTH, NICKNAME, EMAIL, PHONE, GENDER);
    }

    public static UserLoginRequest UserLoginRequest() {
        return UserLoginRequest.of(USERNAME, PASSWORD);
    }

    public static VerificationRequest VerificationRequest() {
        return VerificationRequest.of(EMAIL);
    }

    public static VerificationCheckRequest VerificationCheckRequest() {
        return VerificationCheckRequest.of(EMAIL, CODE);
    }

    public static PasswordUpdateRequest PasswordUpdateRequest() {
        return PasswordUpdateRequest.of(USERNAME, EMAIL, CODE, PASSWORD);
    }

    public static UserLogoutRequest UserLogoutRequest() {
        return UserLogoutRequest.of(AccessToken, RefreshToken);
    }

    public static UserReissueRequest UserReissueRequest() {
        return UserReissueRequest.of(AccessToken, RefreshToken);
    }

    public static UpdateUserRequest UpdateUserRequest() {
        return UpdateUserRequest.of(USERNAME, PASSWORD, EMAIL, NICKNAME);
    }

    public static StoreCreateRequest StoreCreateRequest() {
        return StoreCreateRequest.of(NAME, StoreType.CLOTHES.toString(), DESCRIPTION, HOURS, IMAGES, ADDRESS, ROADNAME, URL, PHONE, LAT, LNG);
    }

    public static StoreModifyRequest StoreModifyRequest() {
        return StoreModifyRequest.of(NAME, StoreType.CLOTHES.toString(), DESCRIPTION, HOURS, IMAGES, ADDRESS, ROADNAME, URL, PHONE, LAT, LNG);
    }

    public static ProductCreateRequest ProductCreateRequest() {
        return ProductCreateRequest.of(NAME, PRICE, STOCK, DESCRIPTION, IMAGES);
    }

    public static ProductModifyRequest ProductModifyRequest() {
        return ProductModifyRequest.of(NAME, PRICE, STOCK, DESCRIPTION, IMAGES);
    }

    public static ReviewPostCreateRequest ReviewPostCreateRequest() {
        return ReviewPostCreateRequest.of(STORE_ID, TITLE, CONTENT, RATING, PRODUCTS, IMAGES);
    }

    public static ReviewPostModifyRequest ReviewPostModifyRequest() {
        return ReviewPostModifyRequest.of(STORE_ID, TITLE, CONTENT, RATING, PRODUCTS, IMAGES);
    }

    public static ReviewCommentCreateRequest ReviewCommentCreateRequest() {
        return ReviewCommentCreateRequest.of(COMMENT);
    }

    public static ReviewCommentModifyRequest ReviewCommentModifyRequest() {
        return ReviewCommentModifyRequest.of(COMMENT);
    }

    public static EventPostCreateRequest EventPostCreateRequest() {
        return EventPostCreateRequest.of(STORE_ID, TITLE, CONTENT, PRODUCTS, IMAGES, LocalDateTime.now().toString(), LocalDateTime.now().plusDays(30L).toString());
    }

    public static EventPostModifyRequest EventPostModifyRequest() {
        return EventPostModifyRequest.of(STORE_ID, TITLE, CONTENT, PRODUCTS, IMAGES, LocalDateTime.now().toString(), LocalDateTime.now().plusDays(30L).toString());
    }

    public static EventCommentCreateRequest EventCommentCreateRequest() {
        return EventCommentCreateRequest.of(CONTENT);
    }

    public static EventCommentModifyRequest EventCommentModifyRequest() {
        return EventCommentModifyRequest.of(CONTENT);
    }

    public static ImagesCreateRequest ImagesCreateRequest() {
        return new ImagesCreateRequest(Collections.emptyList(), ImageType.EVENT.toString());
    }

    public static ImageModifyRequest ImageModifyRequest() {
        return new ImageModifyRequest(null, ImageType.EVENT.toString());
    }

    public static InterviewCreateRequest InterviewCreateRequest() {
        return new InterviewCreateRequest(USERNAME, NAME, COMMENT, UserRole.ROLE_USER.toString());
    }

    public static InterviewModifyRequest InterviewModifyRequest() {
        return new InterviewModifyRequest(USERNAME, NAME, COMMENT, UserRole.ROLE_USER.toString());
    }

}
