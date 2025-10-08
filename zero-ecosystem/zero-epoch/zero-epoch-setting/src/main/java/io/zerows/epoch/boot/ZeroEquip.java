package io.zerows.epoch.boot;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.configuration.ZeroConfig;
import io.zerows.epoch.configuration.ZeroSetting;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KPlugin;
import io.zerows.epoch.management.AbstractAmbiguity;
import io.zerows.platform.constant.VString;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.spi.HEquip;
import io.zerows.support.Ut;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2023-05-30
 */
public class ZeroEquip extends AbstractAmbiguity implements HEquip {

    private ZeroEquip() {
        super(null);
    }

    private ZeroEquip(final HBundle bundle) {
        super(bundle);
    }

    public static ZeroEquip of(final HBundle bundle) {
        return Objects.isNull(bundle) ? new ZeroEquip() : new ZeroEquip(bundle);
    }

    /**
     * @return {@link ZeroSetting}
     */
    @Override
    public HSetting initialize() {
        /*
         * 基础装配 vertx.yml 读取
         * - bundle/vertx.yml
         * - vertx.yml
         *
         * zero:
         *    lime: key1,key2,key3
         * scanned:
         *
         */
        final JsonObject configuration = new JsonObject(); // Ut.Bnd.ioCombineJ(this.nameZero(null), this.caller()); // ZeroIo.read(null, true);
        final HSetting setting = ZeroSetting.of();
        final JsonObject configZero = Ut.valueJObject(configuration, KName.Internal.ZERO);
        setting.container(this.createConfig(configZero));
        final String extension = Ut.valueString(configZero, YmlCore.LIME);

        final JsonObject configBoot = Ut.valueJObject(configuration, VertxYml.boot.__);
        setting.launcher(this.createConfig(configBoot));

        /*
         * zero:
         *    lime: key1,key2,key3
         */
        final JsonObject configExtension = new JsonObject();
        final Set<String> keys = Ut.toSet(extension, VString.COMMA);
        final Set<String> internal = Arrays.stream(KPlugin.FILE_KEY)
            .collect(Collectors.toSet());
        keys.stream().filter(field -> !internal.contains(field)).forEach(field -> {
            // lime file
            // - vertx-key1.yml
            // - vertx-key2.yml
            // - vertx-key3.yml
            final JsonObject fileData = new JsonObject(); // Ut.Bnd.ioConfigureJ(this.nameZero(field), this.caller()); // ZeroIo.read(field, false);
            configExtension.mergeIn(fileData, true);
        });
        /*
         * 文件合并处理，所有 key 值
         */
        Ut.<JsonObject>itJObject(configExtension).forEach(entry -> {
            final String key = entry.getKey();
            setting.infix(key, this.createConfig(entry.getValue()));
        });
        /*
         * <Internal>
         * server = {}
         * inject = {}
         * error = {}
         * resolver = {}
         */
        Arrays.stream(KPlugin.FILE_KEY).forEach(field -> {
            final JsonObject fileData = new JsonObject(); // Ut.Bnd.ioCombineJ(this.nameZero(field), this.caller()); // ZeroIo.read(field, true);
            setting.infix(field, this.createConfig(fileData));
        });
        return setting;
    }

    private HConfig createConfig(final JsonObject options) {
        return new ZeroConfig().options(options);
    }
}
