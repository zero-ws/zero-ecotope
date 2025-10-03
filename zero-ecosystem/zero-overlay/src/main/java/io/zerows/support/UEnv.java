package io.zerows.support;

import io.r2mo.function.Fn;
import io.zerows.component.shared.program.Kv;
import io.zerows.constant.VString;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UEnv {

    static String reportEnv(final ConcurrentMap<String, String> envs) {
        final Set<String> treeSet = new TreeSet<>(envs.keySet());
        final StringBuilder out = new StringBuilder();
        treeSet.forEach(name -> {
            out.append("\n\t").append(name);
            final String value = envs.get(name);
            out.append(" = ").append(value);
        });
        return out.toString();
    }


    static String readEnv(final String name, final String defaultValue) {
        final String parsed = System.getenv(name);
        return UtBase.isNil(parsed) ? defaultValue : parsed;
    }

    static <T> T readEnv(final String name, final Class<T> clazz) {
        return readEnv(name, null, clazz);
    }

    /*
     * 可直接将字符串的 literal 根据Zero内置序列化子系统反序列化成
     * 想要的数据类型对应的值，主要针对 boolean, int, double 等
     */
    static <T> T readEnv(final String name, final T defaultValue, final Class<T> clazz) {
        final String literal = readEnv(name, VString.EMPTY);
        if (UtBase.isNil(literal)) {
            return defaultValue;
        }

        final T value = TV.vT(literal, clazz);
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        return value;
    }

    /*
     * JDK 9 之后要带上启动参数才允许
     * --add-opens java.base/java.util=ALL-UNNAMED
     * --add-opens java.base/java.lang=ALL-UNNAMED
     */
    @SuppressWarnings("unchecked")
    static ConcurrentMap<String, String> writeEnv(final Properties properties) {
        // 提取环境变量引用
        final Map<String, String> envMap = envMap();
        final ConcurrentMap<String, String> envResult = new ConcurrentHashMap<>();
        if (Objects.isNull(envMap)) {
            return envResult;
        }
        final Enumeration<String> it = (Enumeration<String>) properties.propertyNames();
        while (it.hasMoreElements()) {
            final String name = it.nextElement();
            final String value = properties.getProperty(name);
            // .dev.development （环境变量设置，JDK 11之后带参数执行）
            if (UtBase.isNotNil(value)) {
                envMap.put(name, value);
                envResult.put(name, value);
            }
        }
        return envResult;
    }

    static Kv<String, String> writeEnv(final String name, final String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        final Map<String, String> envMap = envMap();
        if (Objects.isNull(envMap)) {
            return null;
        }
        envMap.put(name, value);
        return Kv.create(name, value);
    }

    @SuppressWarnings("all")
    private static Map<String, String> envMap() {
        return Fn.jvmOr(() -> {
            final Map<String, String> env = System.getenv();
            final Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> mValue = (Map<String, String>) field.get(env);
            // Fix issue: Cannot invoke "java.util.Map.put(Object,Object)" because "envMap" is null
            if (Objects.isNull(mValue)) {
                mValue = new HashMap<>();
            }
            return mValue;
        });
    }
}
