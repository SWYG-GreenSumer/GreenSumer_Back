package org.swyg.greensumer.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swyg.greensumer.domain.AddressEntity;

import java.util.Optional;

public interface AddressEntityRepository extends JpaRepository<AddressEntity, Long> {

    Optional<AddressEntity> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
