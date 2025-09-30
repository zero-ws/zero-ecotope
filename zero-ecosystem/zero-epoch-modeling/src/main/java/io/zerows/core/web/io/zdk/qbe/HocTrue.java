package io.zerows.core.web.io.zdk.qbe;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.ClusterSerializable;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.epoch.mature.exception._80306Exception403LinkDeletion;
import io.zerows.specification.atomic.HReturn;

import java.util.Set;

/**
 * @author lang : 2023-06-03
 */
public abstract class HocTrue<T> implements HReturn.HTrue<T> {

    @SuppressWarnings("all")
    private static final Cc<String, HTrue<?>> CCT_CHECKER = Cc.openThread();

    public static WebException web403Link(final Class<?> clazz, final ClusterSerializable json) {
        if (json instanceof final JsonObject data) {
            final String identifier = Ut.valueString(data, KName.IDENTIFIER);
            return new _80306Exception403LinkDeletion(identifier);
        } else if (json instanceof final JsonArray data) {
            final Set<String> identifiers = Ut.valueSetString(data, KName.IDENTIFIER);
            return new _80306Exception403LinkDeletion(Ut.fromJoin(identifiers));
        } else {
            return new _400BadRequestException("[ R2MO ] Link 所需数据类型不合法：" + clazz.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> HTrue<T> of(final Class<?> clazz) {
        return (HTrue<T>) CCT_CHECKER.pick(() -> Ut.instance(clazz), clazz.getName());
    }
}