package org.swyg.greensumer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreModifyRequest {
    private String name;
    private String type;
    private String description;
    private String hours;
    private List<Long> images;
    private String address;
    private String roadname;
    private String url;
    private String phone;

    public static StoreModifyRequest of(String name, String type, String description, String hours, List<Long> images, String address, String roadname, String url, String phone) {
        return StoreModifyRequest.builder()
                .name(name)
                .type(type)
                .description(description)
                .hours(hours)
                .images(images)
                .address(address)
                .roadname(roadname)
                .url(url)
                .phone(phone)
                .build();
    }
}
