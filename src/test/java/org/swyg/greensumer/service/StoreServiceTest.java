package org.swyg.greensumer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.swyg.greensumer.domain.*;
import org.swyg.greensumer.dto.SellerStore;
import org.swyg.greensumer.dto.Store;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.images.StoreImageEntityRepository;
import org.swyg.greensumer.repository.store.AddressEntityRepository;
import org.swyg.greensumer.repository.store.ProductEntityRepository;
import org.swyg.greensumer.repository.store.SellerStoreEntityRepository;
import org.swyg.greensumer.repository.store.StoreEntityRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.*;
import static org.swyg.greensumer.fixture.Fixtures.*;
import static org.swyg.greensumer.fixture.RequestFixture.StoreCreateRequest;
import static org.swyg.greensumer.fixture.RequestFixture.StoreModifyRequest;

@DisplayName("비즈니스 로직 - 제로웨이스트 샵")
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks private StoreService sut;

    @Mock private StoreEntityRepository storeEntityRepository;
    @Mock private UserEntityRepositoryService userEntityRepositoryService;
    @Mock private ProductEntityRepository productEntityRepository;
    @Mock private SellerStoreEntityRepository sellerStoreEntityRepository;
    @Mock private AddressService addressService;
    @Mock private AddressEntityRepository addressEntityRepository;
    @Mock private StoreImageEntityRepository storeImageEntityRepository;


    private static UserEntity userEntity;
    private static StoreEntity storeEntity;
    private static ProductEntity productEntity;
    private static AddressEntity addressEntity;
    private static SellerStoreEntity sellerStoreEntity;
    private static List<StoreImageEntity> storeImageEntities;

    @BeforeEach
    void setUp() {
        userEntity = userEntity();
        storeEntity = storeEntity();
        productEntity = productEntity();
        addressEntity = addressEntity();
        sellerStoreEntity = sellerStoreEntity();
        storeImageEntities = storeImageEntities();
    }

    @DisplayName("[가게 등록] 가게 정보를 입력하면, 가게를 생성한다.")
    @Test
    void givenStoreInfo_whenRequestingSaveStore_thenReturnNothing() {
        // Given
        willReturn(addressEntity).given(addressService).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        willReturn(userEntity).given(userEntityRepositoryService).findByUsernameOrException(anyString());
        willReturn(Optional.empty()).given(storeEntityRepository).findByNameAndAddress(anyString(), any(AddressEntity.class));
        willReturn(storeImageEntities()).given(storeImageEntityRepository).findAllByIdIn(anyList());
        willReturn(storeEntity).given(storeEntityRepository).save(any());
        willReturn(sellerStoreEntity).given(sellerStoreEntityRepository).save(any());

        // When
        sut.create(StoreCreateRequest(), userEntity.getUsername());

        //Then
        verify(addressService, times(1)).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(anyString());
        verify(storeEntityRepository, times(1)).findByNameAndAddress(anyString(), any(AddressEntity.class));
        verify(storeImageEntityRepository, times(1)).findAllByIdIn(anyList());
        verify(storeEntityRepository, times(1)).save(any());
        verify(sellerStoreEntityRepository, times(1)).save(any());
    }

    @DisplayName("[가게 등록] 회원가입하지 않은 사용자가 요청한 경우, 에러가 발생한다.")
    @Test
    void givenAlreadySavedStoreInfo_whenRequestingSaveStore_thenThrowsUserNotFoundException() {
        // Given
        willReturn(addressEntity).given(addressService).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userEntityRepositoryService).findByUsernameOrException(anyString());

        // When
        Throwable throwable = catchThrowable(() -> sut.create(StoreCreateRequest(), userEntity.getUsername()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("User not founded");

        verify(addressService, times(1)).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(anyString());
        verify(storeEntityRepository, times(0)).findByNameAndAddress(anyString(), any(AddressEntity.class));
        verify(storeImageEntityRepository, times(0)).findAllByIdIn(anyList());
        verify(storeEntityRepository, times(0)).save(any());
        verify(sellerStoreEntityRepository, times(0)).save(any());
    }

    @DisplayName("[가게 등록] 가게 이름과 주소가 이미 등록된 경우, 중복 에러가 발생한다.")
    @Test
    void givenAlreadySavedStoreInfo_whenRequestingSaveStore_thenThrowsDuplicateException() {
        // Given
        willReturn(addressEntity).given(addressService).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        willReturn(userEntity).given(userEntityRepositoryService).findByUsernameOrException(anyString());
        willReturn(Optional.of(storeEntity)).given(storeEntityRepository).findByNameAndAddress(anyString(), any(AddressEntity.class));

        // When
        Throwable throwable = catchThrowable(() -> sut.create(StoreCreateRequest(), userEntity.getUsername()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("Name of Store is duplicated. name is duplicated");

        verify(addressService, times(1)).findAddressEntity(anyString(), anyString(), anyDouble(), anyDouble());
        verify(userEntityRepositoryService, times(1)).findByUsernameOrException(anyString());
        verify(storeEntityRepository, times(1)).findByNameAndAddress(anyString(), any(AddressEntity.class));
        verify(storeImageEntityRepository, times(0)).findAllByIdIn(anyList());
        verify(storeEntityRepository, times(0)).save(any());
        verify(sellerStoreEntityRepository, times(0)).save(any());
    }

    /**
     * java.lang.UnsupportedOperationException 에러 발생
     */

    @Deprecated
    @DisplayName("[가게 수정] 수정된 가게 정보를 입력하면, 가게정보가 수정된다.")
    @Test
    void givenUpdatedStoreInfo_whenRequestingModifyStore_thenReturnStore() {
        // Given
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(anyString());
        willReturn(Optional.of(storeEntity)).given(storeEntityRepository).findById(storeEntity.getId());
        willReturn(Optional.of(sellerStoreEntity)).given(sellerStoreEntityRepository).findBySeller_IdAndStore_Id(userEntity.getId(), storeEntity.getId());
        willReturn(addressEntity).given(addressService).updateAddress(anyLong(), anyString(), anyString(), anyDouble(), anyDouble());
        willReturn(storeImageEntities()).given(storeImageEntityRepository).findAllByIdIn(List.of(1L, 2L, 3L));

        // When
        sut.modify(storeEntity.getId(), StoreModifyRequest(), userEntity.getUsername());

        //Then
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
        verify(storeEntityRepository, times(1)).findById(storeEntity.getId());
        verify(sellerStoreEntityRepository, times(1)).findBySeller_IdAndStore_Id(userEntity.getId(), storeEntity.getId());
        verify(addressService, times(1)).updateAddress(anyLong(), anyString(), anyString(), anyDouble(), anyDouble());
        verify(storeImageEntityRepository, times(1)).findAllByIdIn(List.of(1L, 2L, 3L));
    }

    @DisplayName("[가게 수정] 회원가입하지 않은 유저가 요청한 경우")
    @Test
    void givenUpdatedStoreInfo_whenRequestingModifyStore_thenThrowsUserNotFoundException() {
        // Given
        willThrow(new GreenSumerBackApplicationException(ErrorCode.USER_NOT_FOUND)).given(userEntityRepositoryService).loadUserByUsername(anyString());

        // When
        Throwable throwable = catchThrowable(() -> sut.modify(storeEntity().getId(), StoreModifyRequest(), userEntity.getUsername()));

        //Then
        assertThat(throwable)
                .isInstanceOf(GreenSumerBackApplicationException.class)
                .hasMessage("User not founded");

        //Then
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
        verify(storeEntityRepository, times(0)).findById(anyLong());
        verify(sellerStoreEntityRepository, times(0)).findBySeller_IdAndStore_Id(anyLong(), anyLong());
        verify(addressService, times(0)).updateAddress(anyLong(), anyString(), anyString(), anyDouble(), anyDouble());
        verify(storeImageEntityRepository, times(0)).findAllByIdIn(any());
    }

    /**
     * java.lang.UnsupportedOperationException 에러 발생
     */

    @Deprecated
    @DisplayName("[가게 삭제] 가게아이디를 입력하면 가게를 삭제한다.")
    @Test
    void givenStoreId_whenRequestingDeleteStore_thenReturnNothing() {
        // Given
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(anyString());
        willReturn(Optional.of(storeEntity)).given(storeEntityRepository).findById(anyLong());
        willReturn(Optional.of(sellerStoreEntity)).given(sellerStoreEntityRepository).findBySeller_IdAndStore_Id(userEntity.getId(), storeEntity.getId());
        willReturn(storeImageEntities()).given(storeImageEntityRepository).findAllByIdIn(List.of(1L));
        willDoNothing().given(storeEntityRepository).deleteById(anyLong());

        // When
        sut.delete(storeEntity.getId(), userEntity.getUsername());

        //Then
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
        verify(storeEntityRepository, times(1)).findById(anyLong());
        verify(sellerStoreEntityRepository, times(1)).findBySeller_IdAndStore_Id(userEntity.getId(), storeEntity.getId());
        verify(storeImageEntityRepository, times(1)).findAllByIdIn(List.of(1L));
        verify(storeEntityRepository, times(1)).deleteById(any());
    }

    @DisplayName("모든 가게 리스트 조회")
    @Test
    void givenNothing_whenRequestingStoreList_thenReturnPageStore() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(anyString());
        willReturn(Page.empty()).given(sellerStoreEntityRepository).findAll(pageable);

        // When
        Page<Store> stores = sut.list(pageable, userEntity.getUsername());

        //Then
        assertThat(stores).isEmpty();
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
        verify(sellerStoreEntityRepository, times(1)).findAll(pageable);
    }

    @DisplayName("로그인한 계정에 본인 가게 리스트 조회")
    @Test
    void givenUser_whenRequestingStoreList_thenReturnPageStore() {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(anyString());
        willReturn(Page.empty()).given(sellerStoreEntityRepository).findAllBySeller_Id(userEntity.getId(), pageable);

        // When
        Page<SellerStore> sellerStores = sut.mylist(pageable, userEntity.getUsername());

        //Then
        assertThat(sellerStores).isEmpty();
        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
        verify(sellerStoreEntityRepository, times(1)).findAllBySeller_Id(userEntity.getId(), pageable);
    }

//    @DisplayName("가게에 제품 등록 요청하면 제품이 등록된다.")
//    @Test
//    void givenProduct_whenRequestingRegisterProduct_thenReturnProduct() {
//        // Given
//        Pageable pageable = Pageable.ofSize(10);
//        willReturn(getUser()).given(userEntityRepositoryService).loadUserByUsername(anyString());
//        willReturn(storeEntity).given(storeEntityRepository).findById(anyLong());
//        willReturn(sellerStoreEntity).given(sellerStoreEntityRepository).findBySeller_IdAndStore_Id(anyLong(), anyLong());
//        willReturn(storeEntity()).given(storeEntityRepository).findById(anyLong());
//
//        // When
//        Page<SellerStore> sellerStores = sut.mylist(pageable, userEntity.getUsername());
//
//        //Then
//        assertThat(sellerStores).isEmpty();
//        verify(userEntityRepositoryService, times(1)).loadUserByUsername(anyString());
//        verify(sellerStoreEntityRepository, times(1)).findAllBySeller_Id(userEntity.getId(), pageable);
//    }

//    @DisplayName("제품 아이디입력하고 제품을 수정하면 해당 가게에 등록된 제품을 수정한다.")
//    @Test
//    void givenUpdatedProduct_whenRequestingModifyProduct_thenReturnProduct() {
//        // Given
//        ProductEntity modifiedProductEntity = ModifyProductEntity(productEntity);
//
//        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
//        given(storeEntityRepository.findById(any())).willReturn(Optional.of(storeEntity));
//        given(productEntityRepository.findById(any())).willReturn(Optional.of(productEntity));
//        given(sellerStoreEntityRepository.findBySeller_IdAndStore_Id(any(), any())).willReturn(Optional.of(getSellerStoreEntity()));
//        given(productEntityRepository.saveAndFlush(any())).willReturn(modifiedProductEntity);
//
//        // When
//        Product product = sut.modifyProduct(storeEntity.getId(), productEntity.getId(), getProductModifyRequest(), userEntity.getUsername());
//
//        //Then
//        verify(userEntityRepository, times(1)).findByUsername(any());
//        verify(storeEntityRepository, times(1)).findById(any());
//        verify(productEntityRepository, times(1)).findById(any());
//        then(sellerStoreEntityRepository).should().findBySeller_IdAndStore_Id(any(), any());
//        verify(productEntityRepository, times(1)).saveAndFlush(any());
//    }
//
//    @DisplayName("제품 아이디입력하면 해당 가게에 등록된 제품을 삭제한다.")
//    @Test
//    void givenProductId_whenRequestingDeleteProduct_thenReturnNothing() {
//        // Given
//        given(userEntityRepository.findByUsername(any())).willReturn(Optional.of(userEntity));
//        given(storeEntityRepository.findById(any())).willReturn(Optional.of(storeEntity));
//        given(sellerStoreEntityRepository.findBySeller_IdAndStore_Id(any(), any())).willReturn(Optional.of(getSellerStoreEntity()));
//        doNothing().when(productEntityRepository).deleteById(any());
//
//        // When
//        sut.deleteProduct(storeEntity.getId(), productEntity.getId(), userEntity.getUsername());
//
//        //Then
//        verify(userEntityRepository, times(1)).findByUsername(any());
//        verify(storeEntityRepository, times(1)).findById(any());
//        then(sellerStoreEntityRepository).should().findBySeller_IdAndStore_Id(any(), any());
//        verify(productEntityRepository, times(1)).deleteById(any());
//    }
//
//    @DisplayName("가게 아이디입력하면 해당 가게에 등록된 제품 리스트를 반환한다.")
//    @Test
//    void givenStoreId_whenRequestingProductList_thenReturnProductList() {
//        // Given
//        Pageable pageable = Pageable.ofSize(10);
//        given(storeEntityRepository.findById(any())).willReturn(Optional.of(storeEntity));
//        given(productEntityRepository.findAllByStore(storeEntity, pageable)).willReturn(Page.empty());
//
//        // When
//        Page<Product> products = sut.getStoreWithProductList(storeEntity.getId(), pageable);
//
//        //Then
//        assertThat(products).isEmpty();
//        verify(storeEntityRepository, times(1)).findById(any());
//        verify(productEntityRepository, times(1)).findAllByStore(storeEntity, pageable);
//    }
//
//    @DisplayName("가게 아이디와 제품 아이디를 주면 해당 가게에 등록된 제품를 반환한다.")
//    @Test
//    void givenStoreIdAndProductId_whenRequestingProductOnStore_thenReturnProduct() {
//        // Given
//        Pageable pageable = Pageable.ofSize(10);
//        given(storeEntityRepository.findById(any())).willReturn(Optional.of(storeEntity));
//        given(productEntityRepository.findByStoreAndId(storeEntity, productEntity.getId())).willReturn(Optional.of(createProductEntity()));
//
//        // When
//        Product product = sut.getProduct(storeEntity.getId(), productEntity.getId());
//
//        //Then
//        verify(storeEntityRepository, times(1)).findById(any());
//        verify(productEntityRepository, times(1)).findByStoreAndId(storeEntity, productEntity.getId());
//    }

}