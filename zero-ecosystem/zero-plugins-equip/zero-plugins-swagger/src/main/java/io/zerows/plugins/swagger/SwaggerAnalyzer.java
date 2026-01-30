package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

class SwaggerAnalyzer {
    private static final OCacheActor ACTOR = OCacheActor.of();

    static OpenAPI compile(final SwaggerConfig doc) {
        final OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(new Components());
        openAPI.setPaths(new Paths());

        // 1. 基础信息
        final SwaggerConfig.ApiDocs ymOpen = doc.getApiDocs();
        final Info info = new Info()
            .title(ymOpen.getTitle())
            .version(ymOpen.getVersion())
            .description(ymOpen.getDescription());
        openAPI.setInfo(info);

        // 2. 扫描所有 WebEvent
        final Set<WebEvent> events = ACTOR.value().getEvents();
        for (final WebEvent event : events) {
            analyzeEvent(openAPI, event);
        }

        return openAPI;
    }

    private static void analyzeEvent(final OpenAPI openAPI, final WebEvent event) {
        final Method method = event.getAction();
        if (method == null) {
            return;
        }

        // 1. 获取路径 (WebEvent 已经计算了完整的路径，直接使用)
        final String path = event.getPath();
        PathItem pathItem = openAPI.getPaths().get(path);
        if (pathItem == null) {
            pathItem = new PathItem();
            openAPI.getPaths().addPathItem(path, pathItem);
        }

        // 2. 构建 Operation
        final Operation operation = new Operation();

        // --- A. 基础元数据 (因为没有 Swagger 注解，使用代码元数据兜底) ---
        operation.setSummary(method.getName()); // 方法名作为摘要
        // 使用接口名作为 Tag (例如 "AppAgent")
        final String tagName = method.getDeclaringClass().getSimpleName();
        operation.addTagsItem(tagName);

        // --- B. 参数解析 (@HeaderParam, @BodyParam 等) ---
        parseParameters(method, operation);

        // --- C. 响应解析 (默认 200 OK) ---
        // 既然返回 JsonObject/JsonArray，默认响应 application/json
        final ApiResponses responses = new ApiResponses();
        final ApiResponse response200 = new ApiResponse().description("Success");
        final Content content = new Content();
        final MediaType mediaType = new MediaType();
        // 根据返回值类型推断 Schema
        mediaType.setSchema(resolveSchema(method.getReturnType()));
        content.addMediaType("application/json", mediaType);

        response200.setContent(content);
        responses.addApiResponse("200", response200);
        operation.setResponses(responses);

        // 3. 将 Operation 挂载到 PathItem (基于 JAX-RS 注解判断动词)
        mountOperation(pathItem, method, operation);
    }

    private static void mountOperation(final PathItem pathItem, final Method method, final Operation operation) {
        // 直接判断方法上的 JAX-RS 注解，不依赖 Vert.x 枚举
        if (method.isAnnotationPresent(GET.class)) {
            pathItem.setGet(operation);
        } else if (method.isAnnotationPresent(POST.class)) {
            pathItem.setPost(operation);
        } else if (method.isAnnotationPresent(PUT.class)) {
            pathItem.setPut(operation);
        } else if (method.isAnnotationPresent(DELETE.class)) {
            pathItem.setDelete(operation);
        } else if (method.isAnnotationPresent(PATCH.class)) {
            pathItem.setPatch(operation);
        } else if (method.isAnnotationPresent(HEAD.class)) {
            pathItem.setHead(operation);
        } else if (method.isAnnotationPresent(OPTIONS.class)) {
            pathItem.setOptions(operation);
        }
    }

    private static void parseParameters(final Method method, final Operation operation) {
        // 注意：这里必须使用全限定名 java.lang.reflect.Parameter
        // 因为 import 引入了 Swagger 的 Parameter 类，存在命名冲突
        final java.lang.reflect.Parameter[] parameters = method.getParameters();

        for (final java.lang.reflect.Parameter param : parameters) {
            // 1. 检查是否是 BodyParam (你的特殊注解)
            if (param.isAnnotationPresent(BodyParam.class)) {
                final RequestBody requestBody = new RequestBody();
                requestBody.setRequired(true);
                final Content content = new Content();
                final MediaType mediaType = new MediaType();
                mediaType.setSchema(resolveSchema(param.getType()));
                content.addMediaType("application/json", mediaType);
                requestBody.setContent(content);
                operation.setRequestBody(requestBody);
                continue; // 处理完 Body 就可以跳过该参数的其他检查了
            }

            // 2. 检查标准 JAX-RS 参数 (@HeaderParam, @QueryParam, @PathParam)
            final Parameter swaggerParam = new Parameter();
            boolean isStandardParam = false;

            if (param.isAnnotationPresent(HeaderParam.class)) {
                swaggerParam.setIn("header");
                swaggerParam.setName(param.getAnnotation(HeaderParam.class).value());
                isStandardParam = true;
            } else if (param.isAnnotationPresent(QueryParam.class)) {
                swaggerParam.setIn("query");
                swaggerParam.setName(param.getAnnotation(QueryParam.class).value());
                isStandardParam = true;
            } else if (param.isAnnotationPresent(PathParam.class)) {
                swaggerParam.setIn("path");
                swaggerParam.setName(param.getAnnotation(PathParam.class).value());
                swaggerParam.setRequired(true);
                isStandardParam = true;
            } else if (param.isAnnotationPresent(CookieParam.class)) {
                swaggerParam.setIn("cookie");
                swaggerParam.setName(param.getAnnotation(CookieParam.class).value());
                isStandardParam = true;
            }

            // 3. 如果识别到了标准参数，设置 Schema 并添加到 Operation
            if (isStandardParam) {
                // 如果没有 Schema，根据 Java 类型生成简单的
                if (swaggerParam.getSchema() == null) {
                    swaggerParam.setSchema(resolveSchema(param.getType()));
                }
                operation.addParametersItem(swaggerParam);
            }
        }
    }

    /**
     * 简单的 Java 类型 -> Swagger Schema 映射
     */
    private static Schema<?> resolveSchema(final Class<?> type) {
        // JsonObject / JsonArray / Map -> Object
        if (JsonObject.class.isAssignableFrom(type) ||
            JsonArray.class.isAssignableFrom(type) ||
            Map.class.isAssignableFrom(type)) {
            return new Schema<>().type("object");
        }

        // 基础类型
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

        // 默认兜底
        return new Schema<>().type("object");
    }
}