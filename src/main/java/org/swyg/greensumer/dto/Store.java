package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.StoreEntity;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private Integer id;
    private User user;
    private String name;
    private String description;
    private String address;
    private String hours;
    private String lat;
    private String lng;
    private String logo;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Store fromEntity(StoreEntity entity){
        return new Store(
                entity.getId(),
                User.fromEntity(entity.getUser()),
                entity.getName(),
                entity.getDescription(),
                entity.getAddress(),
                entity.getHours(),
                entity.getLat(),
                entity.getLng(),
                entity.getLogo(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
