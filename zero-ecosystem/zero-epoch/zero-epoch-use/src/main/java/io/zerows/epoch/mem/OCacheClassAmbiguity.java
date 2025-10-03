package io.zerows.epoch.mem;

import io.zerows.epoch.component.scanner.ClassScanner;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.epoch.sdk.metadata.AbstractAmbiguity;
import io.zerows.epoch.sdk.metadata.uca.Inquirer;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 包扫描器，支持二义性处理，组件数量
 * <pre><code>
 *     1. 非 OSGI 环境，只带单个包扫描组件
 *        内置 static 变量存储了所有扫描的包信息
 *     2. OSGI 环境，每个 Osgi 带有一个包扫描器
 *        内置 static 变量同样存储了所有扫描的包信息
 * </code></pre>
 * Class 对象在底层是唯一的，因此不需要考虑重复添加的问题，也可以直接移出
 *
 * @author lang : 2024-04-17
 */
class OCacheClassAmbiguity extends AbstractAmbiguity implements OCacheClass {
    /**
     * 每个 Bundle x 1，此处证明 Set<Class<?>> 是按 Bundle 执行过分组的，即
     * <pre><code>
     *     bundle-01 = Set<Class<?>>
     *     bundle-02 = Set<Class<?>>
     *     ......
     * </code></pre>
     */
    private final OClassCacheInternal meta;

    OCacheClassAmbiguity(final Bundle bundle) {
        super(bundle);
        this.meta = OClassCacheInternal.of();
        // Scanner
        final ClassScanner scanner = ClassScanner.of();
        final Set<Class<?>> scanned = scanner.scan(bundle);
        this.logger().info("Zero system scanned `{0}` classes in total. MetaTree = {1}",
            String.valueOf(scanned.size()), String.valueOf(this.meta.hashCode()));
        this.meta.add(scanned);
    }

    static Set<OClassCacheInternal> META() {
        final Set<OClassCacheInternal> metaSet = new HashSet<>();
        OCacheClass.CC_SKELETON.get().values().forEach(self -> {
            if (self instanceof final OCacheClassAmbiguity cache) {
                metaSet.add(cache.meta);
            }
        });
        return metaSet;
    }

    /* 已经是全局模式 */
    @Override
    public Set<String> keys() {
        return this.meta.get().stream()
            .map(Class::getName)
            .collect(Collectors.toSet());
    }

    /* 读取必须是全局 */
    @Override
    public Set<Class<?>> value() {
        return this.meta.get();
    }

    @Override
    public Set<Class<?>> value(final VertxComponent type) {
        return this.meta.get(type);
    }

    @Override
    public VertxComponent valueType(final Class<?> clazz) {
        VertxComponent type = this.meta.getType(clazz);
        if (Objects.isNull(type)) {
            this.logger().info("Could not extract type from bundle, try to parse from Global Data. class = \"{}\"",
                clazz.getName());
            type = OCacheClass.entireType(clazz);
        }
        return type;
    }

    @Override
    public OCacheClass add(final Set<Class<?>> classes) {
        this.meta.add(classes);
        this.logger().info("Added \"{}\" classes into current bundle.", String.valueOf(classes.size()));
        return this;
    }

    @Override
    public OCacheClass remove(final Set<Class<?>> classes) {
        this.meta.remove(classes);
        this.logger().info("Removed \"{}\" classes from current bundle.", String.valueOf(classes.size()));
        return this;
    }

    @Override
    public OCacheClass compile(final VertxComponent type, final Function<Set<Class<?>>, Set<Class<?>>> compiler) {
        if (Objects.isNull(this.caller())) {
            this.logger().info("Scanned \"{}\" of typed classes from current environment.",
                type.name());
        } else {

            this.logger().info("Scanned \"{}\" of typed classes from current bundle. owner = {}",
                type.name(), this.caller().getSymbolicName());
        }
        this.meta.compile(type, compiler);
        return this;
    }

    /**
     * 全局类存储池，用于存储当前环境所有的类相关信息，此处的存储考虑几点
     * <pre><code>
     *     提供基础存储哈希表，根据当前环境中是否存在 Osgi 对内容进行提取
     *     1. OSGI 环境
     *        DEFAULT_SCANNED = OClassCacheInternal 的全环境
     *     2. OSGI 环境
     *        Osgi 01 = OClassCacheInternal
     *        Osgi 02 = OClassCacheInternal
     * </code></pre>
     *
     * @author lang : 2024-04-19
     */
    static class OClassCacheInternal {

        private final Set<Class<?>> classSet = new HashSet<>();
        private final ConcurrentMap<VertxComponent, Set<Class<?>>> classMap = new ConcurrentHashMap<>();

        private OClassCacheInternal() {
        }

        static OClassCacheInternal of() {
            return new OClassCacheInternal();
        }

        void add(final Set<Class<?>> classes) {
            this.classSet.addAll(classes);
        }

        void addBy(final VertxComponent type, final Set<Class<?>> classes) {
            final Set<Class<?>> stored = this.classMap.getOrDefault(type, new HashSet<>());
            stored.addAll(classes);
            this.classMap.put(type, stored);
        }

        /**
         * 特殊方法，可直接绑定
         * {@link Inquirer#scan(Set)} 实现类对应的方法集，最终可保证结果的正确性，且扫描过程
         * 一直在扫描对应的信息，不会重复扫描
         *
         * @param type     类型
         * @param compiler 编译器
         */
        void compile(final VertxComponent type, final Function<Set<Class<?>>, Set<Class<?>>> compiler) {
            this.addBy(type, compiler.apply(this.classSet));
        }

        Set<Class<?>> get() {
            return this.classSet;
        }

        Set<Class<?>> get(final VertxComponent type) {
            return this.classMap.getOrDefault(type, new HashSet<>());
        }

        VertxComponent getType(final Class<?> clazz) {
            for (final VertxComponent type : this.classMap.keySet()) {
                if (this.classMap.get(type).contains(clazz)) {
                    return type;
                }
            }
            return null;
        }

        void remove(final VertxComponent type) {
            final Set<Class<?>> removed = this.classMap.remove(type);

            this.classSet.removeAll(removed);
        }

        void remove(final Class<?> clazz) {
            // 基础删除
            this.classSet.remove(clazz);
            // 类型中的删除
            this.removeInternal(clazz);
        }

        void remove(final Set<Class<?>> classes) {
            // 基础删除
            this.classSet.removeAll(classes);
            // 类型中的删除
            classes.forEach(this::removeInternal);
        }

        private void removeInternal(final Class<?> clazz) {
            this.classMap.forEach((type, classStored) -> {
                final Set<Class<?>> storedSet = this.classMap.get(type);
                storedSet.remove(clazz);
                this.classMap.put(type, storedSet);
            });
        }
    }
}
