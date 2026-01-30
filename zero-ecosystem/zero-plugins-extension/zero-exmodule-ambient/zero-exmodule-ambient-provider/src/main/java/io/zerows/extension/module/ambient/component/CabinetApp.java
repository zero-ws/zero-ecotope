package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XAppDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-07-08
 */
@Slf4j
public class CabinetApp implements Cabinet<XApp> {
    @Override
    public Future<ConcurrentMap<String, XApp>> loadAsync(final Vertx container) {
        Objects.requireNonNull(container);
        final Configuration configuration = Ke.getConfiguration();
        final XAppDao appDao = new XAppDao(configuration, container);
        return appDao.findAll().compose(apps -> {
            log.info("{} XApp 应用加载 SUCCESS / 数量：{}", AtConstant.K_PREFIX, apps.size());
            return Ux.future(Ut.elementMap(apps, XApp::getId));
        });
    }
}
