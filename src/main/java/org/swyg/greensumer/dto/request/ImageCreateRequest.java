package org.swyg.greensumer.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageCreateRequest {
    private MultipartFile image;
    private String type;
}
