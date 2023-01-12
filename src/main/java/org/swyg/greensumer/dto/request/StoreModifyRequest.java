package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreModifyRequest {
    private String name;
    private String type;
    private String description;
    private String address;
    private String roadname;
    private String hours;
    private Double lat;
    private Double lng;
    private String logo;

    public static StoreModifyRequest of(String name, String type, String description, String address, String roadname, String hours, Double lat, Double lng, String logo) {
        return new StoreModifyRequest(name, type, description, address, roadname, hours, lat, lng, logo);
    }
}
