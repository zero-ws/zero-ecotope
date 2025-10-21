package io.zerows.extension.runtime.ambient.uca.boot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.extension.runtime.ambient.domain.tables.daos.XSourceDao;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.runtime.ambient.eon.AtMsg;
import io.zerows.extension.skeleton.common.Ke;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import org.jooq.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-07-08
 */
public class CabinetSource implements Cabinet<List<XSource>> {
    @Override
    public Future<ConcurrentMap<String, List<XSource>>> loadAsync(final Vertx container) {
        Objects.requireNonNull(container);
        final Configuration configuration = Ke.getConfiguration();
        final XSourceDao sourceDao = new XSourceDao(configuration, container);
        return sourceDao.findAll().compose(sources -> {
            this.logger().info(AtMsg.CABINET_SOURCE, sources.size());
            final ConcurrentMap<String, List<XSource>> grouped = Ut.elementGroup(sources, XSource::getAppId);
            return Ux.future(grouped);
        });
    }
}
