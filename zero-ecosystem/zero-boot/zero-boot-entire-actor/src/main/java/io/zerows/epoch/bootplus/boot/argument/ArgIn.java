package io.zerows.epoch.bootplus.boot.argument;

import io.zerows.epoch.program.Ut;
import io.zerows.platform.HEnvironmentVariable;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.Environment;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 参数提取，数据加载类参数，该类为抽象类，用于加载参数列表，并解析成对应的数据类型
 * 内置带有 {@link java.util.Map} 参数列表以及核心环境参数，作为开发、测试、生产运行
 * 最核心的参数数据结构。<br/>
 * 此数据结构中，子类必须提供参数表相关信息，并且保证子类在构造时已完成了变量的核心定义
 *
 * @author lang : 2023-06-11
 */
public abstract class ArgIn {

    private Environment environment;

    /**
     * 根据子类提供的两个核心方法解析流程
     * <pre><code>
     *     1. {@link ArgIn#names()}
     *        定义了参数名称和相关顺序
     *     2. {@link ArgIn#definition()}
     *        定义了参数的基本规范
     * </code></pre>
     */
    protected ArgIn() {

    }

    protected void initialize(final String[] args) {
        final List<String> names = this.names();
        for (int idx = VValue.IDX; idx < names.size(); idx++) {
            if (idx < args.length) {
                final String name = names.get(idx);
                final String value = args[idx];
                final ArgVar var = this.definition().get(name);
                var.value(this.value(var, value));
            }
        }
        {
            // 环境变量计算
            final String envValue = Ut.envWith(HEnvironmentVariable.ZERO_ENV, Environment.Production.name());
            this.environment = Ut.toEnum(envValue, Environment.class);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T value(final ArgVar var, final String value) {
        final Class<?> type = var.type();
        if (Environment.class == type) {
            return (T) Ut.toEnum(value, Environment.class);
        } else {
            return Ut.valueT(value, type);
        }
    }

    /**
     * 对应输入的顺序转换成对应的参数列表
     * <pre><code>
     *     names[0] = args[0]
     *     names[1] = args[1]
     *     names[2] = args[2]
     * </code></pre>
     *
     * @return {@link java.util.List}
     */
    protected List<String> names() {
        return List.of();
    }

    /**
     * 对应的参数基础规范，规范中会包含如下：
     * <pre><code>
     *     1. name - 参数名
     *     2. type - 参数类型
     *     3. value - 参数值（可以根据默认值计算）
     * </code></pre>
     *
     * @return {@link java.util.concurrent.ConcurrentMap}
     */
    protected ConcurrentMap<String, ArgVar> definition() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 子类必须实现的方法，用于提取单个参数值专用
     *
     * @param name 参数名
     * @param <T>  泛型标记
     *
     * @return {@link T}
     */
    public abstract <T> T value(final String name);

    public ArgIn environment(final Environment environment) {
        this.environment = environment;
        return this;
    }

    public Environment environment() {
        return this.environment;
    }
}
