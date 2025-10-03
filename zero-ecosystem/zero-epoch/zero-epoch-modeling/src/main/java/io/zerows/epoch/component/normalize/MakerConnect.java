package io.zerows.epoch.component.normalize;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.configuration.MDConnect;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre><code>
 *     model/connect.yml -> key = {@link MDConnect}
 * </code></pre>
 *
 * 内置 Dao 的替换流程，如果 Dao 是表名则直接根据表名计算出最终的 Dao 并执行替换（新流程，减少配置）
 *
 * @author lang : 2024-05-12
 */
class MakerConnect implements MakerIo<MDConnect> {
    @Override
    public ConcurrentMap<String, MDConnect> build(final String filename, final Bundle owner,
                                                  final Object... args) {
        final JsonArray connectA;
        if (Objects.isNull(owner)) {
            // 部分模块没有 /model/connect.yml 文件，如 CRUD
            connectA = Ut.ioExist(filename) ? Ut.ioYaml(filename) : new JsonArray();
        } else {
            connectA = Ut.Bnd.ioYamlA(filename, owner);
        }
        if (Ut.isNil(connectA)) {
            return new ConcurrentHashMap<>();
        } else {
            final Replacer<MDConnect> connectReplacer = Replacer.ofConnect();
            final List<MDConnect> connectList = connectReplacer.build(connectA);
            return Ut.elementMap(connectList, MDConnect::getTable);
        }
    }
}
