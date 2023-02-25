package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.ImageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.image;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 이미지 등록")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class})
@WebMvcTest(ImageController.class)
class ImageControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private ImageService imageService;

    public ImageControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][POST] 이미지 리스트 등록 - 정상호출")
    @WithMockUser
    @Test
    void givenImages_whenRequestingUploadImages_thenNothing() throws Exception {
        // Given
        willDoNothing().given(imageService).saveImages(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImagesCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 이미지 리스트 등록 - 이미지 개수가 5개를 초과한 경우")
    @WithMockUser
    @Test
    void givenImages_whenRequestingUploadImages_thenThrowConflictExceptionBecauseOfCount() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.OVER_IMAGE_COUNT)).given(imageService).saveImages(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImagesCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][POST] 이미지 리스트 등록 - 용량이 초과한 경우")
    @WithMockUser
    @Test
    void givenImages_whenRequestingUploadImages_thenThrowConflictExceptionBecauseOfSize() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.IMAGE_SIZE_OVER)).given(imageService).saveImages(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImagesCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][POST] 이미지 리스트 등록 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenImages_whenRequestingUploadImages_thenThrowUnAuthorizeException() throws Exception {
        // Given
        willDoNothing().given(imageService).saveImages(any(), any(), anyString());

        // When & Then
        mvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImagesCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 이미지 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenImageId_whenRequestingImage_thenReturnImage() throws Exception {
        // Given
        willReturn(image()).given(imageService).searchImage(any(), anyString());

        // When & Then
        mvc.perform(get("/api/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", ImageType.PRODUCT.toString())
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 이미지 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenImageId_whenRequestingModifyImage_thenReturnImage() throws Exception {
        // Given
        willDoNothing().given(imageService).modifyImage(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImageModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 이미지 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenImageId_whenRequestingModifyImage_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND)).given(imageService).modifyImage(any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ImageModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 이미지 제거 - 정상호출")
    @WithMockUser
    @Test
    void givenImageId_whenRequestingDeleteImage_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(imageService).removeImage(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", ImageType.PRODUCT.toString())
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 이미지 제거 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenImageId_whenRequestingDeleteImage_thenThrowUnAuthorizedException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.IMAGE_NOT_FOUND)).given(imageService).removeImage(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("type", ImageType.PRODUCT.toString())
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

}