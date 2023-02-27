package org.swyg.greensumer.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.StoreEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
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

    @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registeredAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class) @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deletedAt;

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
                        .map(Image::fromStoreImageEntity)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
