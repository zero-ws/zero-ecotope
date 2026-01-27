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
        // 1. 初始化变量名
        this.whenNames();

        // 2. 加载 Properties 文件到 ENV_VARS (如果存在)
        this.whenDevelopment(clazz);

        // 3. 自动计算
        this.whenAuto();

        // 4. 打印 (数据源自 get() 方法，保证打印出的就是生效的)
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
        // 这里调用的 setEnv 只会写入 ENV_VARS，不再污染 System Property
        devMap.forEach(this::setEnv);
    }

    private void whenAuto() {
        final Map<String, String> autoMap = ENVAuto.whenAuto(
            this::get,
            this::contains
        );
        autoMap.forEach(this::setEnv);
    }

    /**
     * 关键改动：仅仅更新内部缓存和 Name 列表
     * 移除 System.setProperty，防止将配置文件混淆为 JVM 启动参数
     */
    private void setEnv(final String key, final String value) {
        // System.setProperty(key, value); // <--- DELETE THIS
        ENV_VARS.put(key, value);
        NAMES.add(key);
    }

    // ========== Loaders (Public entry) ==========

    private boolean contains(final String key) {
        return NAMES.contains(key);
    }

    // ========== HEnvironment ==========

    /**
     * 核心策略：通过读取顺序决定优先级 (Priority Chain)
     * 1. JVM 参数 (-D) : 永远最高，方便运维紧急覆盖
     * 2. ENV_VARS (配置文件) : 只要文件里定义了，就覆盖系统的
     * 3. System Env (操作系统) : 兜底
     */
    @Override
    public String get(final String key) {
        if (UtBase.isNil(key)) {
            return null;
        }

        // 1. Top Priority: JVM 启动参数 (-Dkey=value)
        // 任何环境(Dev/Prod)下，只要启动命令里带了 -D，必须生效
        String value = System.getProperty(key);
        if (UtBase.isNotNil(value)) {
            return value;
        }

        // 2. High Priority: 配置文件 / 内部缓存
        // 开发环境：Properties 文件会加载到这里，从而覆盖本地脏乱的 OS 环境变量
        // 生产环境：通常没有 PropertySource，或者是空的，这里返回 null，继续往下走
        value = ENV_VARS.get(key);
        if (UtBase.isNotNil(value)) {
            return value;
        }

        // 3. Base Priority: 操作系统环境变量
        // 开发环境：只有当配置文件没写这个 key 时，才读 OS 的
        // 生产环境：因为 ENV_VARS 通常为空，自然而然就读到了 K8s/Docker 注入的 OS 变量
        value = System.getenv(key);
        if (UtBase.isNotNil(value)) {
            // 这里可以回写 ENV_VARS 提升下次读取性能，但不建议回写 System.setProperty
            this.setEnv(key, value);
            return value;
        }

        return null;
    }

    @Override
    public Set<String> vars() {
        return NAMES;
    }

    // ========== Log (保持不变) ==========

    @Override
    @SuppressWarnings("unchecked")
    public ENV vLog() {
        final StringBuilder content = new StringBuilder();
        content.append("\n======= Zero Framework 环境变量 =======\n");
        final HEnvironment environment = ENV.of();

        // 由于 vLog 调用的是 environment.get()，它会严格遵循上面的优先级逻辑
        // 所以打印出来的一定是最终生效的值
        Arrays.stream(EnvironmentVariable.NAMES)
            .filter(StrUtil::isNotEmpty)
            .forEach(name -> {
                final String value = environment.get(name);
                if (value != null) {
                    content.append("\t").append(name).append(" = ").append(value).append("\n");
                }
            });

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
}