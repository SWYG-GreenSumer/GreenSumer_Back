package org.swyg.greensumer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swyg.greensumer.dto.Store;

import java.sql.Timestamp;

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
    private String logo;
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
                store.getLogo(),
                store.getRegisteredAt(),
                store.getUpdatedAt(),
                store.getDeletedAt()
        );
    }
}
