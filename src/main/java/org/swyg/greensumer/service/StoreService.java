package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.ImageType;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.dto.*;
import org.swyg.greensumer.dto.request.ProductCreateRequest;
import org.swyg.greensumer.dto.request.ProductModifyRequest;
import org.swyg.greensumer.dto.request.StoreCreateRequest;
import org.swyg.greensumer.dto.request.StoreModifyRequest;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.store.ProductEntityRepository;
import org.swyg.greensumer.repository.store.SellerStoreEntityRepository;
import org.swyg.greensumer.repository.store.StoreEntityRepository;
import org.swyg.greensumer.repository.store.StoreProductEntityRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreEntityRepository storeEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final SellerStoreEntityRepository sellerStoreEntityRepository;
    private final AddressService addressService;
    private final StoreProductEntityRepository storeProductEntityRepository;
    private final ImageService imageService;
    private final UserEntityRepositoryService userEntityRepositoryService;

    @Transactional
    public Store create(StoreCreateRequest request) {
        // 1. 주소 확인(없는 경우 주소 정보를 등록하여 반환)
        AddressEntity addressEntity = addressService.findAddressEntity(request.getAddress(), request.getRoadname(), request.getLat(), request.getLat());

        // 2. 가게명과 주소가 동일한 가게가 존재할 경우 중복 에러 발생
        storeEntityRepository.findByNameAndAddress(request.getName(), addressEntity).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_STORE_NAME, String.format("%s is duplicated", request.getName()));
        });

        // 3. 가게 객체 생성
        StoreEntity storeEntity = StoreEntity.of(
                request.getName(),
                request.getDescription(),
                StoreType.valueOf(request.getType()),
                addressEntity,
                request.getHours()
        );

        if (request.getImages().size() > 0) {
            storeEntity.addImages(imageService.searchImages(request.getImages(), ImageType.STORE).stream().map(StoreImageEntity::fromImageEntity).collect(Collectors.toList()));
        }

        // 4. 가게 정보 반환
        return Store.fromEntity(storeEntityRepository.save(storeEntity));
    }

    @Transactional
    public Store modify(Long storeId, StoreModifyRequest request, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);

        isStoreManager(user.getId(), storeId);

        storeEntity.updateStore(request.getType(), request.getDescription(), request.getHours());
        storeEntity.updateAddress(addressService.updateAddress(storeEntity.getAddress().getId(), request.getAddress(), request.getRoadname(), request.getLat(), request.getLng()));

        if (request.getImages().size() > 0) {
            storeEntity.addImages(imageService.searchImages(request.getImages(), ImageType.STORE).stream().map(StoreImageEntity::fromImageEntity).collect(Collectors.toList()));
        }

        return Store.fromEntity(storeEntity);
    }

    public void delete(Long storeId, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);

        sellerStoreEntityRepository.deleteAllByStore_Id(storeId);
        storeEntityRepository.deleteById(storeId);
    }

    public Page<Store> list(Pageable pageable, String username) {
        userEntityRepositoryService.loadUserByUsername(username);

        return storeEntityRepository.findAll(pageable).map(Store::fromEntity);
    }

    public Page<SellerStore> mylist(Pageable pageable, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);

        return sellerStoreEntityRepository.findAllBySeller_Id(user.getId(), pageable).map(SellerStore::fromEntity);
    }

    @Transactional
    public Product registerProduct(Long storeId, ProductCreateRequest request, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        ProductEntity productEntity = ProductEntity.of(request.getName(), request.getPrice(), request.getStock(), request.getDescription());

        if (request.getImages().size() != 0) {
            productEntity.addImages(imageService.searchImages(request.getImages(), ImageType.PRODUCT).stream().map(ProductImageEntity::fromImageEntity).collect(Collectors.toList()));
        }

        ProductEntity savedEntity = productEntityRepository.save(productEntity);

        StoreProductEntity storeProductEntity = storeProductEntityRepository.save(StoreProductEntity.of(storeEntity, savedEntity));
        storeEntity.addStoreProduct(storeProductEntity);
        savedEntity.addStoreProduct(storeProductEntity);

        return Product.fromEntity(savedEntity);
    }

    @Transactional
    public Product modifyProduct(Long storeId, Long productId, ProductModifyRequest request, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        ProductEntity productEntity = getProductEntityOrException(productId);

        productEntity.updateProductInfo(request.getName(), request.getDescription(), request.getPrice(), request.getStock());

        if (request.getImages().size() > 0)
            productEntity.addImages(imageService.searchImages(request.getImages(), ImageType.PRODUCT).stream().map(ProductImageEntity::fromImageEntity).collect(Collectors.toList()));

        return Product.fromEntity(productEntity);
    }

    @Transactional
    public void deleteProduct(Long storeId, Long productId, String username) {
        User user = userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        ProductEntity productEntity = getProductEntityOrException(productId);
        StoreProductEntity storeProductEntity = getStoreProductOrException(storeId, productId);

        productEntity.clearImages();
        storeEntity.removeStoreProduct(storeProductEntity);
        productEntityRepository.delete(productEntity);
    }

    public Page<StoreProduct> getStoreWithProductList(Long storeId, Pageable pageable) {
        StoreEntity storeEntity = getStoreEntityOrException(storeId);

        return storeProductEntityRepository.findAllByStore(storeEntity, pageable).map(StoreProduct::fromEntity);
    }

    public StoreProduct getProduct(Long storeId, Long productId) {
        StoreProductEntity storeProductEntity = getStoreProductOrException(storeId, productId);

        return StoreProduct.fromEntity(storeProductEntity);
    }

    public StoreEntity searchStore(AddressEntity addressEntity) {
        return storeEntityRepository.findByAddress_Id(addressEntity.getId()).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded by address", addressEntity.getId()));
        });
    }

    public ProductEntity getProductEntityOrException(Long productId) {
        return productEntityRepository.findById(productId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND, String.format("%s not founded", productId));
        });
    }

    public StoreEntity getStoreEntityOrException(Long storeId) {
        return storeEntityRepository.findById(storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.STORE_NOT_FOUND, String.format("%s not founded", storeId));
        });
    }

    public SellerStoreEntity isStoreManager(Long userId, Long storeId) {
        return sellerStoreEntityRepository.findBySeller_IdAndStore_Id(userId, storeId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userId, storeId));
        });
    }

    private StoreProductEntity getStoreProductOrException(Long storeId, Long productId) {
        return storeProductEntityRepository.findByStore_IdAndProduct_Id(storeId, productId).orElseThrow(() -> {
            throw new GreenSumerBackApplicationException(ErrorCode.PRODUCT_NOT_FOUND_ON_STORE, String.format("%s not founded on Store #%s", productId, storeId));
        });
    }

    public List<ProductEntity> getStoreWithProductList(List<Long> productsId) {
        return productEntityRepository.findAllByIdIn(productsId);
    }

    public List<ProductEntity> getProductListOnStore(List<Long> productsId, Long storeId) {
        return storeProductEntityRepository.findAllByStore_IdAndProductIn(storeId, productsId)
                .stream().map(StoreProductEntity::getProduct).collect(Collectors.toList());
    }

}
