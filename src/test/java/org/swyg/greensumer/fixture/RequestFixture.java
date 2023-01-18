package org.swyg.greensumer.fixture;

import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.dto.request.*;

public class RequestFixture {
    private static final Integer productId = 1;
    private static final Integer storeId = 1;
    private static final String username = "username";
    private static final String password = "password";
    private static final String email = "email";
    private static final String nickname = "nickname";
    private static final String title = "title";
    private static final String content = "content";
    private static final String image = "image";
    private static final String comment = "comment";
    private static final String name = "name";
    private static final String storeType = StoreType.FOOD.toString();
    private static final String description = "description";
    private static final String address = "address";
    private static final String hours = "hours";
    private static final Double lat = 123.123123123;
    private static final Double lng = 123.123123123;
    private static final String logo = "logo";
    private static final String code = "code";
    private static final String roadname = "roadname";
    private static final int price = 5000;
    private static final int stock = 5;
    private static final int modifiedPrice = price + 1000;
    private static final int modifiedStock = stock + 1;

    public static ReviewPostCreateRequest getReviewPostCreateRequest() {
        return ReviewPostCreateRequest.of(productId, title, content, image);
    }

    public static ReviewPostModifyRequest getReviewPostModifyRequest() {
        return ReviewPostModifyRequest.of(productId, title, content, image);
    }

    public static ReviewCommentCreateRequest getReviewCommentCreateRequest() {
        return ReviewCommentCreateRequest.of(comment);
    }

    public static ReviewCommentModifyRequest getReviewCommentModifyRequest() {
        return ReviewCommentModifyRequest.of(comment);
    }

    public static StoreCreateRequest getStoreCreateRequest() {
        return StoreCreateRequest.of(name, storeType, description, address, roadname, hours, lat, lng, logo);
    }

    public static StoreModifyRequest getStoreModifyRequest() {
        return StoreModifyRequest.of(name, storeType, description, address, hours, roadname, lat, lng, logo);
    }

    public static ProductCreateRequest getProductCreateRequest() {
        return ProductCreateRequest.of(name, price, stock, description, image);
    }

    public static ProductModifyRequest getProductModifyRequest() {
        return ProductModifyRequest.of(name, modifiedPrice, modifiedStock, description, image);
    }

    public static UserSignUpRequest getUserSignUpRequest() {
        return UserSignUpRequest.of(username, password, email, nickname, storeId, address, lat, lng);
    }

    public static UserLoginRequest getUserLoginRequest() {
        return UserLoginRequest.of(username, password);
    }

    public static VerificationRequest getVerificationRequest() {
        return VerificationRequest.of(email);
    }

    public static VerificationCheckRequest getVerificationCheckRequest() {
        return VerificationCheckRequest.of(email, code);
    }

    public static UsernameRequest getUsernameRequest() {
        return UsernameRequest.of(email, code);
    }

    public static PasswordUpdateRequest getPasswordUpdateRequest() {
        return PasswordUpdateRequest.of(username, email, code, password);
    }

    public static UpdateUserRequest getUpdateUserRequest(){
        return UpdateUserRequest.of(username, password, email, nickname, address, lat, lng);
    }
}
