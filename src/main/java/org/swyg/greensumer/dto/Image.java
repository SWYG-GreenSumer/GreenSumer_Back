package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ImageEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private Long id;
    private String username;
    private String originFilename;
    private String savedFilename;
    private byte[] imageData;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static Image fromEntity(ImageEntity entity) {
        return new Image(
                entity.getId(),
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
