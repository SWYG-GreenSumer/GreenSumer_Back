package org.swyg.greensumer.fixture;

import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.EventStatus;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.domain.constant.UserRole;
import org.swyg.greensumer.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Fixtures {
    private static final String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNjczMDIwMDYwLCJleHAiOjE2NzU2MTIwNjB9.USuRnODheSfeL65rpXqQOMkVLnOqCtSacrJsLNQXSNg";
    private static final long id = 1L;
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
    private static final String phone = "010-1234-1234";
    private static final String name = "name";
    private static final String modifiedName = "modified Name";
    private static final String address = "address";
    private static final String description = "description";
    private static final Double lat = 123.123123123123;
    private static final Double lng = 123.123123123123;
    private static final String hours = "hours";
    private static final String logo = "logo";
    private static final String roadname = "roadname";
    private static final String url = "http://www.naver.cion";
    private static final String AccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InRlc3QxIiwiaWF0IjoxNjc2NzkwNDgwLCJleHAiOjE2NzY3OTIyODB9.NhUIC2NSpLU1dBpoyQWjGU9O-nNfMczerYl-naPrihk";
    private static final String RefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NzY3OTA0ODAsImV4cCI6MTY3NzM5NTI4MH0.Bk5sjSRHUaM-H0A1y_OloAwvX0G0HcJOLRND_H6yws0";

    public static UserEntity userEntity(){
        return UserEntity.builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .fullname(name)
                .nickname(nickname)
                .phone(phone)
                .gender(true)
                .birth(LocalDate.now())
                .roles(getRoles())
                .build();
    }

    private static Set<UserRole> getRoles() {
        return Set.of(UserRole.ROLE_USER, UserRole.ROLE_SELLER);
    }

    public static User getUser() {
        return User.fromEntity(userEntity());
    }

    public static TokenInfo getTokenInfo() {
        return TokenInfo.builder()
                .accessToken(AccessToken)
                .refreshToken(RefreshToken)
                .build();
    }

    public static StoreEntity storeEntity() {
        return StoreEntity.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .storeType(StoreType.FOOD)
                .description(description)
                .url(url)
                .hours(hours)
                .address(addressEntity())
                .logos(Set.of(storeImageEntity()))
                .build();
    }

    public static StoreImageEntity storeImageEntity() {
        return StoreImageEntity.builder()
                .id(id)
                .originFilename("original")
                .savedFilename("savedFile")
                .userEntity(userEntity())
                .imageData("imageData".getBytes())
                .build();
    }

    private static ProductImageEntity productImageEntity() {
        return ProductImageEntity.builder()
                .id(1L)
                .originFilename("original")
                .savedFilename("savedFile")
                .userEntity(userEntity())
                .build();
    }

    public static Store store() {
        return Store.fromEntity(storeEntity());
    }

    public static AddressEntity addressEntity() {
        return AddressEntity.builder()
                .id(id)
                .address(address)
                .roadname(roadname)
                .latitude(lat)
                .longitude(lng)
                .build();
    }

    public static ProductEntity productEntity() {
        ProductEntity build = ProductEntity.builder()
                .id(1L)
                .name(name)
                .price(price)
                .description(description)
                .stock(1)
                .build();

        build.addImage(productImageEntity());
        return build;
    }

    public static StoreProductEntity storeProductEntity() {
        return StoreProductEntity.builder()
                .id(id)
                .store(storeEntity())
                .product(productEntity())
                .build();
    }

    public static StoreProduct storeProduct() {
        return StoreProduct.fromEntity(storeProductEntity());
    }

    public static ReviewPostEntity reviewPostEntity() {
        return ReviewPostEntity.of(userEntity(), title, content, "★★★★★");
    }

    public static ReviewPostWithComment reviewPostWithComment() {
        return ReviewPostWithComment.fromEntity(reviewPostEntity());
    }

    public static ReviewPost reviewPost() {
        return ReviewPost.fromEntity(reviewPostEntity());
    }

    public static ReviewComment reviewComment() {
        return ReviewComment.fromEntity(reviewCommentEntity());
    }

    public static ReviewCommentEntity reviewCommentEntity() {
        return ReviewCommentEntity.of(reviewPostEntity(), userEntity(), content);
    }

    public static EventPostWithComment eventPostWithComment() {
        return EventPostWithComment.fromEntity(eventPostEntity());
    }

    public static EventPostEntity eventPostEntity() {
        return EventPostEntity.builder()
                .user(userEntity())
                .title(title)
                .content(content)
                .started_at(LocalDateTime.now())
                .ended_at(LocalDateTime.now().plusDays(30L))
                .status(EventStatus.PROGRESSED)
                .build();
    }

    public static EventPost eventPost() {
        return EventPost.fromEntity(eventPostEntity());
    }

    public static EventCommentEntity eventCommentEntity() {
        return EventCommentEntity.builder()
                .user(userEntity())
                .eventPost(eventPostEntity())
                .content(content)
                .build();
    }

    public static EventComment eventComment() {
        return EventComment.fromEntity(eventCommentEntity());
    }

    public static byte[] image() {
        return new byte[]{};
    }

    public static InterviewEntity interviewEntity() {
        return InterviewEntity.builder()
                .id(id)
                .writer(username)
                .opinion(content)
                .storeName(name)
                .target(UserRole.ROLE_USER)
                .build();
    }

    public static Interview interview() {
        return Interview.fromInterviewEntity(interviewEntity());
    }

    public static List<Interview> interviews() {
        return List.of(interview());
    }

    public static SellerStoreEntity sellerStoreEntity() {
        return SellerStoreEntity.of(storeEntity(), userEntity());
    }

    public static List<StoreImageEntity> storeImageEntities() {
        List<StoreImageEntity> storeImageEntities = new ArrayList<>();
        storeImageEntities.add(storeImageEntity());
        return storeImageEntities;
    }

    public static List<ProductImageEntity> productImageEntities() {
        List<ProductImageEntity> productImageEntities = new ArrayList<>();
        productImageEntities.add(productImageEntity());
        return productImageEntities;
    }

}
