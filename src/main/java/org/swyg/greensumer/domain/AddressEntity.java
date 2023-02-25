package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Objects;

@Builder
@AllArgsConstructor
@Getter
@Table(name = "address")
@SQLDelete(sql = "UPDATE address SET deleted_at = NOW() where id=?")
@Entity
@Where(clause = "deleted_at is NULL")
public class AddressEntity extends DateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String roadname;

    @Column(name = "lat", unique = true) private Double latitude;
    @Column(name = "lng", unique = true) private Double longitude;

    public AddressEntity() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressEntity that)) return false;
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(this.getId()); }

    public AddressEntity updateAddress(String address, String roadname, double latitude, double longitude) {
        this.address = address;
        this.roadname = roadname;
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }
}
