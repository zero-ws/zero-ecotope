package io.zerows.plugins.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.*;
import jakarta.ws.rs.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * OpenAPI 文档生成器。
 * <p>
 * 该类扫描所有标注了 JAX-RS 注解（如 @Path、@GET 等）及 Swagger @Operation 的接口类，
 * 自动构建符合 OpenAPI 3.0 规范的文档结构。
 * 支持以下 Swagger 3 注解：
 * - @Operation: 操作描述
 * - @Parameter: 参数描述
 * - @RequestBody: 请求体描述
 * - @ApiResponse: 响应描述
 * - @Tag: 标签分组
 * - @Schema: 数据模型描述
 */
public class OpenApiGenerator {

    /**
     * 生成 OpenAPI 文档对象。
     *
     * @param handlerClasses 所有待扫描的接口类集合（必须为 @Path 标注的接口）
     * @return OpenAPI 对象，包含 paths、info 等结构
     */
    public static OpenAPI generate(Set<Class<?>> handlerClasses) {
        OpenAPI openAPI = new OpenAPI();

        // 添加基础信息
        openAPI.setInfo(new Info()
                .title("Vert.x Swagger API")
                .version("1.0.0")
                .description("自动生成的 OpenAPI 文档"));

        // 生成路径信息
        Paths paths = new Paths();
        for (Class<?> cls : handlerClasses) {
            processClass(cls, paths);
        }
        openAPI.setPaths(paths);

        return openAPI;
    }

    /**
     * 处理一个接口类，提取其所有方法并构造 PathItem。
     *
     * @param cls   接口类
     * @param paths OpenAPI 的路径集合
     */
    private static void processClass(Class<?> cls, Paths paths) {
        // 类上的 @Path 注解作为基路径
        String basePath = cls.isAnnotationPresent(Path.class)
                ? cls.getAnnotation(Path.class).value() : "";

        for (Method method : cls.getDeclaredMethods()) {
            // 获取 HTTP 方法（如 GET/POST）
            PathItem.HttpMethod httpMethod = extractHttpMethod(method);
            if (httpMethod == null) continue;

            // 方法上的 @Path 注解为子路径
            String methodPath = method.isAnnotationPresent(Path.class)
                    ? method.getAnnotation(Path.class).value() : "";

            String fullPath = normalizePath(basePath, methodPath);

            // 构建 Operation 描述
            io.swagger.v3.oas.models.Operation operation = buildOperation(method, cls);

            // 合并 PathItem 进 paths
            PathItem pathItem = paths.computeIfAbsent(fullPath, k -> new PathItem());
            pathItem.operation(httpMethod, operation);
        }
    }

    /**
     * 根据方法上的 Swagger 注解构建 Swagger 的 Operation 对象。
     *
     * @param method 接口方法
     * @param cls    接口类
     * @return Swagger Operation 对象，若无 @Operation 注解则返回 null
     */
    private static io.swagger.v3.oas.models.Operation buildOperation(Method method, Class<?> cls) {
        io.swagger.v3.oas.models.Operation operation = new io.swagger.v3.oas.models.Operation();
        
        // 处理 @Operation 注解
        Operation anno = method.getAnnotation(Operation.class);
        if (anno != null) {
            operation.setOperationId(anno.operationId());
            operation.setSummary(anno.summary());
            operation.setDescription(anno.description());
        } else {
            // 如果没有 @Operation 注解，使用默认值
            operation.setOperationId(method.getName());
            operation.setSummary(method.getName());
        }

        // 设置 tags
        if (anno!=null &&anno.tags().length > 0) {
            operation.setTags(Arrays.asList(anno.tags()));
        }

        // 处理参数
        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = buildParameters(method);
        if (!parameters.isEmpty()) {
            operation.setParameters(parameters);
        }

        // 处理请求体
        io.swagger.v3.oas.models.parameters.RequestBody requestBody = buildRequestBody(method);
        if (requestBody != null) {
            operation.setRequestBody(requestBody);
        }

        // 处理响应
        ApiResponses responses = buildResponses(method);
        operation.setResponses(responses);

        return operation;
    }

    /**
     * 构建方法参数列表。
     *
     * @param method 方法对象
     * @return 参数列表
     */
    private static List<io.swagger.v3.oas.models.parameters.Parameter> buildParameters(Method method) {
        List<io.swagger.v3.oas.models.parameters.Parameter> parameters = new ArrayList<>();
        java.lang.reflect.Parameter[] methodParams = method.getParameters();

        for (int i = 0; i < methodParams.length; i++) {
            java.lang.reflect.Parameter param = methodParams[i];
            io.swagger.v3.oas.annotations.Parameter paramAnno = param.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            
            if (paramAnno != null) {
                io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
                parameter.setName(paramAnno.name().isEmpty() ? param.getName() : paramAnno.name());
                parameter.setDescription(paramAnno.description());
                parameter.setRequired(paramAnno.required());
                parameter.setIn(paramAnno.in().toString().toLowerCase());
                
                // 处理 Schema
                if (paramAnno.schema() != null) {
                    parameter.setSchema(buildSchema(paramAnno.schema()));
                }
                
                parameters.add(parameter);
            }
        }

        return parameters;
    }

