package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.dto.Image;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCreateResponse {

    private Integer id;
    private ImageType imageType;
    private String originFilename;
    private String savedFilename;
    private byte[] imageData;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static ImageCreateResponse fromImage(Image image){
        return new ImageCreateResponse(
                image.getId(),
                image.getImageType(),
                image.getOriginFilename(),
                image.getSavedFilename(),
                image.getImageData(),
                image.getRegisteredAt(),
                image.getUpdatedAt(),
                image.getDeletedAt()
        );
    }
}
