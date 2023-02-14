package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Image;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageSearchResponse {
    private Long id;
    private String fileName;
    private byte[] image;

    public static ImageSearchResponse fromImage(Image image) {
        return new ImageSearchResponse(
                image.getId(),
                image.getOriginFilename(),
                image.getImageData()
        );
    }
}
