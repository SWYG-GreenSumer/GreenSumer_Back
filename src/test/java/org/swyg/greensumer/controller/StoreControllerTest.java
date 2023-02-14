package org.swyg.greensumer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.service.StoreService;
import org.swyg.greensumer.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.swyg.greensumer.fixture.Fixtures.getProduct;
import static org.swyg.greensumer.fixture.Fixtures.getStore;
import static org.swyg.greensumer.fixture.RequestFixture.*;

@DisplayName("View 컨트롤러 - 제로웨이스트 샵")
@ActiveProfiles("test")
@WebMvcTest(StoreController.class)
class StoreControllerTest {

    private final MockMvc mvc;
    private final ObjectMapper objectMapper;

    @MockBean private StoreService storeService;
    @MockBean private UserService userService;

    StoreControllerTest(
            @Autowired MockMvc mvc,
            @Autowired ObjectMapper objectMapper
    ) {
        this.mvc = mvc;
        this.objectMapper = objectMapper;
    }

    @DisplayName("[view][POST] 가게 생성 - 정상호출")
    @Test
    void givenStoreInfo_whenRequestingCreateStore_thenNothing() throws Exception {
        when(storeService.create(any())).thenReturn(getStore());

        mvc.perform(post("/api/v1/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getStoreCreateRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 가게 생성 - 회원가입하지 않은 유저인 경우")
    @WithMockUser
    @Test
    void givenStoreInfo_whenRequestingCreateStore_thenThrowUserNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).when(storeService).create(any());

        mvc.perform(post("/api/v1/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getStoreCreateRequest()))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][POST] 가게 생성 - 이미 존재하는 가게명인 경우")
    @WithMockUser
    @Test
    void givenDuplicateStoreInfo_whenRequestingCreateStore_thenThrowConflictError() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_STORE_NAME)).when(storeService).create(any());

