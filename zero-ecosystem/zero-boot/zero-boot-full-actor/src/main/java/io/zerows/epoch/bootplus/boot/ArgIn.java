package io.zerows.epoch.bootplus.boot;

import io.r2mo.base.program.R2Var;
import io.r2mo.base.program.R2VarSet;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.enums.Environment;
import io.zerows.support.Ut;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     *     2. {@link ArgIn#varSet()}
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
                final R2Var variable = this.varSet().get(name);
                Objects.requireNonNull(variable, "[ ZERO ] 未找到指定的变量定义：" + name);
                final Object valueFor = this.getValue(variable, value);
                variable.value(valueFor);
            }
        }
        {
            // 环境变量计算
            final String envValue = ENV.of().get(EnvironmentVariable.Z_ENV, Environment.Production.name());
            this.environment = Ut.toEnum(envValue, Environment.class);
        }
    }

    @SuppressWarnings("all")
    private <T> T getValue(final R2Var variable, final String value) {
        final Class<?> type = variable.type();
        if (type.isEnum()) {
            return (T) Ut.toEnum(value, (Class<Enum>) type);
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

    protected R2VarSet varSet() {
        return R2VarSet.of();
    }

    /**
     * 子类必须实现的方法，用于提取单个参数值专用
     *
     * @param name 参数名
     * @param <T>  泛型标记
     *
     * @return {@link T}
     */
    public <T> T value(final String name) {
        return Optional.ofNullable(this.varSet())
            .map(set -> set.get(name))
            .map(R2Var::<T>value)
            .orElse(null);
    }

    public ArgIn environment(final Environment environment) {
        this.environment = environment;
        return this;
    }

    public Environment environment() {
        return this.environment;
    }
}
