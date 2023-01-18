package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreModifyRequest {
    private String name;
    private String type;
    private String description;
    private String hours;
    private List<Integer> images;
    private String address;
    private String roadname;
    private Double lat;
    private Double lng;

    public static StoreModifyRequest of(String name, String type, String description, String hours, List<Integer> images, String address, String roadname, Double lat, Double lng) {
        return new StoreModifyRequest(name, type, description, hours, images, address, roadname, lat, lng);
    }
}
