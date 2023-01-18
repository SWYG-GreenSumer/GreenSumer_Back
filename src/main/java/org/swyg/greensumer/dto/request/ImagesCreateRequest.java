package org.swyg.greensumer.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ImagesCreateRequest {
    private List<MultipartFile> images;
    private String type;
}
