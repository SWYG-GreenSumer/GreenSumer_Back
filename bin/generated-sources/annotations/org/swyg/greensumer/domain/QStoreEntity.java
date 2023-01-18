package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreEntity is a Querydsl query type for StoreEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreEntity extends EntityPathBase<StoreEntity> {

    private static final long serialVersionUID = -1034902495L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreEntity storeEntity = new QStoreEntity("storeEntity");

    public final StringPath address = createString("address");

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final StringPath description = createString("description");

    public final StringPath hours = createString("hours");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath lat = createString("lat");

    public final StringPath lng = createString("lng");

    public final StringPath logo = createString("logo");

    public final StringPath name = createString("name");

    public final DateTimePath<java.sql.Timestamp> registeredAt = createDateTime("registeredAt", java.sql.Timestamp.class);

    public final EnumPath<org.swyg.greensumer.domain.constant.StoreType> storeType = createEnum("storeType", org.swyg.greensumer.domain.constant.StoreType.class);

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public final QUserEntity user;

    public QStoreEntity(String variable) {
        this(StoreEntity.class, forVariable(variable), INITS);
    }

    public QStoreEntity(Path<? extends StoreEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreEntity(PathMetadata metadata, PathInits inits) {
        this(StoreEntity.class, metadata, inits);
    }

    public QStoreEntity(Class<? extends StoreEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

