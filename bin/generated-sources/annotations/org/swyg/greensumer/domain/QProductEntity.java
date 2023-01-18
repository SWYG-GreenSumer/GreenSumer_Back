package org.swyg.greensumer.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductEntity is a Querydsl query type for ProductEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductEntity extends EntityPathBase<ProductEntity> {

    private static final long serialVersionUID = -1746294161L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductEntity productEntity = new QProductEntity("productEntity");

    public final DateTimePath<java.sql.Timestamp> deletedAt = createDateTime("deletedAt", java.sql.Timestamp.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final DateTimePath<java.sql.Timestamp> registeredAt = createDateTime("registeredAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> stock = createNumber("stock", Integer.class);

    public final QStoreEntity store;

    public final DateTimePath<java.sql.Timestamp> updatedAt = createDateTime("updatedAt", java.sql.Timestamp.class);

    public QProductEntity(String variable) {
        this(ProductEntity.class, forVariable(variable), INITS);
    }

    public QProductEntity(Path<? extends ProductEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductEntity(PathMetadata metadata, PathInits inits) {
        this(ProductEntity.class, metadata, inits);
    }

    public QProductEntity(Class<? extends ProductEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new QStoreEntity(forProperty("store"), inits.get("store")) : null;
    }

}

