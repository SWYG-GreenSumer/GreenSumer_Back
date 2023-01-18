package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -438027279L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QAddressEntity addressEntity;

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final DateTimePath<java.sql.Timestamp> registeredAt = createDateTime("registeredAt", java.sql.Timestamp.class);

    public final EnumPath<org.swyg.greensumer.domain.constant.UserRole> role = createEnum("role", org.swyg.greensumer.domain.constant.UserRole.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final StringPath username = createString("username");

    public QUserEntity(String variable) {
        this(UserEntity.class, forVariable(variable), INITS);
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserEntity(PathMetadata metadata, PathInits inits) {
        this(UserEntity.class, metadata, inits);
    }

    public QUserEntity(Class<? extends UserEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.addressEntity = inits.isInitialized("addressEntity") ? new QAddressEntity(forProperty("addressEntity")) : null;
    }

}

