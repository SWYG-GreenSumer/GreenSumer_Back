package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.Image;
import org.swyg.greensumer.dto.request.ImageCreateRequest;
import org.swyg.greensumer.dto.request.ImageModifyRequest;
import org.swyg.greensumer.dto.request.ImagesCreateRequest;
import org.swyg.greensumer.dto.response.ImageCreateResponse;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.service.ImageService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/image")
    public Response<ImageCreateResponse> saveImage(@ModelAttribute ImageCreateRequest request, Authentication authentication) throws IOException {

        Image image = imageService.saveImage(request.getImage(), request.getType(), authentication.getName());

        return Response.success(ImageCreateResponse.fromImage(image));
    }

    @PostMapping(value = "/images")
    public Response<List<ImageCreateResponse>> saveImages(@ModelAttribute ImagesCreateRequest request, Authentication authentication) throws IOException {
        List<Image> images = imageService.saveImages(request, authentication.getName());

        return Response.success(images.stream().map(ImageCreateResponse::fromImage).collect(Collectors.toList()));
    }

    @GetMapping(value = "/image/{imageId}")
    public ResponseEntity<?> searchImage(@PathVariable Integer imageId, Authentication authentication) throws IOException {
        Image image = imageService.searchImage(imageId, authentication.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image.getImageData());
    }

    @PutMapping(value = "/image/{imageId}")
    public ResponseEntity<?> modifyImage(@PathVariable Integer imageId, @ModelAttribute ImageModifyRequest request, Authentication authentication) throws IOException {
        Image image = imageService.modifyImage(imageId, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image.getImageData());
    }

    @DeleteMapping(value = "/image/{imageId}")
    public Response<Void> removeImage(@PathVariable Integer imageId, Authentication authentication) throws IOException {
        imageService.removeImage(imageId, authentication.getName());
        return Response.success();
    }

}
