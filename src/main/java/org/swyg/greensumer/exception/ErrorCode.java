package org.swyg.greensumer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "Username is duplicated"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "Password is invalid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token is invalid"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not founded"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Permission is invalid"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    ALREADY_LIKED(HttpStatus.CONFLICT, "User already liked the post"),
    ALARM_CONNECT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Connecting alarm occurs error"),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Sending Mail occurs error"),
    MAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Mail not founded"),
    INVALID_VERIFICATION_CODE(HttpStatus.UNAUTHORIZED, "Code is invalid"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not founded"),
    DUPLICATED_STORE_NAME(HttpStatus.CONFLICT, "Name of Store is duplicated"),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "store is invalid"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not founded"),
    SAME_AS_PREVIOUS_PASSWORD(HttpStatus.CONFLICT, "Same as Previous password"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "Address not founded"),
    IMAGE_SAVE_FAIL(HttpStatus.CONFLICT, "Image save failed"),
    IMAGE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "Image not founded"),
    PRODUCT_NOT_FOUND_ON_STORE(HttpStatus.NOT_FOUND, "Product not registered in the store"),
    IMAGE_IS_NULL(HttpStatus.NO_CONTENT, "Image is null"),
    INVALID_REFRESH_TOKEN(HttpStatus.CONFLICT, "Refresh Token is invalid"),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Event not founded"),
    OVER_IMAGE_COUNT(HttpStatus.CONFLICT, "Image count is over"),
    INTERVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Interview not founded"),
    IMAGE_SIZE_OVER(HttpStatus.CONFLICT, "Image size is Over"),
    ;

    private HttpStatus status;
    private String message;
}
