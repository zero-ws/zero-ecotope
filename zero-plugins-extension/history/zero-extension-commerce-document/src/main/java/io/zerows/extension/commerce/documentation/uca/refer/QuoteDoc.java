package io.zerows.extension.commerce.documentation.uca.refer;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.KName;
import io.zerows.unity.Ux;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.documentation.domain.tables.daos.DReferDao;
import io.zerows.extension.commerce.documentation.domain.tables.pojos.DRefer;
import io.zerows.extension.commerce.documentation.eon.em.EmRefer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2023-09-25
 */
class QuoteDoc implements Quote {
    /**
     * 创建从 fromJ -> toA 的关联关系
     *
     * @param fromJ  此处为 DOC 记录
     * @param toA    对端记录
     * @param toType 对端类型
     *
     * @return 关联关系
     */
    @Override
    public Future<JsonArray> plugAsync(final JsonObject fromJ, final JsonArray toA,
                                       final EmRefer.Entity toType) {
        /*
         * DOC -> ??
         * {
         *     "fromType": "DOC",
         *     "fromId": "xxx, fromJ -> key",
         *     "toType": "???",
         *     "toId": "xxx, toA -> key"
         * }
         */
        Boolean inline = Ut.valueT(fromJ, "inline", Boolean.class);
        if (Objects.isNull(inline)) {
            inline = Boolean.TRUE;      // 默认内联，不做外置关联
        }
        final List<DRefer> referList = new ArrayList<>();
        {
            final JsonArray toAData = Ut.valueJArray(toA);
            final Boolean inlineCopy = inline;
            Ut.itJArray(toAData).forEach(toJ -> {
                final DRefer refer = new DRefer();
                refer.setFromType(EmRefer.Entity.DOC.name());
                refer.setFromId(fromJ.getString(KName.KEY));
                refer.setToType(toType.name());
                refer.setToId(toJ.getString(KName.KEY));
                refer.setInline(inlineCopy);
                referList.add(refer);
            });
        }
        return Ux.Jooq.on(DReferDao.class).insertAsync(referList)
            .compose(Ux::futureA);
    }

    @Override
    public Future<JsonArray> fetchAsync(final String fromId, final EmRefer.Entity toType) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("fromType", EmRefer.Entity.DOC.name());
        condition.put("fromId", fromId);
        condition.put("toType", toType.name());
        return Ux.Jooq.on(DReferDao.class).fetchAndAsync(condition)
            .compose(Ux::futureA);
    }

    @Override
    public Future<Boolean> removeAsync(final String fromId, final Set<String> keys,
                                       final EmRefer.Entity toType) {
        final JsonObject condition = Ux.whereAnd();
        condition.put("fromType", EmRefer.Entity.DOC.name());
        condition.put("fromId", fromId);
        condition.put("toType", toType.name());
        condition.put("toId,i", Ut.toJArray(keys));
        return Ux.Jooq.on(DReferDao.class).deleteByAsync(condition);
    }
}
