package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.dto.Product;
import org.swyg.greensumer.dto.SellerStore;
import org.swyg.greensumer.dto.Store;
import org.swyg.greensumer.dto.request.ProductCreateRequest;
import org.swyg.greensumer.dto.request.ProductModifyRequest;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.ProductEntityRepository;
import org.swyg.greensumer.repository.SellerStoreEntityRepository;
import org.swyg.greensumer.repository.StoreEntityRepository;
import org.swyg.greensumer.repository.UserEntityRepository;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreEntityRepository storeEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final SellerStoreEntityRepository sellerStoreEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final AddressService addressService;

    @Transactional
    public Store create(StoreCreateRequest request) {
        // 1. 주소 확인(없는 경우 주소 정보를 등록하여 반환)
        AddressEntity addressEntity = addressService.findAddressEntity(request.getAddress(), request.getRoadname(), request.getLat(), request.getLat());

        // 2. 가게명과 주소가 동일한 가게가 존재할 경우 중복 에러 발생
        storeEntityRepository.findByNameAndAddress(request.getName(), addressEntity).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_USERNAME, String.format("%s is duplicated", request.getName()));
        });

        // 3. 가게 객체 생성
        StoreEntity storeEntity = StoreEntity.of(
                request.getName(),
                request.getDescription(),
                addressEntity,
                request.getHours(),
                request.getLogo(),
                StoreType.valueOf(request.getType())
        );

        // 4. 가게 정보 반환
        return Store.fromEntity(storeEntityRepository.save(storeEntity));
    }

    public Store modify(Integer storeId, StoreModifyRequest request, String username) {
        // 1. 회원가입된 유저정보 확인
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        // 2. 가게 조회
        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        // 3. 주소 조회(없는 경우 Address Not Found Exception 발생)
        AddressEntity addressEntity = addressService.searchAddress(storeEntity.getAddress().getId());

        // 4. 주소에 변화가 있다면 주소 정보 업데이트
        Double latitude = Double.valueOf(request.getLat());
        Double longitude = Double.valueOf(request.getLng());

        AddressEntity updatedAddress = addressService.updateAddress(addressEntity, AddressEntity.of(request.getAddress(), request.getRoadname(), latitude, longitude));

        // 5. 가게 업데이트
        storeEntity.setStoreType(StoreType.valueOf(request.getType()));
        storeEntity.setDescription(request.getDescription());
        storeEntity.setHours(request.getHours());
        storeEntity.setLogo(request.getLogo());
        storeEntity.setAddress(updatedAddress);
        StoreEntity updateStoreEntity = storeEntityRepository.saveAndFlush(storeEntity);

        return Store.fromEntity(updateStoreEntity);
    }

    public void delete(Integer storeId, String username) {
        // 1. 회원가입된 유저인지 여부 판단
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        // 2. 존재하는 가게인지 여부 판단
        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        // 3. 해당 가게에 담당자인지 판단
        SellerStoreEntity sellerStoreEntity = sellerStoreEntityRepository.findBySeller_IdAndStore_Id(userEntity.getId(), storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        });

        // 4. 가게에 연관된 관리자 정보 삭제
        sellerStoreEntityRepository.deleteAllByStore_Id(storeId);

        // 5. 가게 삭제
        storeEntityRepository.deleteById(storeId);
    }

    public Page<Store> list(Pageable pageable, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        return storeEntityRepository.findAll(pageable).map(Store::fromEntity);
    }

    public Page<SellerStore> mylist(Pageable pageable, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        return sellerStoreEntityRepository.findAllBySeller_Id(userEntity.getId(), pageable).map(SellerStore::fromEntity);
    }

    public Product registerProduct(Integer storeId, ProductCreateRequest request, String username) {
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", username));
        });

        StoreEntity storeEntity = storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });

        // 가게에 담당자인지 확인
        sellerStoreEntityRepository.findBySeller_IdAndStore_Id(userEntity.getId(), storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        });

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

        sellerStoreEntityRepository.findBySeller_IdAndStore_Id(userEntity.getId(), storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        });

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

        sellerStoreEntityRepository.findBySeller_IdAndStore_Id(userEntity.getId(), storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", username, storeId));
        });

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

    public StoreEntity getStoreEntity(AddressEntity addressEntity) {
        return storeEntityRepository.findByAddress_Id(addressEntity.getId()).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded by address", addressEntity.getId()));
        });
    }
}
