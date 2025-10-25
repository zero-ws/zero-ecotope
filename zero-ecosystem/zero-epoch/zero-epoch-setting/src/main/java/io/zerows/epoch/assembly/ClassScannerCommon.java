package io.zerows.epoch.assembly;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.r2mo.function.Fn;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 扫描加速版：
 * 1) 前缀匹配器预编译 + 去冗余
 * 2) 跳过结果缓存（包名 -> 是否跳过）
 * 3) 全流程并行 + 无锁集合
 * 4) 最后再做 ClassFilter 校验
 */
@Slf4j
@SuppressWarnings("all")
class ClassScannerCommon implements ClassScanner {

    /** 去冗余后的前缀匹配器（只在类加载前判一次） */
    private static final PrefixMatcher SKIP_MATCHER = PrefixMatcher.compile(ClassFilterPackage.SKIP_PACKAGE);

    /** 包名 -> 是否跳过 的缓存，避免重复 startsWith */
    private static final ConcurrentMap<String, Boolean> SKIP_CACHE = new ConcurrentHashMap<>(4096);

    /** 并发结果集合（比 synchronizedSet 更少锁竞争） */
    private static Set<Class<?>> newConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        final Set<Class<?>> classSet = newConcurrentSet();

        Fn.jvmAt(() -> {
            final ClassPath cp = ClassPath.from(loader);
            final ImmutableSet<ClassPath.ClassInfo> all = cp.getTopLevelClasses();

            log.info("[ZERO] Skip roots: {}, (deduped: {})",
                ClassFilterPackage.SKIP_PACKAGE.length, SKIP_MATCHER.size());

            // 先过滤包，再并行装载
            all.parallelStream() // 并行遍历
                .map(ci -> ci)   // 保留 ClassInfo
                .filter(ci -> {
                    final String pkg = ci.getPackageName();
                    // 包级跳过判定带缓存
                    return !SKIP_CACHE.computeIfAbsent(pkg, SKIP_MATCHER::matches);
                })
                .forEach(ci -> {
                    try {
                        System.out.println(ci.getPackageName());
                        // 避免 ClassInfo.load() 的内部异常包装，直接走 loader
                        final Class<?> cls = loader.loadClass(ci.getName());
                        classSet.add(cls);
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // 依赖可达性问题：忽略但不打印，保持扫描快速安静
                    } catch (LinkageError e) {
                        // 版本冲突/重复类等链接错误，同样跳过
                    } catch (Throwable t) {
                        // 兜底，避免扫描中断；如需排障可改为 debug 级别打印
                    }
                });
        });

        // 最终合法性过滤（并行）
        return classSet.parallelStream()
            .filter(ClassFilter::isValid)
            .collect(Collectors.toCollection(ClassScannerCommon::newConcurrentSet));
    }

    // ------------------------------------------------------------
    //              高性能前缀匹配器（去冗余 + 线性匹配）
    // ------------------------------------------------------------
    private static final class PrefixMatcher {
        private final String[] roots; // 去冗余后的根前缀，已按长度升序

        private PrefixMatcher(String[] roots) {
            this.roots = roots;
        }

        /** 构建时：去重、去冗余（如果存在 'org.apache' 就移除 'org.apache.xxx'）并按长度升序 */
        static PrefixMatcher compile(String[] raw) {
            if (raw == null || raw.length == 0) return new PrefixMatcher(new String[0]);

            // 去重
            final Set<String> uniq = new HashSet<>(raw.length * 2);
            for (String s : raw) {
                if (s != null && !s.isEmpty()) {
                    uniq.add(s.trim());
                }
            }
            // 升序（长度 + 字典序），便于做“是否被更短前缀覆盖”的判断
            final List<String> list = new ArrayList<>(uniq);
            list.sort((a, b) -> {
                int la = a.length(), lb = b.length();
                return la == lb ? a.compareTo(b) : Integer.compare(la, lb);
            });

            // 去冗余：如果当前前缀被已选更短前缀覆盖，则丢弃
            final List<String> dedup = new ArrayList<>(list.size());
            outer:
            for (String p : list) {
                for (String kept : dedup) {
                    if (p.startsWith(kept)) {
                        // p 被 kept 覆盖，丢弃
                        continue outer;
                    }
                }
                dedup.add(p);
            }

            return new PrefixMatcher(dedup.toArray(new String[0]));
        }

        /** 是否匹配任一根前缀（线性扫描，对已压缩后的 roots 性能足够） */
        boolean matches(String pkgOrClass) {
            if (pkgOrClass == null || pkgOrClass.isEmpty()) return true;
            for (String r : roots) {
                if (pkgOrClass.startsWith(r)) return true;
            }
            return false;
        }

        int size() {
            return roots.length;
        }
    }
}
