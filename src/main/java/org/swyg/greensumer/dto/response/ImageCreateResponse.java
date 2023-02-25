package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Image;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCreateResponse {
    private Long id;
    private String originFilename;
    private String savedFilename;
    private byte[] imageData;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static ImageCreateResponse fromImage(Image image){
        return new ImageCreateResponse(
                image.getId(),
                image.getOriginFilename(),
                image.getSavedFilename(),
                image.getImageData(),
                image.getRegisteredAt(),
                image.getUpdatedAt(),
                image.getDeletedAt()
        );
    }
}
