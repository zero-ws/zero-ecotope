package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonArray;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.List;
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
class MakerIoConnect extends MakerIoBase<MDConnect> {
    MakerIoConnect(final ZeroFs io) {
        super(io);
    }

    @Override
    public ConcurrentMap<String, MDConnect> build(final String filename,
                                                  final HBundle bundle,
                                                  final Object... args) {
        // 部分模块没有 /model/connect.yml 文件，如 CRUD
        final JsonArray connectA = this.io().isExist(filename) ?
            this.io().inYamlA(filename) : new JsonArray();

        if (Ut.isNil(connectA)) {
            // 无定义
            return new ConcurrentHashMap<>();
        } else {
            // 替换定义
            final Replacer<MDConnect> connectReplacer = Replacer.ofConnect();
            final List<MDConnect> connectList = connectReplacer.build(connectA);
            return Ut.elementMap(connectList, MDConnect::getTable);
        }
    }
}
