package io.zerows.cosmic.plugins.security;

import io.r2mo.function.Fn;
import io.r2mo.jce.common.HED;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cosmic.plugins.security.exception._40038Exception400WallDuplicated;
import io.zerows.cosmic.plugins.security.exception._40040Exception400WallKeyMissing;
import io.zerows.cosmic.plugins.security.exception._40078Exception500WallExecutor;
import io.zerows.epoch.annotations.Wall;
import io.zerows.epoch.assembly.DI;
import io.zerows.epoch.assembly.ExtractorEvent;
import io.zerows.epoch.configuration.Inquirer;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.security.SecurityMeta;
import io.zerows.platform.enums.EmApp;
import io.zerows.platform.enums.SecurityType;
import io.zerows.sdk.security.WallExecutor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class InquirerWall implements Inquirer<Set<SecurityMeta>> {

    private static final DI PLUGIN = DI.create(ExtractorEvent.class);

    @Override
    public Set<SecurityMeta> scan(final Set<Class<?>> walls) {
        final Set<SecurityMeta> wallSet = new HashSet<>();
        final Set<Class<?>> wallClass = walls.stream()
            .filter((item) -> item.isAnnotationPresent(Wall.class))
            .collect(java.util.stream.Collectors.toSet());
        if (!wallClass.isEmpty()) {
            final Vertx vertxRef = StoreVertx.of().vertx();
            /*
             * 这意味着已经设置了 @Wall 并且启用了安全配置 wall 类处理自定义验证，在此分支中表示系统成功扫描到合法的 @Wall 注解类，
             * 启用了 Zero 框架中的 Security Extension 安全扩展功能。
             *
             * 注意：zero-extension-rbac 模块中会带有 Security Extension 的扩展功能，并在 Vert.x 4.0 版本中升级标准框架，保
             * 证系统可以直接使用 Zero 框架内置的安全墙类。
             */
            this.verifySpecification(wallClass, vertxRef);

            wallClass.stream().map(clazz -> this.create(clazz, vertxRef))
                .forEach(wallSet::add);
        }
        log.info("[ PLUG ] ( {} Secure ) \uD83E\uDDEC Zero 系统扫描到 {} 个 @Wall 组件", wallSet.size(), wallSet.size());
        return Set.of();
    }

    private SecurityMeta create(final Class<?> clazz, final Vertx vertxRef) {
        final SecurityMeta meta = new SecurityMeta();
        final Wall wall = clazz.getAnnotation(Wall.class);
        meta.setOrder(wall.order());
        meta.setPath(wall.path());

        final SecurityType type = this.verifyConfig(clazz, vertxRef);
        meta.setType(type);

        final WallExecutor executor = PLUGIN.createInstance(clazz);
        meta.setProxy(executor);
        return meta;
    }
    //
    //    private KSecurity create(final Class<?> clazz) {
    //        final KSecurity aegis = new KSecurity();
    //        /*
    //         * 「Validation」
    //         * 1 - Proxy Creation with Wall Specification
    //         * 2 - Wall Type & Aegis Item
    //         ***/
    //        this.verifyProxy(clazz, aegis);
    //
    //        final Annotation annotation = clazz.getAnnotation(Wall.class);
    //        final String typeKey = Ut.invoke(annotation, "value");
    //        this.verifyConfig(clazz, aegis, typeKey);
    //        aegis.setPath(Ut.invoke(annotation, "path"));
    //
    //        /*
    //         * AuthorizationHandler class here
    //         */
    //        final Class<?> handlerCls = Ut.invoke(annotation, "handler");
    //        if (AuthorizationHandler.class.isAssignableFrom(handlerCls)) {
    //            aegis.setHandler(handlerCls);
    //        }
    //        /* Verify */
    //        return aegis;
    //    }
    //

    /**
     * 注，{@link Wall} 类不可以重复定义，此处的重复有两层含义
     * <pre>
     *     规则一：重复法则校验
     *     1. 路径重复
     *     2. 顺序重复
     *     规则二：接口法则校验
     *     标注了 {@link Wall} 的类必须是 {@link WallExecutor} 的实现类，不可以随意定制！
     *     规则三：如果存在 key 值，则校验 key 值对应的配置中的 type 是否合法
     * </pre>
     * 一旦重复定义那么系统将会出现安全墙的二义性法则，于是会导致安全机制失效
     *
     * @param wallClass Wall 类集合
     */
    private void verifySpecification(final Set<Class<?>> wallClass, final Vertx vertxRef) {
        final Set<String> duplicated = new HashSet<>();
        for (final Class<?> clazz : wallClass) {
            Fn.jvmKo(!Ut.isImplement(clazz, WallExecutor.class), _40078Exception500WallExecutor.class, clazz);


            final Wall wall = clazz.getAnnotation(Wall.class);
            final String duplicatedKey = HED.encryptSHA256(wall.order() + wall.path());
            duplicated.add(duplicatedKey);
        }

        Fn.jvmKo(duplicated.size() != wallClass.size(),
            _40038Exception400WallDuplicated.class,
            wallClass.stream().map(Class::getName).collect(Collectors.toSet()));
    }

    private SecurityType verifyConfig(final Class<?> target, final Vertx vertxRef) {
        final Wall wall = target.getAnnotation(Wall.class);
        final HConfig config = NodeStore.findInfix(vertxRef, EmApp.Native.SECURITY);
        if (Objects.isNull(config)) {
            return wall.type();
        }

        final JsonObject configJ = config.options(KName.CONFIG);
        final String configKey = wall.value();
        if (!configJ.containsKey(configKey)) {
            log.warn("[ PLUG ] ( Secure ) Wall `{}` ( path = {} ) 未找到安全配置 key `{}`，您的自定义配置不会生效！",
                target.getName(), wall.path(), configKey);
            return wall.type();
        }
        final JsonObject configData = configJ.getJsonObject(configKey);
        final String typeStr = Ut.valueString(configData, KName.TYPE);
        final SecurityType type = SecurityType.from(typeStr);
        log.info("[ PLUG ] ( Secure ) Wall `{}` ( path = {} ) 使用了安全配置类型 {}", target.getName(), wall.path(), type);

        Fn.jvmKo(Objects.isNull(type), _40040Exception400WallKeyMissing.class, configKey, target);
        return type;
    }
}
