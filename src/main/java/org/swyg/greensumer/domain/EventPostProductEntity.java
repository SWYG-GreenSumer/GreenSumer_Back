package org.swyg.greensumer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "event_product")
@SQLDelete(sql = "UPDATE event_product SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
public class EventPostProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "event_id") private EventPostEntity event;

    @ManyToOne @JoinColumn(name = "product_id") private ProductEntity product;

    public void setEvent(EventPostEntity event) {
        this.event = event;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
