package io.zerows.epoch.web;

import io.vertx.core.http.HttpMethod;
import io.zerows.platform.constant.VString;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web API 命名生成器。
 * <p>
 * 作用：将 HTTP 路径 + 方法 转换为系统内部通用的逻辑唯一标识 (Logical Key)。
 * 规则：全小写、点号分隔、参数转义($)。
 * </p>
 */
public class WebApi {

    /**
     * JAX-RS 风格参数正则: {name}
     */
    private static final Pattern PATTERN_JAX_RS = Pattern.compile("\\{([^}]+)\\}");

    /**
     * 根据 WebEvent 生成逻辑标识。
     *
     * @param event WebEvent
     * @return 逻辑名称 (e.g. "api.menus.get")
     */
    public static String nameOf(final WebEvent event) {
        if (event == null) {
            return null;
        }
        return nameOf(event.getPath(), event.getMethod());
    }

    /**
     * 根据路径和方法生成逻辑标识。
     * <p>
     * 示例映射：
     * 1. GET  /hi/beanv/validated      -> hi.beanv.validated.get
     * 2. GET  /hi/beanv/{name}/on      -> hi.beanv.$name.on.get
     * 3. POST /api/{appId}/:type       -> api.$appid.$type.post
     * 4. GET  /                        -> get
     * </p>
     *
     * @param path   原始路径
     * @param method HTTP 方法 (必须存在)
     * @return 逻辑名称 (无后缀)
     */
    public static String nameOf(final String path, final HttpMethod method) {
        // 1. 严格校验：没有 Method 就无法确定唯一性，直接返回 null
        if (method == null) {
            return null;
        }

        // 2. 解析路径部分
        final String prefix = resolvePath(path);
        final String suffix = method.name().toLowerCase(Locale.getDefault());

        // 3. 根路径特判：如果路径解析为空 (即原始路径为 /)，直接返回方法名
        if (prefix == null || prefix.isEmpty()) {
            return suffix;
        }

        // 4. 拼接
        return prefix + VString.DOT + suffix;
    }

    /**
     * [私有] 纯路径解析
     */
    private static String resolvePath(final String path) {
        if (path == null) {
            return null;
        }
        String result = path.trim();

        // 1. 去除开头的 /
        if (result.startsWith(VString.SLASH)) {
            result = result.substring(1);
        }

        // 2. 统一参数风格：JAX-RS {name} -> $name
        final Matcher matcher = PATTERN_JAX_RS.matcher(result);
        if (matcher.find()) {
            result = matcher.replaceAll(VString.DOLLAR + "$1");
        }

        // 3. 统一参数风格：Vert.x :name -> $name
        if (result.contains(VString.COLON)) {
            result = result.replace(VString.COLON, VString.DOLLAR);
        }

        // 4. 路径分隔符转点号：/ -> .
        result = result.replace(VString.SLASH, VString.DOT);

        return result;
    }
}