package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.StoreService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.RequestFixture.*;
import static org.swyg.greensumer.fixture.Fixtures.*;

@DisplayName("View 컨트롤러 - 제로웨이스트 샵")
@ActiveProfiles("test")
@MockBean({JpaMetamodelMappingContext.class})
@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private StoreService storeService;

    StoreControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][POST] 가게 생성(판매자인 경우) - 정상호출")
    @WithMockUser(username = "seller", roles = {"USER, SELLER"})
    @Test
    void givenStoreInfo_whenRequestingCreateStore_thenNothing() throws Exception {
        // Given
        willDoNothing().given(storeService).create(any(StoreCreateRequest.class), anyString());

        // When & Then
        mvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 가게 생성(관리자인 경우) - 정상호출")
    @WithMockUser(username = "admin", roles = {"USER, SELLER, ADMIN"})
    @Test
    void givenStoreInfo_whenRequestingCreateStoreFromUser_thenNothing() throws Exception {
        // Given
        willDoNothing().given(storeService).create(any(StoreCreateRequest.class), anyString());

        // When & Then
        mvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 가게 생성 - 회원가입하지 않은 유저인 경우")
    @WithAnonymousUser
    @Test
    void givenStoreInfo_whenRequestingCreateStoreFromAdmin_thenThrowIsUnAuthorized() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(storeService).create(any(StoreCreateRequest.class), anyString());

        // When & Then
        mvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 가게 생성 - 이미 존재하는 가게명인 경우")
    @WithMockUser
    @Test
    void givenDuplicateStoreInfo_whenRequestingCreateStore_thenThrowConflictError() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_STORE_NAME)).given(storeService).create(any(StoreCreateRequest.class), anyString());

        // When & Then
        mvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreInfo_whenRequestingModifyStore_thenReturnReviewPost() throws Exception {
        // Given
        willDoNothing().given(storeService).modify(any(), any(StoreModifyRequest.class), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreInfo_whenRequestingModifyStoreNotLogin_thenThrowUnauthroizedException() throws Exception {
        // Given
        willDoNothing().given(storeService).modify(any(), any(StoreModifyRequest.class), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 본인에 가게가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenStoreInfo_whenRequestingModifyOtherStore_thenThrowInvalid_Permission_Error() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(storeService).modify(any(), any(StoreModifyRequest.class), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 존재하는 가게가 아닌 경우")
    @WithMockUser
    @Test
    void givenStoreInfo_whenRequestingDeleteStore_thenThrowNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).given(storeService).modify(anyLong(), any(StoreModifyRequest.class), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(StoreModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingDeleteStore_thenReturnReviewPost() throws Exception {
        // Given
        willDoNothing().given(storeService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingDeleteStoreNotLogin_thenThrowUnauthroizedException() throws Exception {
        // Given
        willDoNothing().given(storeService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 본인에 가게가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingDeleteOtherStore_thenThrowInvalid_Permission_Error() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(storeService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 존재하는 가게가 아닌 경우")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingDeleteStore_thenThrowNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).given(storeService).delete(any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][GET] 가게 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingStoreList_thenReturnStoresList() throws Exception {
        // Given
        willReturn(Page.empty()).given(storeService).list(any(Pageable.class), anyString());

        // When & Then
        mvc.perform(get("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 자신이 운영하는 가게 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingMyStoreList_thenReturnStoresList() throws Exception {
        // Given
        willReturn(Page.empty()).given(storeService).mylist(any(Pageable.class), anyString());

        // When & Then
        mvc.perform(get("/api/stores/my")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 자신이 운영하는 가게 리스트 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingMyStoreListNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        given(storeService.mylist(any(Pageable.class), any())).willReturn(Page.empty());

        // When & Then
        mvc.perform(get("/api/stores/my")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 정상호출")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingRegisterProduct_thenReturnProductResponse() throws Exception {
        // Given
        willReturn(mock(Product.class)).given(storeService).registerProduct(any(), any(), any());

        // When & Then
        mvc.perform(post("/api/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 상품 등록 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingRegisterProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        willReturn(mock(Product.class)).given(storeService).registerProduct(any(), any(), any());

        // When & Then
        mvc.perform(post("/api/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingRegisterProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(storeService).registerProduct(any(), any(), any());

        // When & Then
        mvc.perform(post("/api/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingRegisterProduct_thenThrowStoreNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).given(storeService).registerProduct(any(), any(), any());

        // When & Then
        mvc.perform(post("/api/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductCreateRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 상품 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyProduct_thenReturnProductResponse() throws Exception {
        // Given
        willReturn(mock(Product.class)).given(storeService).modifyProduct(any(), any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 상품 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingModifyProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        willReturn(mock(Product.class)).given(storeService).modifyProduct(any(), any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 상품 수정 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingModifyProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(storeService).modifyProduct(any(), any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 상품 수정 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyProduct_thenThrowStoreNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).given(storeService).modifyProduct(any(), any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 상품 수정 - 상품이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyNotExistProduct_thenThrowProductNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND)).given(storeService).modifyProduct(any(), any(), any(), anyString());

        // When & Then
        mvc.perform(put("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(ProductModifyRequest()))
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 정상 호출")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteProduct_thenReturnNothing() throws Exception {
        // Given
        willDoNothing().given(storeService).deleteProduct(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProductId_whenRequestingDeleteProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        willDoNothing().given(storeService).deleteProduct(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProductId_whenRequestingDeleteProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).given(storeService).deleteProduct(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteProduct_thenThrowStoreNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).given(storeService).deleteProduct(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 상품이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteNotExistProduct_thenThrowProductNotFoundException() throws Exception {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND_ON_STORE)).given(storeService).deleteProduct(any(), any(), anyString());

        // When & Then
        mvc.perform(delete("/api/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                )
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][GET] 상품 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingProductListInStore_thenReturnProductListInStore() throws Exception {
        // Given
        willReturn(Page.empty()).given(storeService).getStoreWithProductList(any(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/stores/1/products")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 상품 리스트 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingProductListInStoreNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        willReturn(Page.empty()).given(storeService).getStoreWithProductList(any(), any(Pageable.class));

        // When & Then
        mvc.perform(get("/api/stores/1/products")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 상품 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreIdAndProductId_whenRequestingProductInStore_thenReturnProduct() throws Exception {
        // Given
        willReturn(storeProduct()).given(storeService).getProduct(any(), any());

        // When & Then
        mvc.perform(get("/api/stores/1/products/1")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 상품 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreIdAndProductId_whenRequestingProductInStoreNotLogin_thenThrowUnauthorizedException() throws Exception {
        // Given
        willReturn(storeProduct()).given(storeService).getProduct(any(), any());

        // When & Then
        mvc.perform(get("/api/stores/1/products/1")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

}
