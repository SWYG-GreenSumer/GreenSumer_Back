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
    private String lat;
    private String lng;
    private String logo;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static StoreResponse fromStore(Store store) {
        return new StoreResponse(
                store.getId(),
                UserResponse.fromUser(store.getUser()),
                store.getName(),
                store.getDescription(),
                store.getAddress(),
                store.getHours(),
                store.getLat(),
                store.getLng(),
                store.getLogo(),
                store.getRegisteredAt(),
                store.getUpdatedAt(),
                store.getDeletedAt()
        );
    }
}
