package org.swyg.greensumer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GreenSumerBackApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;

    public GreenSumerBackApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }


    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }

        return String.format("%s. %s", errorCode.getMessage(), message);
    }

}
