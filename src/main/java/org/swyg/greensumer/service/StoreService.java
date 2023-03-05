package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.domain.constant.StoreType;
import org.swyg.greensumer.dto.*;
import org.swyg.greensumer.dto.request.*;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.images.ProductImageEntityRepository;
import org.swyg.greensumer.repository.images.StoreImageEntityRepository;
import org.swyg.greensumer.repository.store.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreEntityRepository storeEntityRepository;
    private final ProductEntityRepository productEntityRepository;
    private final SellerStoreEntityRepository sellerStoreEntityRepository;
    private final AddressService addressService;
    private final StoreProductEntityRepository storeProductEntityRepository;
    private final ProductImageEntityRepository productImageEntityRepository;
    private final UserEntityRepositoryService userEntityRepositoryService;
    private final StoreImageEntityRepository storeImageEntityRepository;
    private final StoreCacheRepository storeCacheRepository;

    @Transactional
    public void create(StoreCreateRequest request, String username) {
        // 1. 주소 확인(없는 경우 주소 정보를 등록하여 반환)
        AddressEntity addressEntity = addressService.findAddressEntity(request.getAddress(), request.getRoadname(), request.getLat(), request.getLng());

        UserEntity userEntity = userEntityRepositoryService.findByUsernameOrException(username);

        // 2. 가게명과 주소가 동일한 가게가 존재할 경우 중복 에러 발생
        storeEntityRepository.findByNameAndAddress(request.getName(), addressEntity).ifPresent(it -> {
            throw new GreenSumerBackApplicationException(ErrorCode.DUPLICATED_STORE_NAME, String.format("%s is duplicated", request.getName()));
        });

        // 3. 가게 객체 생성
        StoreEntity storeEntity = StoreEntity.of(request.getName(), request.getDescription(), StoreType.valueOf(request.getType().toUpperCase()), addressEntity, request.getHours(), request.getUrl(), request.getPhone());

        if (request.getImages().size() > 0) {
            storeEntity.addImages(storeImageEntityRepository.findAllByIdIn(request.getImages()));
        }

        StoreEntity storeEntity1 = storeEntityRepository.save(storeEntity);

        SellerStoreEntity sellerStoreEntity = sellerStoreEntityRepository.save(SellerStoreEntity.of(storeEntity, userEntity));
        storeEntity.addSellerStore(sellerStoreEntity);

        storeCacheRepository.save(Store.fromEntity(storeEntity1));
    }

    @Transactional
    public void modify(Long storeId, StoreModifyRequest request, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);

        isStoreManager(user.getId(), storeId);

        storeEntity.updateStore(request.getType(), request.getDescription(), request.getHours(), request.getPhone(), request.getUrl());
        storeEntity.updateAddress(addressService.updateAddress(storeEntity.getAddress().getId(), request.getAddress(), request.getRoadname(), request.getLat(), request.getLng()));

        if (request.getImages().size() > 0) {
            storeEntity.addImages(storeImageEntityRepository.findAllByIdIn(request.getImages()));
        }
    }

    public void delete(Long storeId, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        storeEntity.clear();

        storeEntityRepository.deleteById(storeId);
    }

    public Page<Store> list(Pageable pageable, String username) {
        userEntityRepositoryService.loadUserByUsername(username);

        return storeEntityRepository.findAll(pageable).map(Store::fromEntity);
    }

    public Page<SellerStore> mylist(Pageable pageable, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
        return sellerStoreEntityRepository.findAllBySeller_Id(user.getId(), pageable).map(SellerStore::fromEntity);
    }

    @Transactional
    public Product registerProduct(Long storeId, ProductCreateRequest request, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        ProductEntity productEntity = ProductEntity.of(request.getName(), request.getPrice(), request.getStock(), request.getDescription());

        if (request.getImages().size() != 0) {
            productEntity.addImages(productImageEntityRepository.findAllByIdIn(request.getImages()));
        }

        ProductEntity savedEntity = productEntityRepository.save(productEntity);

        StoreProductEntity storeProductEntity = storeProductEntityRepository.save(StoreProductEntity.of(storeEntity, savedEntity));
        storeEntity.addStoreProduct(storeProductEntity);
        savedEntity.addStoreProduct(storeProductEntity);

        return Product.fromEntity(savedEntity);
    }

    @Transactional
    public Product modifyProduct(Long storeId, Long productId, ProductModifyRequest request, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
        StoreEntity storeEntity = getStoreEntityOrException(storeId);
        SellerStoreEntity storeManager = isStoreManager(user.getId(), storeId);
        ProductEntity productEntity = getProductEntityOrException(productId);

        productEntity.updateProductInfo(request.getName(), request.getDescription(), request.getPrice(), request.getStock());

        if (request.getImages().size() > 0)
            productEntity.addImages(productImageEntityRepository.findAllByIdIn(request.getImages()));

        return Product.fromEntity(productEntity);
    }

    @Transactional
    public void deleteProduct(Long storeId, Long productId, String username) {
        User user = (User) userEntityRepositoryService.loadUserByUsername(username);
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

    public Long listCount() {
        return storeEntityRepository.count();
    }

    public List<Store> searchStoreList() {
        // redis
        List<Store> storeList = storeCacheRepository.findAll();

        if(!storeList.isEmpty()) {
            log.info("[StoreService - searchStoreList] redis findAll success!");
            return storeList;
        }

        // db
        return storeEntityRepository.findAll()
                .stream()
                .map(Store::fromEntity)
                .collect(Collectors.toList());
    }

    // TODO: 임시
    public void saveAll() {
        // redis
        storeEntityRepository.findAll().stream().forEach(storeEntity -> storeCacheRepository.save(Store.fromEntity(storeEntity)));
    }

    @Transactional
    public void connectImagesAtStore(Long storeId, ConnectionImageRequest request) {
        StoreEntity storeEntity = getStoreEntityOrException(storeId);

        List<Long> images = LongStream.iterate(request.getStart(), i -> i+1)
                        .limit(request.getEnd()).boxed().toList();

        storeEntity.addImages(storeImageEntityRepository.findAllByIdIn(images));
    }

    @Transactional
    public void connectImagesAtProduct(Long storeId, ConnectionImageRequest request) {
        StoreEntity storeEntity = storeEntityRepository.getReferenceById(storeId);

        List<Long> images = LongStream.iterate(request.getStart(), i -> i+1)
                .limit(request.getEnd()).boxed().toList();

        storeEntity.addImages(storeImageEntityRepository.findAllByIdIn(images));
    }
}
