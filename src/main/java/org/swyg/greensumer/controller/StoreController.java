package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.Store;
import org.swyg.greensumer.dto.request.ProductCreateRequest;
import org.swyg.greensumer.dto.request.ProductModifyRequest;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.dto.response.ProductResponse;
import org.swyg.greensumer.dto.response.Response;
import org.swyg.greensumer.dto.response.StoreResponse;
import org.swyg.greensumer.service.StoreService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@RestController
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public Response<Void> create(@RequestBody StoreCreateRequest request, Authentication authentication) {
        storeService.create(request, authentication.getName());

        return Response.success();
    }

    @PutMapping("/{storeId}")
    public Response<StoreResponse> modify(@PathVariable Integer storeId, @RequestBody StoreModifyRequest request, Authentication authentication) {
        Store store = storeService.modify(storeId, request, authentication.getName());

        return Response.success(StoreResponse.fromStore(store));
    }

    @DeleteMapping("/{storeId}")
    public Response<Void> delete(@PathVariable Integer storeId, Authentication authentication) {
        storeService.delete(storeId, authentication.getName());

        return Response.success();
    }

    @GetMapping
    public Response<Page<StoreResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(storeService.list(pageable, authentication.getName()).map(StoreResponse::fromStore));
    }

    @GetMapping("/my")
    public Response<Page<StoreResponse>> mylist(Pageable pageable, Authentication authentication) {
        return Response.success(storeService.mylist(pageable, authentication.getName()).map(StoreResponse::fromStore));
    }

    @PostMapping("/{storeId}/products")
    public Response<ProductResponse> registerProduct(@PathVariable Integer storeId, @RequestBody ProductCreateRequest request, Authentication authentication) {
        Product product = storeService.registerProduct(storeId, request, authentication.getName());

        return Response.success(ProductResponse.fromProduct(product));
    }

    @PutMapping("/{storeId}/products/{productId}")
    public Response<ProductResponse> modifyProduct(
            @PathVariable Integer storeId,
            @PathVariable Integer productId,
            @RequestBody ProductModifyRequest request,
            Authentication authentication
    ) {
        Product product = storeService.modifyProduct(storeId, productId, request, authentication.getName());

        return Response.success(ProductResponse.fromProduct(product));
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    public Response<Void> deleteProduct(@PathVariable Integer storeId, @PathVariable Integer productId, Authentication authentication) {
        storeService.deleteProduct(storeId, productId, authentication.getName());

        return Response.success();
    }

    @GetMapping("/{storeId}/products")
    public Response<Page<ProductResponse>> getProductList(@PathVariable Integer storeId, Authentication authentication, Pageable pageable
    ) {
        return Response.success(storeService.getProductList(storeId, pageable).map(ProductResponse::fromProduct));
    }

    @GetMapping("/{storeId}/products/{productId}")
    public Response<ProductResponse> getProduct(@PathVariable Integer storeId, @PathVariable Integer productId, Authentication authentication) {
        return Response.success(ProductResponse.fromProduct(storeService.getProduct(storeId, productId)));
    }
}
