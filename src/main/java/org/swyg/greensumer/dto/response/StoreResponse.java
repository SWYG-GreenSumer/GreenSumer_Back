package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.domain.ImageEntity;
import org.swyg.greensumer.dto.Image;
import org.swyg.greensumer.dto.Store;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {
    private Integer id;
    private UserResponse userResponse;
    private String name;
    private String description;
    private String address;
    private String hours;
    private Double lat;
    private Double lng;
    private Set<Image> images;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static StoreResponse fromStore(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getSellerStores().size() == 0 ? new UserResponse() : UserResponse.fromUser(store.getSellerStores().stream().iterator().next().getSeller()),
                store.getName(),
                store.getDescription(),
                store.getAddress().getAddress(),
                store.getHours(),
                store.getAddress().getLat(),
                store.getAddress().getLng(),
                store.getLogos(),
                store.getRegisteredAt(),
                store.getUpdatedAt(),
                store.getDeletedAt()
        );
    }
}
