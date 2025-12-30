package io.zerows.platform;

import cn.hutool.core.util.StrUtil;
import io.zerows.platform.annotations.PropertySource;
import io.zerows.specification.configuration.HEnvironment;
import io.zerows.specification.development.HLog;
import io.zerows.support.base.UtBase;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-05
 */
@Slf4j
public class ENV implements HEnvironment, HLog {

    private static final ConcurrentMap<String, String> ENV_VARS = new ConcurrentHashMap<>();
    private static final Set<String> NAMES = new TreeSet<>();
    private static final ENV INSTANCE = new ENV();
    private static final ENVDev DEV = ENVDev.of();

    private ENV() {
        // 私有构造函数，防止外部实例化
    }

    public static ENV of() {
        return INSTANCE;
    }

    // ========== Lifecycle ==========

    public static String parseVariable(final String wrapValue) {
        final ENV env = ENV.of();
        return ENVAuto.parseEnv(wrapValue, env::get);
    }

    public void whenStart(final Class<?> clazz) {
        /*
         * 先初始化环境变量
         * - Z_
         * - AEON_
         * - R2MO_
         */
        this.whenNames();

        // 开发环境变量加载
        this.whenDevelopment(clazz);

        // [新增] 自动计算 JDBC URL
        this.whenAuto();

        // 打印环境变量，只打印 ZERO 相关的变量
        this.vLog();
    }

    private void whenNames() {
        NAMES.addAll(ENVAuto.whenAuto());
    }

    private void whenDevelopment(final Class<?> clazz) {
        final PropertySource source = clazz.getDeclaredAnnotation(PropertySource.class);
        if (Objects.isNull(source)) {
            return;
        }
        final Map<String, String> devMap = new HashMap<>();
        for (final String path : source.value()) {
            devMap.putAll(DEV.envLoad(path));
        }
        devMap.forEach(this::setEnv);
    }

    private void whenAuto() {
        final Map<String, String> autoMap = ENVAuto.whenAuto(
            this::get,
            this::contains
        );
        autoMap.forEach(this::setEnv);
    }

    private void setEnv(final String key, final String value) {
        System.setProperty(key, value);
        ENV_VARS.put(key, value);
        NAMES.add(key);
    }

    // ========== Loaders (Public entry) ==========

    /**
     * 辅助判断是否存在 key
     */
    private boolean contains(final String key) {
        return NAMES.contains(key);
    }

    // ========== HEnvironment ==========

    @Override
    public String get(final String key) {
        if (UtBase.isNil(key)) {
            return null;
        }

        final String envValue = System.getenv(key);
        if (UtBase.isNotNil(envValue)) {
            this.setEnv(key, envValue);     // 回写保障！
            return envValue;
        }

        final String sysValue = System.getProperty(key);
        if (UtBase.isNotNil(sysValue)) {
            this.setEnv(key, sysValue);     // 回写保障！
            return sysValue;
        }
        return ENV_VARS.get(key);
    }

    @Override
    public Set<String> vars() {
        return NAMES;
    }

    // ========== 特殊处理：增强版变量解析 ==========

    // 环境变量打印专用
    @Override
    @SuppressWarnings("unchecked")
    public ENV vLog() {
        final StringBuilder content = new StringBuilder();
        content.append("\n======= Zero Framework 环境变量 =======\n");

        final HEnvironment environment = ENV.of();

        // 1. 打印 EnvironmentVariable.NAMES 中定义的标准变量
        Arrays.stream(EnvironmentVariable.NAMES)
            .filter(StrUtil::isNotEmpty)
            .forEach(name -> {
                final String value = environment.get(name);
                if (value != null) {
                    content.append("\t").append(name).append(" = ").append(value).append("\n");
                }
            });

        // 2. 额外打印自动生成的 URL (因为 NAMES 数组中不包含 URL 的常量定义，只有 INSTANCE)
        // 使用常量 EnvironmentVariable.DBS_URL / DBW_URL / DBH_URL
        Arrays.asList(
            EnvironmentVariable.DBS_URL,
            EnvironmentVariable.DBW_URL,
            EnvironmentVariable.DBH_URL
        ).forEach(name -> {
            final String value = environment.get(name);
            if (value != null) {
                content.append("\t").append(name).append(" = ").append(value).append("\n");
            }
        });

        content.append("======================================\n");
        final String message = content.toString();
        log.info("[ ZERO ] 运行环境：{}", message);
        return this;
    }
    // ========== Reporter ==========
}