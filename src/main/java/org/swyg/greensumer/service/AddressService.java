package org.swyg.greensumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                () -> addressEntityRepository.save(AddressEntity.builder().address(address).roadname(roadname).latitude(latitude).longitude(longitude).build())
        );
    }

    public AddressEntity searchAddress(Long id) {
        return addressEntityRepository.findById(id).orElseThrow(() -> new GreenSumerBackApplicationException(ErrorCode.ADDRESS_NOT_FOUND, String.format("%s not founded", id)));
    }

    @Transactional
    public AddressEntity updateAddress(Long id, String address, String roadname, double latitude, double longitude) {
        AddressEntity addressEntity = searchAddress(id);

        return addressEntity.updateAddress(address, roadname, latitude, longitude);
    }
}
