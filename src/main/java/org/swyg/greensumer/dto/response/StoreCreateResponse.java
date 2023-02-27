package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Store;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String hours;
    private Double lat;
    private Double lng;
    private Set<ImageResponse> images;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static StoreCreateResponse fromStore(Store store) {
        return new StoreCreateResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getAddress().getAddress(),
                store.getHours(),
                store.getAddress().getLat(),
                store.getAddress().getLng(),
                store.getLogos().stream()
                        .map(ImageResponse::fromImage)
                        .collect(Collectors.toUnmodifiableSet()),
                store.getRegisteredAt(),
                store.getUpdatedAt(),
                store.getDeletedAt()
        );
    }
}
