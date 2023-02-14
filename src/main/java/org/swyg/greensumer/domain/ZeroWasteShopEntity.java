package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "zerowasteshop")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZeroWasteShopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopName;
    private String shopAddress;
    private double latitude;
    private double longitude;

    public void changeZeroWasteShopAddress(String address) {
        this.shopAddress = address;
    }
}
