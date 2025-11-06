package io.zerows.boot.full.base;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.VDBC;
import io.zerows.extension.module.mbsecore.exception._80539Exception404FabricIssue;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.platform.enums.typed.ChangeFlag;
import io.zerows.platform.metadata.KFabric;
import io.zerows.platform.metadata.KMap;
import io.zerows.support.Ut;

import java.util.Objects;


@SuppressWarnings("unchecked")
public abstract class AbstractPlugin<T> {
    protected transient DataAtom atom;
    protected transient KFabric fabric;

    public T bind(final DataAtom atom) {
        this.atom = atom;
        return (T) this;
    }

    public T bind(final KFabric fabric) {
        this.fabric = fabric;
        return (T) this;
    }

    protected LogOf logger() {
        return LogOf.get(this.getClass());
    }

    protected ChangeFlag operation(final JsonObject options) {
        return Ut.toEnum(() -> options.getString(VDBC.I_SERVICE.SERVICE_CONFIG.CONFIGURATION_OPERATION), ChangeFlag.class, ChangeFlag.NONE);
    }

    protected KFabric fabric(final JsonObject options) {
        Fn.jvmKo(Objects.isNull(this.fabric), _80539Exception404FabricIssue.class);
        final Object mapping = options.getValue(KName.MAPPING);
        final KMap.Node item = this.mapping(mapping);
        /* 双条件检查，为 NULL 和 Empty 都没有任何 Mapping 配置*/
        if (Objects.isNull(item) || item.isEmpty()) {
            this.logger().info("[ Combiner ] No mapping! {0}", options.encode());
            return this.fabric;
        } else {
            this.logger().info("[ Combiner ] Mapping found! {0}", item.toString());
            return this.fabric.copy(item);
        }
    }

    private KMap.Node mapping(final Object value) {
        if (value instanceof String) {
            final JsonObject config = Ut.ioJObject(value.toString());
            return this.mapping(config);
        } else if (value instanceof JsonObject) {
            return new KMap.Node((JsonObject) value);
        } else {
            return null;
        }
    }
}
