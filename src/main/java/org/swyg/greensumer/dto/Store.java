package org.swyg.greensumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.StoreEntity;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private Long id;
    private String name;
    private String description;
    private Address address;
    private String hours;
    private String phone;
    private String url;
    private Set<Image> logos;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Store fromEntity(StoreEntity entity){
        return new Store(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                Address.fromEntity(entity.getAddress()),
                entity.getHours(),
                entity.getPhone(),
                entity.getUrl(),
                entity.getLogos().stream()
                        .map(Image::fromEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
