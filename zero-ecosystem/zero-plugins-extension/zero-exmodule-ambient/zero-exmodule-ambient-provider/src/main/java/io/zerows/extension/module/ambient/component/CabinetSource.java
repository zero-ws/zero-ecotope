package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-07-08
 */
@Slf4j
public class CabinetSource implements Cabinet<List<XSource>> {
    @Override
    public Future<ConcurrentMap<String, List<XSource>>> loadAsync(final Vertx container) {
        Objects.requireNonNull(container);
        final Configuration configuration = Ke.getConfiguration();
        final XSourceDao sourceDao = new XSourceDao(configuration, container);
        return sourceDao.findAll().compose(sources -> {
            log.info("{} 数据源加载 = SUCCESS / 数量：{}", AtConstant.K_PREFIX, sources.size());
            final ConcurrentMap<String, List<XSource>> grouped = Ut.elementGroup(sources, XSource::getAppId);
            return Ux.future(grouped);
        });
    }
}
