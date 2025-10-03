package io.zerows.extension.runtime.ambient.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.support.FnBase;
import io.zerows.component.aop.Before;
import io.zerows.epoch.corpus.io.zdk.qbe.HocTrue;
import io.zerows.extension.runtime.ambient.domain.tables.pojos.XCategory;
import io.zerows.extension.runtime.ambient.uca.validator.TreeChecker;
import io.zerows.specification.atomic.HReturn;

import java.util.Set;

/**
 * @author lang : 2023-05-27
 */
public class BeforeTreeChecker implements Before {
    private final HReturn.HTrue<XCategory> checker = TreeChecker.of();

    @Override
    public Set<ChangeFlag> types() {
        return Set.of(ChangeFlag.DELETE);
    }

    @Override
    public Future<JsonObject> beforeAsync(final JsonObject data, final JsonObject config) {
        // 全为 false 过
        return FnBase.passNone(data, HocTrue.web403Link(this.getClass(), data), Set.of(
            (input) -> this.checker.executeJAsync(data, config)
        ));
    }

    @Override
    public Future<JsonArray> beforeAsync(final JsonArray data, final JsonObject config) {
        // 全为 false 过
        return FnBase.passNone(data, HocTrue.web403Link(this.getClass(), data), Set.of(
            (input) -> this.checker.executeJAsync(data, config)
        ));
    }
}
