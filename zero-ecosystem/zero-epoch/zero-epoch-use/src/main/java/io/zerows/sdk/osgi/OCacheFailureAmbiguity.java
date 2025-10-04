package io.zerows.sdk.osgi;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-04-17
 */
class OCacheFailureAmbiguity implements OCacheFailure {
    /**
     * 全局数据，由于异常数据比较特殊的数据结构，计算过程中相对麻烦，所以此处直接提供全局数据的提取
     * <pre><code>
     *     error:
     *     info:
     * </code></pre>
     * 全局数据为包域，可以直接从接口通过静态方式提取，直接省略掉对象引用的提取流程
     */
    static final JsonObject GLOBAL_DATA = new JsonObject();

    /**
     * 单个 Bundle 对应的数据，此处的结构对应关系
     * {@link OCacheFailure} x 1 -> {@link Bundle} x 1 -> {@link JsonObject} x 1
     */
    private final JsonObject storedData = new JsonObject();
    private final Bundle bundle;

    OCacheFailureAmbiguity(final Bundle bundle) {
        this.bundle = bundle;
    }

    // 初始化流程（修复无法配置的 BUG）
    @Override
    public <C> OCacheFailure configure(final C configuration) {
        if (configuration instanceof final HSetting setting) {
            final HConfig error = setting.infix(YmlCore.error.__KEY);

            // 双环境都需要使用
            final JsonObject dataInit = error.options();
            GLOBAL_DATA.mergeIn(dataInit, true);

            // 是否开启 OSGI 环境
            if (Objects.nonNull(this.bundle)) {
                this.storedData.mergeIn(error.options(), true);
            }
        }
        return this;
    }

    @Override
    public JsonObject valueGet(final String errorCode) {
        final String key = errorCode.startsWith("E") ? errorCode : "E" + errorCode;
        return Ut.valueJObject(this.storedData, key);
    }

    @Override
    public JsonObject value() {
        return this.storedData.copy();
    }

    @Override
    public Set<String> keys() {
        final JsonObject errors = Ut.valueJObject(this.storedData, KName.ERROR);
        return errors.fieldNames();
    }

    @Override
    public OCacheFailure add(final JsonObject errorYml) {
        // error
        final JsonObject dataJ = Ut.valueJObject(errorYml, true);
        final JsonObject errorJ = Ut.valueJObject(dataJ, KName.ERROR);
        if (!errorJ.isEmpty()) {
            Ut.Log.exception(this.getClass())
                .info("\"{}\" errors has been added to the Store.", errorJ.size());
        }
        GLOBAL_DATA.mergeIn(dataJ, true);
        // OSGI 环境
        if (Objects.nonNull(this.bundle)) {
            this.storedData.mergeIn(dataJ, true);
        }
        return this;
    }

    @Override
    public OCacheFailure remove(final JsonObject errorYml) {
        // error
        final JsonObject dataJ = Ut.valueJObject(errorYml, true);
        final JsonObject errorJ = Ut.valueJObject(dataJ, KName.ERROR);

        Ut.Log.exception(this.getClass())
            .info("\"{}\" errors has been removed from the Store.", errorJ.fieldNames().size());
        this.remove(GLOBAL_DATA, dataJ);
        // OSGI 环境
        if (Objects.nonNull(this.bundle)) {
            this.remove(this.storedData, dataJ);
        }
        return this;
    }

    private void remove(final JsonObject original, final JsonObject removed) {
        final JsonObject errorStored = original.getJsonObject(KName.ERROR, new JsonObject());
        final JsonObject errorRemoved = removed.getJsonObject(KName.ERROR, new JsonObject());
        errorRemoved.fieldNames().forEach(errorStored::remove);
        final JsonObject infoStored = original.getJsonObject(KName.INFO, new JsonObject());
        final JsonObject infoRemoved = removed.getJsonObject(KName.INFO, new JsonObject());
        infoRemoved.fieldNames().forEach(infoStored::remove);
    }
}
