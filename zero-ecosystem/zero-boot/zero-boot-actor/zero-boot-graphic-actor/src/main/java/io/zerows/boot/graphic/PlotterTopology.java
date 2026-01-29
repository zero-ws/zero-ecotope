package io.zerows.boot.graphic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XCategoryDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XCategory;
import io.zerows.platform.constant.VString;
import io.zerows.platform.metadata.KRef;
import io.zerows.program.Ux;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.zerows.boot.extension.util.Ox.LOG;

public class PlotterTopology extends PlotterBase {
    @Override
    public Future<JsonObject> drawAsync(final String recordId, final String relationId) {
        /*
         * ci.device -> nodes
         * rl.device.relation -> edges
         */
        return this.drawAsync(recordId, relationId, () -> {
            final KRef refer = new KRef();
            /* 设备读取（节点）*/
            return this.dao(recordId).fetchAllAsync()
                .compose(Ux::futureA).compose(refer::future)
                /* 关系读取（边）*/
                .compose(nil -> this.dao(relationId).fetchAllAsync())
                .compose(Ux::futureA).compose(relation -> PlotterHelper.drawAsync(refer.get(), relation));
        });
    }

    @Override
    public Future<JsonObject> drawAsync(final String recordId, final String relationId, final Set<String> ignores) {
        return this.drawAsync(recordId, relationId, () -> {
            final HApp app = this.ark.app();
            final String sigma = app.option(KName.SIGMA);
            /* 读取 categoryThird */
            final JsonObject condition = new JsonObject();
            condition.put("identifier,i", Ut.toJArray(ignores));
            condition.put(KName.SIGMA, sigma);
            condition.put(VString.EMPTY, Boolean.TRUE);
            return DB.on(XCategoryDao.class).<XCategory>fetchAndAsync(condition).compose(categories -> {
                /* 读取不为 key 的 */
                final Set<String> keys = categories.stream().map(XCategory::getId).collect(Collectors.toSet());
                final JsonObject criteria = new JsonObject();
                criteria.put("categoryThird,!i", Ut.toJArray(keys));
                /* 设备读取（节点） */
                final KRef refer = new KRef();
                return this.dao(recordId).fetchAsync(criteria)
                    .compose(Ux::futureA).compose(refer::future)
                    /* 关系读取（边）*/
                    .compose(nil -> this.dao(relationId).fetchAllAsync())
                    .compose(Ux::futureA).compose(relation -> PlotterHelper.drawAsync(refer.get(), relation));
            });
        });
    }

    private Future<JsonObject> drawAsync(
        final String recordId, final String relationId,
        final Supplier<Future<JsonObject>> consumer) {
        if (Ut.isNil(recordId) || Ut.isNil(relationId)) {
            LOG.Uca.warn(this.getClass(), "传入模型ID有问题：node = {0}, edge = {1}",
                recordId, relationId);
            return Ux.future(new JsonObject());
        } else {
            return consumer.get();
        }
    }
}
