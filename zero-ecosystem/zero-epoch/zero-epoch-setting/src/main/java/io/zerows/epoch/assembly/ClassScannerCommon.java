package io.zerows.epoch.assembly;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 扫描加速版 🚀 (Powered by ClassGraph)
 *
 * 核心改进：
 * 1. 解决了在非 URLClassLoader 环境下（如 Zero/Vert.x 工具启动时）扫描不到类的问题。
 * 2. 利用 ClassGraph 底层多线程扫描。
 * 3. 保持原有的“静默加载”和“最终过滤”逻辑。
 */
@Slf4j
@SuppressWarnings("all")
class ClassScannerCommon implements ClassScanner {

    /** 并发结果集合 */
    private static Set<Class<?>> newConcurrentSet() {
        return ConcurrentHashMap.newKeySet();
    }

    @Override
    public Set<Class<?>> scan(final HBundle bundle) {
        final long t0 = System.nanoTime();
        final Set<Class<?>> loaded = newConcurrentSet();

        // 获取黑名单配置 (假设 ClassFilterPackage.SKIP_PACKAGE 是 String[] 或 List<String>)
        // ClassGraph 的 rejectPackages 支持 String... 变长参数
        String[] skipPackages = ClassFilterPackage.SKIP_PACKAGE;

        int totalTopLevel = 0;

        // 配置 ClassGraph
        // .enableClassInfo() : 必须开启以获取类信息
        // .rejectPackages()  : 在扫描底层直接剔除黑名单包，性能远高于加载后过滤
        // .ignoreClassVisibility() : 扫描所有修饰符的类
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .rejectPackages(skipPackages)
            .ignoreClassVisibility()
            .scan()) {

            // 获取所有扫描到的类信息（此时并未加载 Class 对象）
            var allClassInfo = scanResult.getAllClasses();
            totalTopLevel = allClassInfo.size();

            // 使用并行流进行真正的类加载（保持你原有的异常处理逻辑）
            StreamSupport.stream(allClassInfo.spliterator(), true).unordered()
                .forEach(ci -> {
                    try {
                        // loadClass() 会使用扫描时检测到的正确 ClassLoader
                        final Class<?> cls = ci.loadClass();
                        loaded.add(cls);
                    } catch (Throwable e) {
                        // 保持原逻辑：静默处理依赖缺失或加载错误
                        // ClassGraph 的 loadClass() 可能会抛出 IllegalArgumentException 如果依赖缺失
                    }
                });

        } catch (Exception e) {
            log.warn("[ ZERO ] ClassGraph 扫描过程发生异常", e);
        }

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