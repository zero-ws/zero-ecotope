package io.zerows.extension.skeleton.boot;

import io.r2mo.vertx.jooq.generate.configuration.MetaGenerate;
import io.vertx.core.Vertx;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiConsumer;

/**
 * @author lang : 2025-10-20
 */
@Slf4j
public class ExtensionLauncher {

    private static ExtensionLauncher INSTANCE;
    private final ExtensionGenerate generator;
    private final String[] args;

    private ExtensionLauncher(final Class<?> bootCls, final String[] args) {
        this.generator = new ExtensionGenerate();
        this.args = args;
    }

    public static ExtensionLauncher create(final Class<?> bootCls, final String[] args) {
        if (INSTANCE == null) {
            INSTANCE = new ExtensionLauncher(bootCls, args);
        }
        return INSTANCE;
    }

    /**
     * 以微服务方式启动
     */
    public void startAs(final BiConsumer<Vertx, HConfig> consumer) {

    }

    /**
     * 执行代码生成器
     */
    public void startGenerate(final MetaGenerate metadata) {
        // 提取配置信息，执行代码生成
        this.generator.start(metadata, this.args);
    }
}