        mvc.perform(post("/api/v1/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getStoreCreateRequest()))
                ).andDo(print())
                .andExpect(status().isConflict());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreInfo_whenRequestingModifyStore_thenReturnReviewPost() throws Exception {
        when(storeService.modify(any(), any(), any())).thenReturn(getStore());

        mvc.perform(put("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getStoreModifyRequest()))
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreInfo_whenRequestingModifyStoreNotLogin_thenThrowUnauthroizedException() throws Exception {
        when(storeService.modify(any(), any(), any())).thenReturn(getStore());

        mvc.perform(put("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 본인에 가게가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenStoreInfo_whenRequestingModifyOtherStore_thenThrowInvalid_Permission_Error() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(storeService).modify(any(), any(), any());

        mvc.perform(put("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 가게 정보 수정 - 존재하는 가게가 아닌 경우")
    @WithMockUser
    @Test
    void givenStoreInfo_whenRequestingDeleteStore_thenThrowNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).when(storeService).modify(any(), any(), any());

        mvc.perform(put("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingDeleteStore_thenReturnReviewPost() throws Exception {
        doNothing().when(storeService).delete(any(), any());

        mvc.perform(delete("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingDeleteStoreNotLogin_thenThrowUnauthroizedException() throws Exception {
        doNothing().when(storeService).delete(any(), any());

        mvc.perform(delete("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 본인에 가게가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingDeleteOtherStore_thenThrowInvalid_Permission_Error() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(storeService).delete(any(), any());

        mvc.perform(delete("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 가게 정보 삭제 - 존재하는 가게가 아닌 경우")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingDeleteStore_thenThrowNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).when(storeService).delete(any(), any());

        mvc.perform(delete("/api/v1/stores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getReviewPostModifyRequest()))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][GET] 가게 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingStoreList_thenReturnStoresList() throws Exception {
        given(storeService.list(any(Pageable.class), any())).willReturn(Page.empty());

        mvc.perform(get("/api/v1/stores"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 가게 리스트 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingStoreListNotLogin_thenThrowUnauthorizedException() throws Exception {
        given(storeService.list(any(Pageable.class), any())).willReturn(Page.empty());

        mvc.perform(get("/api/v1/stores"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 자신이 운영하는 가게 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenNothing_whenRequestingMyStoreList_thenReturnStoresList() throws Exception {
        given(storeService.mylist(any(Pageable.class), any())).willReturn(Page.empty());

        mvc.perform(get("/api/v1/stores/my"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 자신이 운영하는 가게 리스트 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenNothing_whenRequestingMyStoreListNotLogin_thenThrowUnauthorizedException() throws Exception {
        given(storeService.mylist(any(Pageable.class), any())).willReturn(Page.empty());

        mvc.perform(get("/api/v1/stores/my"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 정상호출")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingRegisterProduct_thenReturnProductResponse() throws Exception {
        given(storeService.registerProduct(any(), any(), any())).willReturn(getProduct());

        mvc.perform(post("/api/v1/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductCreateRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][POST] 상품 등록 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingRegisterProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        given(storeService.registerProduct(any(), any(), any())).willReturn(getProduct());

        mvc.perform(post("/api/v1/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductCreateRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingRegisterProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(storeService).registerProduct(any(), any(), any());

        mvc.perform(post("/api/v1/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductCreateRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][POST] 상품 등록 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingRegisterProduct_thenThrowStoreNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).when(storeService).registerProduct(any(), any(), any());

        mvc.perform(post("/api/v1/stores/1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductCreateRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 상품 수정 - 정상호출")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyProduct_thenReturnProductResponse() throws Exception {
        given(storeService.modifyProduct(any(), any(), any(), any())).willReturn(getProduct());

        mvc.perform(put("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductModifyRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][PUT] 상품 수정 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingModifyProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        given(storeService.modifyProduct(any(), any(), any(), any())).willReturn(getProduct());

        mvc.perform(put("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductModifyRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 상품 수정 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProduct_whenRequestingModifyProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(storeService).modifyProduct(any(), any(), any(), any());

        mvc.perform(put("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductModifyRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][PUT] 상품 수정 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyProduct_thenThrowStoreNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).when(storeService).modifyProduct(any(), any(), any(), any());

        mvc.perform(put("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductModifyRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][PUT] 상품 수정 - 상품이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProduct_whenRequestingModifyNotExistProduct_thenThrowProductNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND)).when(storeService).modifyProduct(any(), any(), any(), any());

        mvc.perform(put("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getProductModifyRequest()))
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 정상 호출")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteProduct_thenReturnNothing() throws Exception {
        mvc.perform(delete("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenProductId_whenRequestingDeleteProductNotLogin_thenThrowUnauthorizedException() throws Exception {
        mvc.perform(delete("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 가게 관리자가 아닌 경우")
    @WithAnonymousUser
    @Test
    void givenProductId_whenRequestingDeleteProductFromOther_thenThrowInvalidPermissionException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION)).when(storeService).deleteProduct(any(), any(), any());

        mvc.perform(delete("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 가게가 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteProduct_thenThrowStoreNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND)).when(storeService).deleteProduct(any(), any(), any());

        mvc.perform(delete("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][DELETE] 상품 삭제 - 상품이 존재하지 않는 경우")
    @WithMockUser
    @Test
    void givenProductId_whenRequestingDeleteNotExistProduct_thenThrowProductNotFoundException() throws Exception {
        doThrow(new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND)).when(storeService).deleteProduct(any(), any(), any());

        mvc.perform(delete("/api/v1/stores/1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("[view][GET] 상품 리스트 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreId_whenRequestingProductListInStore_thenReturnProductListInStore() throws Exception {
        given(storeService.getStoreWithProductList(any(), any(Pageable.class))).willReturn(Page.empty());

        mvc.perform(get("/api/v1/stores/1/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 상품 리스트 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreId_whenRequestingProductListInStoreNotLogin_thenThrowUnauthorizedException() throws Exception {

        mvc.perform(get("/api/v1/stores/1/products"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("[view][GET] 상품 조회 - 정상호출")
    @WithMockUser
    @Test
    void givenStoreIdAndProductId_whenRequestingProductInStore_thenReturnProduct() throws Exception {
        given(storeService.getProduct(any(), any())).willReturn(getProduct());

        mvc.perform(get("/api/v1/stores/1/products/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("[view][GET] 상품 조회 - 로그인하지 않은 경우")
    @WithAnonymousUser
    @Test
    void givenStoreIdAndProductId_whenRequestingProductInStoreNotLogin_thenThrowUnauthorizedException() throws Exception {

        mvc.perform(get("/api/v1/stores/1/products/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
