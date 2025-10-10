package io.zerows.epoch.boot;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.YmApp;
import io.zerows.epoch.basicore.YmApplication;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.exception._41001Exception500AppNameMissing;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
abstract class ConfigLoadBase implements ConfigLoad {
    protected static final JUtil UT = SPI.V_UTIL;

    /**
     * 步骤说明
     * <pre>
     *     1. 步骤一：
     *        反向填充 {@link HApp} 的数据信息
     *          app:
     *            id:
     *            tenant:
     *            data:
     *            config:
     *        上述数据用于填充 {@link HApp} 对象，数据来源于 {@link YmConfiguration} 中的 app 节点
     *     2. 步骤二：日志补充
     * </pre>
     *
     * @param configuration 原始配置
     * @param app           应用信息
     */
    protected YmConfiguration completeConfiguration(final YmConfiguration configuration,
                                                    final JObject parsedJ,
                                                    final HApp app) {
        if (Objects.isNull(configuration)) {
            throw new _500ServerInternalException("[ ZERO ] 配置解析异常，无法继续进行，请检查配置内容！");
        }
        // 步骤一：反向填充 app 信息
        this.completeApp(configuration, app);


        // 步骤二：日志补充
        ZeroLogging.configure(configuration.getLogging());


        // 步骤三：扩展节点填充
        this.completeExtension(configuration, parsedJ);
        log.debug("[ ZERO ] 加载的配置内容：\n{}", parsedJ.encodePretty());
        return configuration;
    }

    private void completeExtension(final YmConfiguration configuration, final JObject parsed) {
        final Set<String> fieldSet = Arrays.stream(configuration.getClass().getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(JsonIgnore.class))
            .filter(field -> !Modifier.isPublic(field.getModifiers()))
            .map(Field::getName).collect(Collectors.toSet());

        final JsonObject rawJ = parsed.data();
        rawJ.fieldNames().stream()
            .filter(fieldName -> !fieldSet.contains(fieldName))
            .forEach(fieldName -> {
                final JsonObject configJ = rawJ.getJsonObject(fieldName);
                if (Ut.isNotNil(configJ)) {
                    configuration.put(fieldName, configJ);
                }
            });
    }

    private void completeApp(final YmConfiguration configuration, final HApp app) {
        // 步骤一：反向填充 app 信息
        final YmApp inApp = configuration.getApp();
        if (Objects.nonNull(inApp)) {
            // id, tenant, name, ns
            app.id(inApp.getId());
            app.tenant(inApp.getTenant());
            // config 配置
            app.option(inApp.getConfig());
            // data 配置
            app.data(inApp.getData());
        }
        // -41001 验证
        final YmApplication application = configuration.application();
        Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
            _41001Exception500AppNameMissing.class);
        app.name(application.getName());
    }
}
