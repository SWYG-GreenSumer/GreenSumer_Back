package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.domain.constant.ImageType;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private Long id;
    private ImageType imageType;
    private String username;
    private String originFilename;
    private String savedFilename;
    private byte[] imageData;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Image fromEntity(ImageEntity entity) {
        return new Image(
                entity.getId(),
                entity.getImageType(),
                entity.getUserEntity().getUsername(),
                entity.getOriginFilename(),
                entity.getSavedFilename(),
                entity.getImageData(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}