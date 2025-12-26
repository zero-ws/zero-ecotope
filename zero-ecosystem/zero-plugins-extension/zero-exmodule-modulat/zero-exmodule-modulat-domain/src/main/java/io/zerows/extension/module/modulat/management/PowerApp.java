package io.zerows.extension.module.modulat.management;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.skeleton.spi.ExModulat;
import io.zerows.program.Ux;
import io.zerows.specification.app.HMod;
import io.zerows.spi.HPI;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * 新版去掉原来繁琐的流程，构造新流程处理
 * <pre>
 *     1. 根据 id 从环境中提取 {@link OCacheMod} 的缓存信息
 *     2. 每个缓存信息中都会包含 mName = {@link HMod} 的基本信息
 * </pre>
 *
 * @author lang : 2024-07-08
 */
public class PowerApp {

    private static final Cc<String, PowerApp> CC_APP = Cc.open();
    private final OCacheMod modReference;

    public PowerApp(final String appId) {
        // 抓取应用关键的引用
        this.modReference = OCacheMod.of(appId);
    }

    public static Future<PowerApp> getCreated(final String appId, final boolean open) {
        Objects.requireNonNull(appId);
        // 先做缓存检查，若缓存中存在则不用创建新内容
        if (CC_APP.containsKey(appId)) {
            return Ux.future(CC_APP.get(appId));
        }


        // 直接调用 HPI 处理
        return HPI.of(ExModulat.class).waitAsync(
            modulat -> modulat.extension(appId, open),
            JsonObject::new
        ).compose(storedJ -> {
            final String configApp = Ut.valueString(storedJ, KName.KEY);
            if (!appId.equals(configApp)) {
                return Ux.future(null);
            }


            // 抓取应用相关的 HMod 缓存
            final PowerApp app = new PowerApp(appId);
            /* 移除 bags / key */
            final JsonObject configAppJ = storedJ.copy();
            configAppJ.remove(KName.KEY);
            configAppJ.remove(KName.App.BAGS);
            Ut.itJObject(configAppJ, JsonObject.class)
                .map(entry -> new PowerMod(entry.getKey(), entry.getValue()))
                .forEach(app::block);
            CC_APP.put(appId, app);
            return Ux.future(app);
        });
    }

    public static Future<PowerApp> getRefresh(final String appId, final boolean open) {
        // 移除旧缓存
        CC_APP.remove(appId);
        // 重建以达到 Refresh 的目的
        return getCreated(appId, open);
    }

    public PowerApp block(final HMod mod) {
        this.modReference.add(mod);
        return this;
    }

    public HMod block(final String name) {
        return this.modReference.valueGet(name);
    }
}
