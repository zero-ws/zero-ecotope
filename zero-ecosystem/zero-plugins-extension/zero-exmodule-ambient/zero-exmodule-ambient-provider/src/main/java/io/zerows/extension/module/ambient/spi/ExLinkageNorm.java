package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.ambient.domain.tables.daos.XLinkageDao;
import io.zerows.extension.module.ambient.serviceimpl.LinkService;
import io.zerows.extension.module.ambient.servicespec.LinkStub;
import io.zerows.extension.skeleton.spi.ExLinkage;
import io.zerows.support.Ut;
import io.zerows.support.fn.Fx;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class ExLinkageNorm implements ExLinkage {
    @Override
    public Future<JsonArray> link(final JsonArray linkage, final boolean vector) {
        final LinkStub linkStub = Ut.singleton(LinkService.class);
        return linkStub.saving(linkage, vector);
    }

    @Override
    public Future<Boolean> unlink(final JsonObject criteria) {
        return DB.on(XLinkageDao.class).deleteByAsync(criteria);
    }

    @Override
    public Future<JsonArray> fetch(final JsonObject criteria) {
        return DB.on(XLinkageDao.class).fetchJAsync(criteria).compose(Fx.ofJArray(
            KName.SOURCE_DATA,
            KName.TARGET_DATA
        ));
    }
}
