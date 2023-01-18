package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewPostEntity is a Querydsl query type for ReviewPostEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewPostEntity extends EntityPathBase<ReviewPostEntity> {

    private static final long serialVersionUID = 853255230L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewPostEntity reviewPostEntity = new QReviewPostEntity("reviewPostEntity");

    public final SetPath<ReviewCommentEntity, QReviewCommentEntity> comments = this.<ReviewCommentEntity, QReviewCommentEntity>createSet("comments", ReviewCommentEntity.class, QReviewCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final StringPath hashtag = createString("hashtag");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath imagePath = createString("imagePath");

    public final QProductEntity product;

    public final DateTimePath<java.sql.Timestamp> registeredAt = createDateTime("registeredAt", java.sql.Timestamp.class);

    public final StringPath title = createString("title");

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final QUserEntity user;

    public QReviewPostEntity(String variable) {
        this(ReviewPostEntity.class, forVariable(variable), INITS);
    }

    public QReviewPostEntity(Path<? extends ReviewPostEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewPostEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewPostEntity(PathMetadata metadata, PathInits inits) {
        this(ReviewPostEntity.class, metadata, inits);
    }

    public QReviewPostEntity(Class<? extends ReviewPostEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new QProductEntity(forProperty("product"), inits.get("product")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

