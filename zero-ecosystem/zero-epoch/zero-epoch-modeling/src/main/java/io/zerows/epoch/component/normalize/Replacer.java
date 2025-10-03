package io.zerows.epoch.component.normalize;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.configuration.module.MDConnect;
import io.zerows.epoch.program.Ut;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 针对新版的表名构造 Dao 的专用生成器，用于构造原始的
 * <pre><code>
 *     1. KConnect
 *     2. KModule
 *     3. KClass / KHybrid
 * </code></pre>
 *
 * @author lang : 2024-05-10
 */
public interface Replacer<T> {

    @SuppressWarnings("all")
    Cc<String, Replacer> CC_REPLACER = Cc.openThread();

    @SuppressWarnings("all")
    static Replacer<MDConnect> ofConnect() {
        return (Replacer<MDConnect>) CC_REPLACER.pick(ReplacerConnect::new, OnenessConnect.class.getName());
    }

    default List<T> build(final JsonArray configA) {
        final List<T> buildList = new ArrayList<>();
        Ut.itJArray(configA).forEach(json -> {
            final T buildItem = this.build(json);
            if (Objects.nonNull(buildItem)) {
                buildList.add(buildItem);
            }
        });
        return buildList;
    }

    T build(JsonObject configJ);

    default OLog logger() {
        return Ut.Log.uca(this.getClass());
    }
}