    /**
     * 构建请求体。
     *
     * @param method 方法对象
     * @return RequestBody 对象，如果没有 @RequestBody 注解则返回 null
     */
    private static io.swagger.v3.oas.models.parameters.RequestBody buildRequestBody(Method method) {
        RequestBody requestBodyAnno = method.getAnnotation(RequestBody.class);
        if (requestBodyAnno == null) return null;

        io.swagger.v3.oas.models.parameters.RequestBody requestBody = new io.swagger.v3.oas.models.parameters.RequestBody();
        requestBody.setDescription(requestBodyAnno.description());
        requestBody.setRequired(requestBodyAnno.required());

        // 处理内容
        if (requestBodyAnno.content().length > 0) {
            io.swagger.v3.oas.models.media.Content content = new io.swagger.v3.oas.models.media.Content();
            for (io.swagger.v3.oas.annotations.media.Content contentAnno : requestBodyAnno.content()) {
                MediaType mediaType = new MediaType();
                if (contentAnno.schema() != null) {
                    mediaType.setSchema(buildSchema(contentAnno.schema()));
                }
                content.addMediaType(contentAnno.mediaType(), mediaType);
            }
            requestBody.setContent(content);
        }

        return requestBody;
    }

    /**
     * 构建响应对象。
     *
     * @param method 方法对象
     * @return ApiResponses 对象
     */
    private static ApiResponses buildResponses(Method method) {
        ApiResponses responses = new ApiResponses();
        
        // 处理 @ApiResponse 注解
        ApiResponse[] apiResponses = method.getAnnotationsByType(ApiResponse.class);
        for (ApiResponse apiResponse : apiResponses) {
            io.swagger.v3.oas.models.responses.ApiResponse response = new io.swagger.v3.oas.models.responses.ApiResponse();
            response.setDescription(apiResponse.description());
            
            // 处理响应内容
            if (apiResponse.content().length > 0) {
                io.swagger.v3.oas.models.media.Content content = new io.swagger.v3.oas.models.media.Content();
                for (io.swagger.v3.oas.annotations.media.Content contentAnno : apiResponse.content()) {
                    MediaType mediaType = new MediaType();
                    if (contentAnno.schema() != null) {
                        mediaType.setSchema(buildSchema(contentAnno.schema()));
                    }
                    content.addMediaType(contentAnno.mediaType(), mediaType);
                }
                response.setContent(content);
            }
            
            responses.addApiResponse(apiResponse.responseCode(), response);
        }

        // 如果没有 @ApiResponse 注解，添加默认的 200 响应
        if (responses.isEmpty()) {
            responses.addApiResponse("200", new io.swagger.v3.oas.models.responses.ApiResponse().description("OK"));
        }

        return responses;
    }

    /**
     * 构建 Schema 对象。
     *
     * @param schemaAnno Schema 注解
     * @return Schema 对象
     */
    private static io.swagger.v3.oas.models.media.Schema<?> buildSchema(io.swagger.v3.oas.annotations.media.Schema schemaAnno) {
        io.swagger.v3.oas.models.media.Schema<?> schema = new io.swagger.v3.oas.models.media.Schema<>();
        
        if (!schemaAnno.type().isEmpty()) {
            schema.setType(schemaAnno.type());
        }
        if (!schemaAnno.format().isEmpty()) {
            schema.setFormat(schemaAnno.format());
        }
        if (!schemaAnno.description().isEmpty()) {
            schema.setDescription(schemaAnno.description());
        }
        if (!schemaAnno.example().isEmpty()) {
            schema.setExample(schemaAnno.example());
        }
        
        schema.setNullable(schemaAnno.nullable());
        // Note: readOnly and writeOnly are deprecated in newer versions
        // schema.setReadOnly(schemaAnno.readOnly());
        // schema.setWriteOnly(schemaAnno.writeOnly());
        
        return schema;
    }

    /**
     * 提取方法上的 HTTP 方法注解（GET/POST/...）。
     *
     * @param method 方法对象
     * @return PathItem.HttpMethod 枚举，未识别则返回 null
     */
    private static PathItem.HttpMethod extractHttpMethod(Method method) {
        if (method.isAnnotationPresent(GET.class)) return PathItem.HttpMethod.GET;
        if (method.isAnnotationPresent(POST.class)) return PathItem.HttpMethod.POST;
        if (method.isAnnotationPresent(PUT.class)) return PathItem.HttpMethod.PUT;
        if (method.isAnnotationPresent(DELETE.class)) return PathItem.HttpMethod.DELETE;
        if (method.isAnnotationPresent(PATCH.class)) return PathItem.HttpMethod.PATCH;
        return null;
    }

    /**
     * 合并类路径与方法路径，保证格式为 `/xxx/yyy`。
     *
     * @param basePath   类级别的路径
     * @param methodPath 方法级别的路径
     * @return 格式化后的完整路径（以 / 开头）
     */
    private static String normalizePath(String basePath, String methodPath) {
        String full = (basePath + "/" + methodPath).replaceAll("//+", "/");
        return full.startsWith("/") ? full : "/" + full;
    }
}
