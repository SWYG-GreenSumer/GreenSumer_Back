package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreCreateRequest {
    private String name;
    private String type;
    private String description;
    private String address;
    private String hours;
    private String lat;
    private String lng;
    private String logo;

    public static StoreCreateRequest of(String name, String type, String description, String address, String hours, String lat, String lng, String logo) {
        return new StoreCreateRequest(name, type, description, address, hours, lat, lng, logo);
    }
}
