package io.zerows.epoch.configuration;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.function.Fn;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.spec.YmApp;
import io.zerows.epoch.spec.YmApplication;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.epoch.spec.exception._40001Exception500UpClassArgs;
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
public abstract class ConfigLoadBase implements ConfigLoad {

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
        ConfigLogging.configure(configuration.getLogging());


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
            // 1. ID 填充
            if (StrUtil.isEmpty(app.id())) {
                app.id(inApp.getId());
            }

            // 2. NS (Namespace) 填充，名空间会从 yml 中加载
            if (StrUtil.isNotEmpty(inApp.getNs())) {
                app.ns(inApp.getNs());
            }

            // 3. Tenant 填充
            if (StrUtil.isEmpty(app.tenant())) {
                app.tenant(inApp.getTenant());
            }

            // 4. Config (Option) 配置填充 (通常对象类型判 null)
            app.option(inApp.getConfig());

            // 5. Data 配置填充
            app.data(inApp.getData());
        }
        // -41001 验证
        final YmApplication application = configuration.application();
        Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
            _40001Exception500UpClassArgs.class);
        app.name(application.getName());
    }
}
