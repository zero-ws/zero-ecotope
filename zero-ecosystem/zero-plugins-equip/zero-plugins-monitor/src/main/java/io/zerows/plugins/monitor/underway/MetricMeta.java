package io.zerows.plugins.monitor.underway;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.assembly.ClassScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-30
 */
@SuppressWarnings("all")
class MetricMeta {
    private static final ClassScanner SCANNER = ClassScanner.of();
    private static final Set<Class<?>> SCANNED = SCANNER.scan(null);

    static Map<Field, Cc> mapOfCc() {
        final Map<Field, Cc> refs = new HashMap<>();
        SCANNED.forEach(clazz -> {
            // getDeclaredFields 能拿到 private 字段，也能拿到 interface 的常量
            final Field[] fields = clazz.getDeclaredFields();

            for (final Field field : fields) {
                // 2. 类型检查：只看 Cc 类型
                if (!Cc.class.isAssignableFrom(field.getType())) {
                    continue;
                }

                // 3. 【关键】静态检查！
                // 无论是 Interface 还是 Class，只有 static 字段才能通过 field.get(null) 读取
                if (!Modifier.isStatic(field.getModifiers())) {
                    continue; // 跳过实例变量，防止 NPE
                }

                try {
                    // 4. 暴力反射，允许访问 private static
                    field.setAccessible(true);

                    // 5. 读取静态变量的值 (obj 传 null)
                    Cc ccValue = (Cc) field.get(null);

                    // 6. 存入 Map (排除空值)
                    if (ccValue != null) {
                        refs.put(field, ccValue);
                    }
                } catch (IllegalAccessException e) {
                    // 建议记录日志，或者简单吞掉
                    e.printStackTrace();
                }
            }
        });
        return refs;
    }
}
