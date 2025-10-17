package io.zerows.plugins.trash;

import io.r2mo.typed.cc.Cc;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.database.jooq.JooqInfix;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Batch;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * Builder for `identifier`
 * 1) Transfer identifier to TABLE_NAME
 * 2) Double check whether the TABLE EXISTING
 * 3) Build Data into TABLE_NAME
 */
@Slf4j
@SuppressWarnings("all")
class TrashBuilder {

    private static ConcurrentMap<String, Field> FIELD_MAP = new ConcurrentHashMap<String, Field>() {
        {
            put("key", DSL.field(DSL.name("KEY"), String.class));
            put("identifier", DSL.field(DSL.name("IDENTIFIER"), String.class));
            put("record", DSL.field(DSL.name("RECORD"), String.class));
            put("sigma", DSL.field(DSL.name("SIGMA"), String.class));
            put("language", DSL.field(DSL.name("LANGUAGE"), String.class));
            put("active", DSL.field(DSL.name("ACTIVE"), Boolean.class));
            put("createdBy", DSL.field(DSL.name("CREATED_BY"), String.class));
            put("createdAt", DSL.field(DSL.name("CREATED_AT"), Timestamp.class));
        }
    };
    private final transient DSLContext context;
    private final transient String identifier;
    private final transient String tableName;
    private static Cc<String, TrashBuilder> CC_BUILDER = Cc.open();

    static TrashBuilder of(final String identifier, final JsonObject options) {
        final String cacheKey = Ut.isNil(options) ? identifier : identifier + "@" + options.hashCode();
        return CC_BUILDER.pick(() -> new TrashBuilder(identifier).init(options), cacheKey);
    }


    private TrashBuilder(final String identifier) {
        this.identifier = identifier;
        final String tableName = this.identifier.toUpperCase()
            /*
             * Here two formatFail
             * 1) such as `ci.server` that contains `.`;
             * 2) such as `x-tabular` that contains `-`;
             */
            .replace('.', '_')
            .replace('-', '_');
        this.tableName = "HIS_" + tableName;
        this.context = JooqInfix.contextTrash();
    }

    @Fluent
    public TrashBuilder init(final JsonObject options) {
        this.context.createTableIfNotExists(DSL.name(this.tableName))
            /* Primary Key */
            .column("KEY", SQLDataType.VARCHAR(36).nullable(false))
            .column("IDENTIFIER", SQLDataType.VARCHAR(255))
            .column("RECORD", SQLDataType.CLOB)
            /* Uniform */
            .column("SIGMA", SQLDataType.VARCHAR(255))
            .column("LANGUAGE", SQLDataType.VARCHAR(20))
            .column("ACTIVE", SQLDataType.BOOLEAN)
            /* Auditor */
            .column("CREATED_AT", SQLDataType.LOCALDATETIME)
            .column("CREATED_BY", SQLDataType.VARCHAR(36))
            /* PRIMARY KEY */
            .constraint(DSL.constraint("PK_" + this.tableName).primaryKey(DSL.name("KEY")))
            .execute();
        log.info("[ ZERO ] ( His ) 数据表 `{}` 已创建！", this.tableName);
        return this;
    }

    public boolean createHistory(final JsonObject content, final MultiMap params) {
        /*
         * Insert History
         */
        final InsertSetMoreStep steps = stepInsert(content);
        steps.execute();
        return true;
    }

    private InsertSetMoreStep stepInsert(final JsonObject content) {
        final InsertSetMoreStep steps = (InsertSetMoreStep) this.context.insertInto(DSL.table(this.tableName));
        steps.set(FIELD_MAP.get("key"), UUID.randomUUID().toString());
        steps.set(FIELD_MAP.get("identifier"), this.identifier);
        steps.set(FIELD_MAP.get("record"), content.encode());
        /*
         * Default Value
         */
        steps.set(FIELD_MAP.get("sigma"), content.getString("sigma"));
        steps.set(FIELD_MAP.get("language"), content.getString("language"));
        steps.set(FIELD_MAP.get("active"), Boolean.TRUE);
        steps.set(FIELD_MAP.get("createdBy"), content.getString("createdBy"));

        final Date date = new Date();
        final Timestamp timestamp = new Timestamp(date.getTime());
        steps.set(FIELD_MAP.get("createdAt"), timestamp);
        return steps;
    }

    public boolean createHistory(final JsonArray content, final MultiMap params) {
        final List<Query> batchOps = new ArrayList<>();
        Ut.itJArray(content).map(this::stepInsert).forEach(batchOps::add);
        final Batch batch = this.context.batch(batchOps);
        batch.execute();
        return true;
    }
}
