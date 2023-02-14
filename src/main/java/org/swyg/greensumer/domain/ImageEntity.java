package org.swyg.greensumer.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@MappedSuperclass
public abstract class ImageEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    protected UserEntity userEntity;  // 업로드 한 유저

    @Column(columnDefinition = "TEXT")
    protected String originFilename;

    @Column(columnDefinition = "TEXT")
    protected String savedFilename;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagedata", length = 16_000_000)
    protected byte[] imageData;

    public void update(byte[] imageData) {
        this.imageData = imageData;
    }
}
