package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.ProductEntity;
import org.swyg.greensumer.domain.StoreEntity;
import org.swyg.greensumer.domain.UserEntity;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.Store;
import org.swyg.greensumer.dto.request.ProductCreateRequest;
import org.swyg.greensumer.dto.request.ProductModifyRequest;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ProductEntityRepository;
import org.swyg.greensumer.repository.StoreEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreEntityRepository storeEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public void create(StoreCreateRequest request, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        String storeName = request.getName();

        if (isDuplicateStoreName(storeName)) {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", storeName));
        }

        StoreEntity storeEntity = StoreEntity.of(
                userEntity,
                storeName,
                request.getDescription(),
                request.getAddress(),
                request.getHours(),
                request.getLat(),
                request.getLng(),
                request.getLogo(),
                StoreType.valueOf(request.getType())
        );

        storeEntityRepository.save(storeEntity);
    }

    public Store modify(Integer storeId, StoreModifyRequest request, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findByNameAndId(request.getName(), storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        storeEntity.setName(request.getName());
        storeEntity.setStoreType(StoreType.valueOf(request.getType()));
        storeEntity.setDescription(request.getDescription());
        storeEntity.setHours(request.getHours());
        storeEntity.setAddress(request.getAddress());
        storeEntity.setLat(request.getLat());
        storeEntity.setLng(request.getLng());
        storeEntity.setLogo(request.getLogo());

        StoreEntity updateStoreEntity = storeEntityRepository.saveAndFlush(storeEntity);
        return Store.fromEntity(updateStoreEntity);
    }

    public boolean isDuplicateStoreName(String storeName) {
        return storeEntityRepository.existsByName(storeName);
    }

    public void delete(Integer storeId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        if (!storeEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        }

        productEntityRepository.deleteAllByStore(storeEntity);
        storeEntityRepository.deleteById(storeId);
    }

    public Page<Store> list(Pageable pageable, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        return storeEntityRepository.findAll(pageable).map(Store::fromEntity);
    }

    public Page<Store> mylist(Pageable pageable, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        return storeEntityRepository.findAllByUser(userEntity, pageable).map(Store::fromEntity);
    }

    public Product registerProduct(Integer storeId, ProductCreateRequest request, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        if (!storeEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        }

        ProductEntity productEntity = productEntityRepository.save(ProductEntity.of(
                storeEntity,
                request.getName(),
                request.getPrice(),
                request.getStock(),
                request.getDescription(),
                request.getImage()
        ));

        return Product.fromEntity(productEntity);
    }

    public Product modifyProduct(Integer storeId, Integer productId, ProductModifyRequest request, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        if (!storeEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        }

        ProductEntity productEntity = productEntityRepository.findById(productId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId));
        });

        productEntity.setName(request.getName());
        productEntity.setDescription(request.getDescription());
        productEntity.setPrice(request.getPrice());
        productEntity.setStock(request.getStock());
        productEntity.setImage(request.getImage());

        ProductEntity modifiedProduct = productEntityRepository.saveAndFlush(productEntity);
        return Product.fromEntity(modifiedProduct);
    }

    public void deleteProduct(Integer storeId, Integer productId, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        if (!storeEntity.getUser().getUsername().equals(username)) {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        }

        productEntityRepository.deleteById(productId);
    }

    public Page<Product> getProductList(Integer storeId, Pageable pageable) {
        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        return productEntityRepository.findAllByStore(storeEntity, pageable).map(Product::fromEntity);
    }

    public Product getProduct(Integer storeId, Integer productId) {
        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

         ProductEntity productEntity = productEntityRepository.findByStoreAndId(storeEntity, productId).orElseThrow(() -> {
             throw new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded on %s", productId, storeEntity.getName()));
         });

         return Product.fromEntity(productEntity);
    }
}
