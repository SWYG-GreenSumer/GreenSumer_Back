package org.swyg.greensumer.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@MappedSuperclass
public abstract class PostEntity extends DateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id")
    protected UserEntity user;

    @Column(name = "title", length = 50) protected String title;
    @Column(name = "content", columnDefinition = "TEXT")  protected String content;
}
