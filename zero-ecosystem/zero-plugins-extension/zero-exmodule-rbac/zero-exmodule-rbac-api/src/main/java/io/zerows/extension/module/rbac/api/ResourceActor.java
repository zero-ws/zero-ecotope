package io.zerows.extension.module.rbac.api;

import io.r2mo.base.dbe.Join;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.module.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.module.rbac.domain.tables.daos.SResourceDao;
import io.zerows.extension.module.rbac.servicespec.ResourceStub;
import io.zerows.support.Ut;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
@Slf4j
public class ResourceActor {

    @Inject
    private transient ResourceStub resourceStub;

    @Address(Addr.Authority.RESOURCE_SEARCH)
    public Future<JsonObject> searchResource(final JsonObject query) {
        /*
         *
         * for searching resource here.
         * - The first step is creating resource with action at the same time
         * - The action and resource is the relation ( 1:1 ) binding
         * - The permissionId ( S_ACTION ) could be null when the new resource created.
         */
        log.info("[ PLUG ] 输入的查询条件：{}", query.encode());
        return DB.on(Join.of(
            SResourceDao.class,
            SActionDao.class, "resourceId"
        )).searchAsync(query);
    }

    @Address(Addr.Authority.RESOURCE_BY_ACTION)
    public Future<JsonObject> fetchResourceByAction(final JsonObject query) {
        final JsonObject queryJ = Ut.elementSubset(query, KName.METHOD, KName.URI, KName.APP_ID);
        log.info("[ PLUG ] 资源查询：{}", queryJ.encode());
        return DB.on(Join.of(
            SResourceDao.class,
            SActionDao.class, "resourceId"
        )).fetchOneAsync(queryJ);
    }

    @Address(Addr.Authority.RESOURCE_GET_CASCADE)
    public Future<JsonObject> getById(final String key) {
        return this.resourceStub.fetchResource(key);
    }

    @Address(Addr.Authority.RESOURCE_ADD_CASCADE)
    public Future<JsonObject> create(final JsonObject data) {
        return this.resourceStub.createResource(data);
    }

    @Address(Addr.Authority.RESOURCE_UPDATE_CASCADE)
    public Future<JsonObject> update(final String key, final JsonObject data) {
        return this.resourceStub.updateResource(key, data);
    }

    @Address(Addr.Authority.RESOURCE_DELETE_CASCADE)
    public Future<Boolean> delete(final String key) {
        return this.resourceStub.deleteResource(key);
    }
}
