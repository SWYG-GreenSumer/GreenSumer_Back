package org.swyg.greensumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.request.ProductCreateRequest;
import org.swyg.greensumer.dto.request.ProductModifyRequest;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.dto.response.*;
import org.swyg.greensumer.service.StoreService;

@RequiredArgsConstructor
@RequestMapping("/api/stores")
@RestController
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public Response<Void> create(@RequestBody StoreCreateRequest request, Authentication authentication) {
        storeService.create(request, authentication.getName());

        return Response.success();
    }

    @PutMapping("/{storeId}")
    public Response<Void> modify(@PathVariable Long storeId, @RequestBody StoreModifyRequest request, Authentication authentication) {
        storeService.modify(storeId, request, authentication.getName());

        return Response.success();
    }

    @DeleteMapping("/{storeId}")
    public Response<Void> delete(@PathVariable Long storeId, Authentication authentication) {
        storeService.delete(storeId, authentication.getName());

        return Response.success();
    }

    @GetMapping
    public Response<Page<StoreResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(storeService.list(pageable, authentication.getName()).map(StoreResponse::fromStore));
    }

    @GetMapping("/my")
    public Response<Page<SellerStoreResponse>> mylist(Pageable pageable, Authentication authentication) {

        return Response.success(storeService.mylist(pageable, authentication.getName()).map(SellerStoreResponse::fromSellerStore));
    }

    @PostMapping("/{storeId}/products")
    public Response<ProductResponse> registerProduct(@PathVariable Long storeId, @RequestBody ProductCreateRequest request, Authentication authentication) {
        Product product = storeService.registerProduct(storeId, request, authentication.getName());

        return Response.success(ProductResponse.fromProduct(product));
    }

    @PutMapping("/{storeId}/products/{productId}")
    public Response<ProductResponse> modifyProduct(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestBody ProductModifyRequest request,
            Authentication authentication
    ) {
        Product product = storeService.modifyProduct(storeId, productId, request, authentication.getName());

        return Response.success(ProductResponse.fromProduct(product));
    }

    @DeleteMapping("/{storeId}/products/{productId}")
    public Response<Void> deleteProduct(@PathVariable Long storeId, @PathVariable Long productId, Authentication authentication) {
        storeService.deleteProduct(storeId, productId, authentication.getName());

        return Response.success();
    }

    @GetMapping("/{storeId}/products")
    public Response<Page<StoreProductResponse>> getStoreWithProductList(@PathVariable Long storeId, Pageable pageable) {
        return Response.success(storeService.getStoreWithProductList(storeId, pageable).map(StoreProductResponse::fromStoreProduct));
    }

    @GetMapping("/{storeId}/products/{productId}")
    public Response<StoreProductResponse> getProduct(@PathVariable Long storeId, @PathVariable Long productId, Authentication authentication) {
        return Response.success(StoreProductResponse.fromStoreProduct(storeService.getProduct(storeId, productId)));
    }
}
