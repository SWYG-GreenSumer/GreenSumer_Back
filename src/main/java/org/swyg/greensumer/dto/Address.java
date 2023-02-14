package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.AddressEntity;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private Long id;
    private String address;
    private String roadName;
    private Double lat;
    private Double lng;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Address fromEntity(AddressEntity entity){
        return new Address(
                entity.getId(),
                entity.getAddress(),
                entity.getRoadname(),
                entity.getLatitude(),
                entity.getLatitude(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
