package io.zerows.epoch.assembly;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.r2mo.function.Fn;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 扫描加速版 🚀（Trie 前缀匹配 + 包级跳过缓存 + 并行流 + 无锁集合）
 *
 * 语义保持不变：
 * - 全量发现顶级类 → 黑名单前缀跳过（包名 startsWith）→ 并行装载 → 最终 ClassFilter::isValid 过滤
 * - 不指定“只扫描哪些包”，仅用黑名单跳过
 * - 仅输出一条总览日志
 */
@Slf4j
@SuppressWarnings("all")
class ClassScannerCommon implements ClassScanner {

    /** 黑名单前缀匹配器（去重/去冗余后构建 Trie） */
    private static final ClassMatcherTrie SKIP_MATCHER =
        ClassMatcherTrie.compile(ClassFilterPackage.SKIP_PACKAGE);

    /** 包名 -> 是否跳过 的缓存，避免对同包反复匹配（并发场景下命中更高） */
    private static final ConcurrentMap<String, Boolean> SKIP_CACHE = new ConcurrentHashMap<>(4096);

    /** 并发结果集合（比 Collections.synchronizedSet 更少锁竞争） */
    private static Set<Class<?>> newConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final long t0 = System.nanoTime();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Set<Class<?>> loaded = newConcurrentSet();

        final int totalTopLevel = Fn.jvmOr(() -> {
            int total = 0;
            try {
                final ClassPath cp = ClassPath.from(loader);
                final ImmutableSet<ClassPath.ClassInfo> all = cp.getTopLevelClasses();
                total = all.size();

                // 并行 + 无序，保持你原先的 computeIfAbsent 方案（在你的环境里更快）
                StreamSupport.stream(all.spliterator(), true).unordered()
                    .filter(ci -> {
                        final String pkg = ci.getPackageName();
                        return !SKIP_CACHE.computeIfAbsent(pkg, SKIP_MATCHER::matches);
                    })
                    .forEach(ci -> {
                        try {
                            final Class<?> cls = loader.loadClass(ci.getName());
                            loaded.add(cls);
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            // 静默：依赖不可达
                        } catch (Exception e) {
                            // 静默：其它受检/运行时异常
                        }
                        // 不额外处理 LinkageError（按你的要求）
                    });
            } catch (Exception ignore) {
                // 保持扫描不中断
            }
            return total;
        });

        // 最终合法性过滤（并行）—— 与旧版保持一致
        final Set<Class<?>> result = loaded.parallelStream()
            .filter(ClassFilter::isValid)
            .collect(Collectors.toCollection(ClassScannerCommon::newConcurrentSet));

        final long t1 = System.nanoTime();
        log.info("[ ZERO ] 扫描完成：{}/{}，总耗时={} ms 📊",
            result.size(), totalTopLevel, (t1 - t0) / 1_000_000L);

        return result;
    }
}
