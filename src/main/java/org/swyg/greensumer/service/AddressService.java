package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.dto.Address;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.store.AddressCacheRepository;
import org.swyg.greensumer.repository.store.AddressEntityRepository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressEntityRepository addressEntityRepository;
    private final AddressCacheRepository addressCacheRepository;

    public AddressEntity findAddressEntity(String address, String roadname, Double latitude, Double longitude) {
        List<Address> addressList = addressCacheRepository.findAll();

        Address target = addressList.stream().filter(e -> e.getLat().equals(latitude) && e.getLng().equals(longitude)).findFirst().get();

        if(!Objects.isNull(target)) {
            return toEntity(target);
        }else {
            AddressEntity entity = AddressEntity.builder().address(address).roadname(roadname).latitude(latitude).longitude(longitude).build();

            AddressEntity addressEntity = addressEntityRepository.findByLatitudeAndLongitude(entity.getLatitude(), entity.getLongitude()).orElseGet(
                    () -> addressEntityRepository.save(entity));

            addressCacheRepository.saveAddress(Address.fromEntity(addressEntity));
            return addressEntity;
        }
    }

    public AddressEntity searchAddress(Long id) {
        Address address = addressCacheRepository.findById(id).orElseGet(() ->
                Address.fromEntity(addressEntityRepository.findById(id).orElseThrow(() ->
                        new GreenSumerBackApplicationException(ErrorCode.ADDRESS_NOT_FOUND, String.format("%s not founded", id)))));

        return toEntity(address);

    }

    @Transactional
    public AddressEntity updateAddress(Long id, String address, String roadname, double latitude, double longitude) {
        AddressEntity addressEntity = searchAddress(id);

        addressCacheRepository.saveAddress(Address.fromEntity(addressEntity.updateAddress(address, roadname, latitude, longitude)));

        return addressEntity;
    }

    private AddressEntity toEntity(Address address) {
        return AddressEntity.builder()
                .id(address.getId())
                .address(address.getAddress())
                .roadname(address.getRoadName())
                .latitude(address.getLat())
                .longitude(address.getLng())
                .build();
    }

}
