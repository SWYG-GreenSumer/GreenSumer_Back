package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewCommentEntity is a Querydsl query type for ReviewCommentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewCommentEntity extends EntityPathBase<ReviewCommentEntity> {

    private static final long serialVersionUID = 1756536231L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewCommentEntity reviewCommentEntity = new QReviewCommentEntity("reviewCommentEntity");

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> registeredAt = createDateTime("registeredAt", java.sql.Timestamp.class);

    public final QReviewPostEntity reviewPost;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final QUserEntity user;

    public QReviewCommentEntity(String variable) {
        this(ReviewCommentEntity.class, forVariable(variable), INITS);
    }

    public QReviewCommentEntity(Path<? extends ReviewCommentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewCommentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewCommentEntity(PathMetadata metadata, PathInits inits) {
        this(ReviewCommentEntity.class, metadata, inits);
    }

    public QReviewCommentEntity(Class<? extends ReviewCommentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reviewPost = inits.isInitialized("reviewPost") ? new QReviewPostEntity(forProperty("reviewPost"), inits.get("reviewPost")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

