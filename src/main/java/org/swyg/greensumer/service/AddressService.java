package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swyg.greensumer.domain.AddressEntity;
import org.swyg.greensumer.exception.ErrorCode;
import org.swyg.greensumer.exception.GreenSumerBackApplicationException;
import org.swyg.greensumer.repository.AddressEntityRepository;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressEntityRepository addressEntityRepository;

    public AddressEntity findAddressEntity(String address, String roadname, Double latitude, Double longitude) {
        return addressEntityRepository.findByLatitudeAndLongitude(latitude, longitude).orElseGet(
                () -> addressEntityRepository.save(AddressEntity.of(address, roadname, latitude, longitude))
        );
    }

    public AddressEntity searchAddress(Long id) {
        return addressEntityRepository.findById(id)
                .orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.ADDRESS_NOT_FOUND, String.format("%s not founded", id)));
    }

    public AddressEntity updateAddress(AddressEntity addressEntity, AddressEntity updatedAddress) {
        // 유효성 검사
        searchAddress(addressEntity.getId());

        if(!(addressEntity.getAddress().equals(updatedAddress.getAddress()) ||
                addressEntity.getRoadname().equals(updatedAddress.getRoadname()) ||
                addressEntity.getLatitude().equals(updatedAddress.getLatitude()) ||
                addressEntity.getLongitude().equals(updatedAddress.getLongitude()))
        ) {

            addressEntity.setAddress(updatedAddress.getAddress());
            addressEntity.setRoadname(updatedAddress.getRoadname());
            addressEntity.setLatitude(updatedAddress.getLatitude());
            addressEntity.setLongitude(updatedAddress.getLongitude());

            return addressEntityRepository.saveAndFlush(addressEntity);
        }

        return addressEntity;
    }
}
