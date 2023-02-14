package org.swyg.greensumer.fixture;

import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public class Fixtures {
    private static final String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNjczMDIwMDYwLCJleHAiOjE2NzU2MTIwNjB9.USuRnODheSfeL65rpXqQOMkVLnOqCtSacrJsLNQXSNg";
    private static final Integer id = 1;
    private static final int price = 5000;
    private static final int stock = 5;
    private static final int modifiedPrice = price + 1000;
    private static final int modifiedStock = stock + 1;
    private static final String title = "title";
    private static final String content = "content";
    private static final String image = "image";
    private static final String username = "username";
    private static final String password = "password";
    private static final String email = "email";
    private static final String nickname = "nickname";
    private static final String name = "name";
    private static final String modifiedName = "modified Name";
    private static final String address = "address";
    private static final String description = "description";
    private static final Double lat = 123.123123123123;
    private static final Double lng = 123.123123123123;
    private static final String hours = "hours";
    private static final String logo = "logo";
    private static final String roadname = "roadname";
    private static final UserRole userRole = UserRole.USER;
    private static final UserRole sellerRole = UserRole.SELLER;

    public static ReviewPost getReviewPost() {
        return new ReviewPost(id, title, content, image, getProduct(), getUser(), getNow(), null, null);
    }

    public static Product getProduct() {
        return new Product(id, name, price, stock, description, image, getNow(), null, null);
    }

    public static User getUser() {
        return new User(id, username, password, email, nickname, getAddress(), userRole, getNow(), null, null);
    }

    public static Store getStore() {
        return new Store(id, getSetSellerStores(), name, description, getAddress(), hours, logo, getNow(), null, null);
    }

    public static AddressEntity getAddressEntity(){
        return createAddressEntity();
    }

    public static AddressEntity createAddressEntity() {
        return AddressEntity.of(1L, address, roadname, lat, lng, getNow(), null, null);
    }

    public static Address getAddress() {
        return Address.fromEntity(createAddressEntity());
    }

    public static SellerStoreEntity getSellerStoreEntity() {
        SellerStoreEntity sellerStoreEntity = new SellerStoreEntity();
        sellerStoreEntity.setSeller(createUserEntity());
        sellerStoreEntity.setStore(createStoreEntity());
        return sellerStoreEntity;
    }

    public static Set<SellerStore> getSetSellerStores() {
        Set<SellerStore> sellerStores = new LinkedHashSet<>();
        return sellerStores;
    }

    private static Timestamp getNow() {
        return Timestamp.from(Instant.now());
    }

    public static ReviewComment getReviewComment() {
        return new ReviewComment(id, content, getUser(), getReviewPost(), getNow(), null, null);
    }

    public static ReviewPostWithComment getReviewPostWithComment() {
        return new ReviewPostWithComment(id, title, content, image, getProduct(), getUser(), Set.of(getReviewComment()), getNow(), null, null);
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getName() {
        return name;
    }

    public static Integer getId() {
        return id;
    }

    public static UserEntity createUserEntity() {
        return UserEntity.of(username, password, nickname, email, getAddressEntity());
    }

    public static StoreEntity createStoreEntity() {
        return StoreEntity.of(id, name, description, createAddressEntity(), hours, logo, StoreType.FOOD);
    }

    public static ProductEntity createProductEntity() {
        return ProductEntity.of(id, createStoreEntity(), name, price, stock, description, image);
    }

    public static ProductEntity ModifyProductEntity(ProductEntity entity) {
        entity.setName(modifiedName);
        entity.setStock(modifiedStock);
        entity.setPrice(modifiedPrice);
        return entity;
    }

    public static UserEntity createSignUpUserAccount() {
        UserEntity userEntity = createUserEntity();
        userEntity.setId(id);
        return userEntity;
    }

    public static UserEntity getUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);
        return user;
    }

    public static UserEntity getSellerEntity() {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setAddressEntity(getAddressEntity());
        user.setRole(sellerRole);
        return user;
    }

    public static ProductEntity getProductEntity() {
        return ProductEntity.of(id, createStoreEntity(), name, price, stock, description, image);

    }

    public static StoreEntity getStoreEntity() {
        return createStoreEntity();
    }

    public static ReviewPostEntity getReviewPostEntity() {
        ReviewPostEntity reviewPost = new ReviewPostEntity();
        reviewPost.setId(id);
        reviewPost.setUser(getUserEntity());
        reviewPost.setProduct(getProductEntity());
        reviewPost.setTitle(title);
        reviewPost.setContent(content);
        reviewPost.setImagePath(image);
        return reviewPost;
    }

    public static Verification getVerificationEntity() {
        String code = "abcdef";
        Verification verification = Verification.of(email, code);
        verification.setId(1);
        verification.setStartedAt(Timestamp.from(Instant.now()));
        verification.setExpiredAt(new Timestamp(System.currentTimeMillis() + 180_000));
        return verification;
    }

    public static ReviewCommentEntity getReviewCommentEntity() {
        ReviewCommentEntity reviewComment = new ReviewCommentEntity();
        reviewComment.setId(id);
        reviewComment.setReviewPost(getReviewPostEntity());
        reviewComment.setUser(getUserEntity());
        reviewComment.setContent(content);
        return reviewComment;
    }

}
