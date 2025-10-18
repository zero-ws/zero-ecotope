package io.zerows.extension.commerce.rbac.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.commerce.rbac.agent.service.business.ResourceStub;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SActionDao;
import io.zerows.extension.commerce.rbac.domain.tables.daos.SResourceDao;
import io.zerows.extension.commerce.rbac.eon.Addr;
import io.zerows.program.Ux;
import jakarta.inject.Inject;

import static io.zerows.extension.commerce.rbac.util.Sc.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Queue
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
        LOG.Web.info(this.getClass(),
            "The criteria of input parameters: {0}", query.encode());
        return Ux.Jooq.join()
            /*
             * Join in jooq here:
             *
             * S_RESOURCE ( key )
             *      JOIN
             * S_ACTION ( resourceId )
             */
            .add(SResourceDao.class)
            .join(SActionDao.class, "resourceId")
            .searchAsync(query);
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
