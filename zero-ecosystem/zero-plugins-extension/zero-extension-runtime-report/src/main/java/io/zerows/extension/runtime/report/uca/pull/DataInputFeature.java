package io.zerows.extension.runtime.report.uca.pull;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.Kv;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.runtime.report.domain.tables.pojos.KpFeature;
import io.zerows.extension.runtime.report.uca.feature.RInComponent;
import io.zerows.extension.runtime.report.uca.pull.io.InValueComponent;

import java.util.Objects;

/**
 * @author lang : 2024-11-04
 */
class DataInputFeature implements DataInput {

    private static final Cc<String, RInComponent> CCT_SKELETON = Cc.openThread();

    @Override
    public Future<Kv<String, Object>> prepare(final JsonObject params, final JsonObject configureJ, final KpFeature feature) {
        Objects.requireNonNull(feature);
        /*
         * 此处考虑两点
         * 1）inComponent / inConfig
         * 2）valueConfig
         * 第一优先级是直接处理 in 模式，此处属于 DataInput 部分，这样处理更容易将输入执行相关转换
         * 第二优先级是 valueConfig，这种模式是直接处理值，不需要输入
         */
        final String inComponent = feature.getInComponent();
        if (Ut.isNil(inComponent)) {
            final RInComponent component = CCT_SKELETON.pick(InValueComponent::new, inComponent);
            // VALUE_CONFIG
            final JsonObject inConfig = Ut.toJObject(feature.getValueConfig());
            inConfig.put(KName.__.INHERIT, configureJ);
            return component.prepare(params, inConfig);
        } else {
            final RInComponent component = CCT_SKELETON.pick(() -> Ut.instance(inComponent), inComponent);
            // IN_CONFIG
            final JsonObject inConfig = Ut.toJObject(feature.getInConfig());
            inConfig.put(KName.__.INHERIT, configureJ);
            return component.prepare(params, inConfig);
        }
    }
}
