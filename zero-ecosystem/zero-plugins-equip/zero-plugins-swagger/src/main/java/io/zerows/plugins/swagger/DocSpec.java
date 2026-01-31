package io.zerows.plugins.swagger;

import io.r2mo.typed.cc.Cc;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.web.WebEvent;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 规范专用解析处理
 */
public interface DocSpec {

    Cc<String, DocSpec> CC_DOC_SPEC = Cc.openThread();

    static DocSpec of(final Supplier<DocSpec> constructorFn) {
        return CC_DOC_SPEC.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    /**
     * 共享工具：简单的 Java 类型 -> Swagger Schema 映射
     */
    static Schema<?> resolveSchema(final Class<?> type) {
        if (JsonObject.class.isAssignableFrom(type) ||
            JsonArray.class.isAssignableFrom(type) ||
            Map.class.isAssignableFrom(type)) {
            return new Schema<>().type("object");
        }
        if (type == String.class) {
            return new Schema<>().type("string");
        }
        if (type == Integer.class || type == int.class) {
            return new Schema<>().type("integer").format("int32");
        }
        if (type == Long.class || type == long.class) {
            return new Schema<>().type("integer").format("int64");
        }
        if (type == Boolean.class || type == boolean.class) {
            return new Schema<>().type("boolean");
        }
        if (type == Double.class || type == double.class) {
            return new Schema<>().type("number").format("double");
        }
        if (type == Float.class || type == float.class) {
            return new Schema<>().type("number").format("float");
        }
        return new Schema<>().type("object");
    }

    void compile(OpenAPI openAPI, WebEvent event);

    /**
     * 辅助方法：根据 JAX-RS 注解获取 PathItem 中的 Operation（如果不存在则返回 null）
     */
    default Operation getOperation(final PathItem pathItem, final Method method) {
        if (method.isAnnotationPresent(GET.class)) {
            return pathItem.getGet();
        }
        if (method.isAnnotationPresent(POST.class)) {
            return pathItem.getPost();
        }
        if (method.isAnnotationPresent(PUT.class)) {
            return pathItem.getPut();
        }
        if (method.isAnnotationPresent(DELETE.class)) {
            return pathItem.getDelete();
        }
        if (method.isAnnotationPresent(PATCH.class)) {
            return pathItem.getPatch();
        }
        if (method.isAnnotationPresent(HEAD.class)) {
            return pathItem.getHead();
        }
        if (method.isAnnotationPresent(OPTIONS.class)) {
            return pathItem.getOptions();
        }
        return null;
    }
}