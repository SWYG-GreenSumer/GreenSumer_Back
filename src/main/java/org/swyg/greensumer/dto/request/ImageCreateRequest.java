package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageCreateRequest {
    private MultipartFile image;
    private String type;            // REVIEW, EVENT, PRODUCT, STORE
}
