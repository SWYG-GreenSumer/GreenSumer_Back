package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVerificationEntity is a Querydsl query type for VerificationEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVerificationEntity extends EntityPathBase<VerificationEntity> {

    private static final long serialVersionUID = 188815169L;

    public static final QVerificationEntity verificationEntity = new QVerificationEntity("verificationEntity");

    public final StringPath code = createString("code");

    public final DateTimePath<java.sql.Timestamp> expiredAt = createDateTime("expiredAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> startedAt = createDateTime("startedAt", java.sql.Timestamp.class);

    public final BooleanPath status = createBoolean("status");

    public final StringPath subject = createString("subject");

    public QVerificationEntity(String variable) {
        super(VerificationEntity.class, forVariable(variable));
    }

    public QVerificationEntity(Path<? extends VerificationEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVerificationEntity(PathMetadata metadata) {
        super(VerificationEntity.class, metadata);
    }

}

