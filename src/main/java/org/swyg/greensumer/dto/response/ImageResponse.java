package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Image;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
    private Long id;
    private String username;
    private String originFilename;
    private String savedFilename;
    private byte[] imageData;

    public static ImageResponse fromImage(Image image) {
        return ImageResponse.builder()
                .id(image.getId())
                .username(image.getUsername())
                .originFilename(image.getOriginFilename())
                .imageData(image.getImageData())
                .build();
    }
}
