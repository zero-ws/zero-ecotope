package io.zerows.epoch.common.uca.log;

import io.r2mo.typed.cc.Cc;
import io.zerows.ams.util.UtBase;

import java.util.function.Function;

/**
 * @author lang : 2023/4/25
 */
public class LogFactory {

    private static final Cc<String, LogFactory> CC_LOG_FACTORY = Cc.openThread();
    private static final Cc<String, LogModule> CC_LOG_EXTENSION = Cc.open();

    private final String module;

    private LogFactory(final String module) {
        this.module = module;
    }

    static LogFactory create(final String module) {
        return CC_LOG_FACTORY.pick(() -> new LogFactory(module), module);
    }

    // ----------------- 普通体

    /**
     * 「配置日志器」Green普通体
     * 配置化系统专用日志处理器程序，生成基于配置的 {@link LogModule} 专用日志记录器，动态配置系统专用日志器
     *
     * @param type 日志类型，日志类型决定了日志的颜色和输出文字基本信息
     *
     * @return {@link LogModule}
     */
    public LogModule configure(final String type) {
        return this.extension(type, UtBase::rgbGreenN);
    }

    /**
     * 「OSGI日志器」Cyan普通体
     * OSGI 专用系统日志器，通常用于 OSGI 中的日志系统，如 Bundle / Launcher
     *
     * @param type 日志类型，日志类型决定了日志的颜色和输出文字基本信息
     *
     * @return {@link LogModule}
     */
    public LogModule osgi(final String type) {
        return new LogModule(this.module).bind(type).bind(UtBase::rgbCyanN);
    }
    // ----------------- 粗体

    /**
     * 「插件日志器」Cyan粗体
     * zero-equip 扩展模块专用日志器，对应到项目中的所有模块创建的日志系统
     *
     * @param type 日志类型，日志类型决定了日志的颜色和输出文字基本信息
     *
     * @return {@link LogModule}
     */
    public LogModule infix(final String type) {
        return this.extension(type, UtBase::rgbCyanB);
    }

    /**
     * 「扩展模块日志器」Blue粗体
     * zero-extension 扩展模块专用日志器，对应到项目中的所有模块创建的日志系统
     *
     * @param type 日志类型，日志类型决定了日志的颜色和输出文字基本信息
     *
     * @return {@link LogModule}
     */
    public LogModule extension(final String type) {
        return this.extension(type, UtBase::rgbBlueB);
    }

    /**
     * 「云端日志器」Red粗体
     * aeon 系统专用日志器，对应到项目中所有的云端日志系统
     *
     * @param type 日志类型，日志类型决定了日志的颜色和输出文字基本信息
     *
     * @return {@link LogModule}
     */
    public LogModule cloud(final String type) {
        return this.extension(type, UtBase::rgbRedB);
    }


    private LogModule extension(final String type, final Function<String, String> colorFn) {
        return CC_LOG_EXTENSION.pick(
            () -> new LogModule(this.module).bind(type).bind(colorFn),
            this.module + "/" + type);
    }
}
