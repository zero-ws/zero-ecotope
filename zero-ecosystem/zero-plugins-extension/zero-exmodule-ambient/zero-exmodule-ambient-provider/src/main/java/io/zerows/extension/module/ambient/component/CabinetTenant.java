package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XTenantDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class CabinetTenant implements Cabinet<XTenant> {
    @Override
    public Future<ConcurrentMap<String, XTenant>> loadAsync(final Vertx container) {
        Objects.requireNonNull(container);
        final Configuration configuration = Ke.getConfiguration();
        final XTenantDao tenantDao = new XTenantDao(configuration, container);
        return tenantDao.findAll().compose(tenants -> {
            log.info("{} XTenant 组户加载 SUCCESS / 数量：{}", AtConstant.K_PREFIX, tenants.size());
            return Ux.future(Ut.elementMap(tenants, XTenant::getId));
        });
    }
}
