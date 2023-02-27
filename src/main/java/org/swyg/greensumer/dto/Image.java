package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.*;

import java.time.LocalDateTime;

@Getter
@Builder
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
        return Image.builder()
                .id(entity.getId())
                .username(entity.getUserEntity().getUsername())
                .originFilename(entity.getOriginFilename())
                .savedFilename(entity.getSavedFilename())
                .build();
    }

    public static Image fromStoreImageEntity(StoreImageEntity entity) {
        return Image.builder()
                .id(entity.getId())
                .username(entity.getUserEntity().getUsername())
                .originFilename(entity.getOriginFilename())
                .savedFilename(entity.getSavedFilename())
                .build();
    }

    public static Image fromProductImageEntity(ProductImageEntity entity) {
        return Image.builder()
                .id(entity.getId())
                .username(entity.getUserEntity().getUsername())
                .originFilename(entity.getOriginFilename())
                .savedFilename(entity.getSavedFilename())
                .build();
    }

    public static Image fromReviewImageEntity(ReviewImageEntity entity) {
        return Image.builder()
                .id(entity.getId())
                .username(entity.getUserEntity().getUsername())
                .originFilename(entity.getOriginFilename())
                .savedFilename(entity.getSavedFilename())
                .build();
    }

    public static Image fromEventImageEntity(EventImageEntity entity) {
        return Image.builder()
                .id(entity.getId())
                .username(entity.getUserEntity().getUsername())
                .originFilename(entity.getOriginFilename())
                .savedFilename(entity.getSavedFilename())
                .build();
    }
}
