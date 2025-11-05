package io.zerows.extension.module.rbac.component;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.KPermit;
import io.zerows.extension.module.rbac.exception._80225Exception404AdmitCompilerNull;
import io.zerows.platform.enums.EmSecure;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 独立接口，对应不同的Ui模式的方法提取流程，每一种构造一个核心组件
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HAdmit {

    Cc<String, HAdmit> CCT_ADMIT_COMPILER = Cc.openThread();

    /*
     * 此处 qr 已经是做过 fromExpression 解析的值，可直接作为 qr 来处理
     */
    static HAdmit create(final KPermit permit, final Class<?> target) {
        final EmSecure.ScIn in = permit.source();
        final Supplier<HAdmit> supplier = __H1H.ADMIN_COMPILER.get(in);
        if (Objects.isNull(supplier)) {
            return null;
        }
        final HAdmit compiler = CCT_ADMIT_COMPILER.pick(supplier, in.name());

        // Error-80225
        final Class<?> targetCls = Objects.isNull(target) ? HAdmit.class : target;
        Fn.jvmKo(Objects.isNull(compiler), _80225Exception404AdmitCompilerNull.class, in);
        return compiler;
    }

    Future<JsonArray> ingest(JsonObject qr, JsonObject config);
}
