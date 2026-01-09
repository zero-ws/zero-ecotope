package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.annotations.Adjust;
import io.zerows.epoch.annotations.Codex;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.assembly.exception._40005Exception500EventSource;
import io.zerows.epoch.assembly.exception._40036Exception500CodexMore;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.support.Ut;
import jakarta.ws.rs.Path;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Scanned @EndPoint clazz to build Event metadata
 * 强校验版：发现重复路由定义将打印错误信息，且冲突的 Event 均不加入环境
 */
@Slf4j
public class ExtractorEvent implements Extractor<Set<WebEvent>> {

    @Override
    public Set<WebEvent> extract(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return new HashSet<>();
        }
        // 1. Class verify
        this.verify(clazz);
        // 2. Check whether clazz annotated with @PATH
        if (clazz.isAnnotationPresent(Path.class)) {
            // 3.1. Append Root Path
            final Path path = this.path(clazz);
            assert null != path : "Path should not be null.";
            return this.extract(clazz, ExtractToolPath.resolve(path));
        } else {
            // 3.2. Use method Path directly
            return this.extract(clazz, null);
        }
    }

    private void verify(final Class<?> clazz) {
        if (!clazz.isInterface()) {
            ExtractTool.verifyNoArgConstructor(clazz);
        }
        ExtractTool.verifyIfPublic(clazz);
        if (!clazz.isAnnotationPresent(EndPoint.class)) {
            throw new _40005Exception500EventSource(clazz);
        }
    }

    @SuppressWarnings("all")
    private Set<WebEvent> extract(final Class<?> clazz, final String root) {
        final Method[] methods = clazz.getDeclaredMethods();

        // 1. Validate Codex annotation appears (RxJava logic)
        final Long counter = Observable.fromArray(methods)
            .map(Method::getParameterAnnotations)
            .flatMap(Observable::fromArray)
            .map(Arrays::asList)
            .map(item -> item.stream().map(Annotation::annotationType).collect(Collectors.toList()))
            .filter(item -> item.contains(Codex.class))
            .count().blockingGet();
        Fn.jvmKo(methods.length < counter, _40036Exception500CodexMore.class, clazz);

        // 🚀 2. 第一阶段：收集该类下所有合法的 WebEvent 到 List（不提前去重）
        final List<WebEvent> scannedEvents = Arrays.stream(methods)
            .filter(ExtractToolMethod::isValid)
            .map(item -> this.extract(item, root))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 🚀 3. 第二阶段：根据 (Method + Path + Order) 进行分组，检测类内冲突
        final Map<String, List<WebEvent>> grouped = scannedEvents.stream()
            .collect(Collectors.groupingBy(event -> {
                // 构造逻辑指纹 Key，确保 Path 归一化（转大写、去空格、去尾斜杠已在 WebEvent 内部或此处处理）
                return (event.getMethod() + " " + event.getPath() + " " + event.getOrder()).toUpperCase();
            }));

        // 🚀 4. 第三阶段：执行过滤逻辑
        final Set<WebEvent> result = new HashSet<>();
        grouped.forEach((key, list) -> {
            if (list.size() > 1) {
                // 发现重复！
                this.logConflict(clazz, list);
            } else {
                // 唯一项：安全加入
                result.add(list.get(0));
            }
        });

        return result;
    }

    /**
     * 打印简短的冲突警告（单行）
     */
    private void logConflict(final Class<?> clazz, final List<WebEvent> conflicts) {
        final WebEvent sample = conflicts.getFirst();
        // 提取所有冲突的方法名，用逗号分隔
        final String methodNames = conflicts.stream()
            .map(e -> e.getAction().getName() + "()")
            .collect(Collectors.joining(", "));

        // 单行输出核心冲突点
        log.error("[ ZERO ] ❌ 路由冲突 (已忽略): 类 {}, 坐标 [{}]{}:{}, 涉及方法: [{}]",
            clazz.getSimpleName(),         // 简写类名更清晰
            sample.getMethod(),
            sample.getPath(),
            sample.getOrder(),
            methodNames);
    }

    /**
     * Scan for single
     *
     * @param method single method that will be scanned.
     * @param root   root path calculation
     * @return Standard Event object
     */
    private WebEvent extract(final Method method, final String root) {
        final WebEvent event = new WebEvent();
        final HttpMethod httpMethod = ExtractToolMethod.resolve(method);
        if (null == httpMethod) {
            log.warn("[ ZEOR ] \u001b[0;31m!!!!!, Missed HttpMethod annotation for method\u001b[m ? (GET,POST,PUT,...). method = \u001b[0;31m{}\u001b[m", method);
            return null;
        } else {
            event.setMethod(httpMethod);
        }

        // Path Resolve
        final Path path = this.path(method);
        if (null == path) {
            if (!Ut.isNil(root)) {
                event.setPath(root);
            }
        } else {
            final String result = ExtractToolPath.resolve(path, root);
            event.setPath(result);
        }

        event.setAction(method);
        event.setConsumes(ExtractToolMedia.consumes(method));
        event.setProduces(ExtractToolMedia.produces(method));
        event.setProxy(method.getDeclaringClass());

        // Order Resolve
        if (method.isAnnotationPresent(Adjust.class)) {
            final Adjust adjust = method.getDeclaredAnnotation(Adjust.class);
            event.setOrder(adjust.value());
        }
        return event;
    }

    private Path path(final Class<?> clazz) {
        return this.path(clazz.getDeclaredAnnotation(Path.class));
    }

    private Path path(final Method method) {
        return this.path(method.getDeclaredAnnotation(Path.class));
    }

    private Path path(final Annotation anno) {
        return (anno instanceof Path) ? (Path) anno : null;
    }
}