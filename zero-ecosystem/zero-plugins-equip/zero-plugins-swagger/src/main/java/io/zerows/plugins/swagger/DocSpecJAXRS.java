package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
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

class DocSpecJAXRS implements DocSpec {

    @Override
    public void compile(final OpenAPI openAPI, final WebEvent event) {
        final Method method = event.getAction();
        if (method == null) {
            return;
        }

        // 1. 确保 PathItem 存在
        final String path = event.getPath();
        PathItem pathItem = openAPI.getPaths().get(path);
        if (pathItem == null) {
            pathItem = new PathItem();
            openAPI.getPaths().addPathItem(path, pathItem);
        }

        // 2. 创建并挂载 Operation (基于 JAX-RS 动词)
        final Operation operation = new Operation();
        this.mountOperation(pathItem, method, operation);

        // 3. 设置基础元数据 (Summary & Tag)
        operation.setSummary(method.getName());
        operation.addTagsItem(method.getDeclaringClass().getSimpleName());

        // 4. 解析 JAX-RS 参数
        this.parseJAXRSParameters(method, operation);

        // 5. 设置默认响应 (根据返回值)
        this.parseDefaultResponse(method, operation);
    }

    private void mountOperation(final PathItem pathItem, final Method method, final Operation operation) {
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

    private void parseJAXRSParameters(final Method method, final Operation operation) {
        // 冲突点：import 了 Swagger 的 Parameter，反射的必须用全名
        final java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (final java.lang.reflect.Parameter param : parameters) {
            // BodyParam
            if (param.isAnnotationPresent(BodyParam.class)) {
                final RequestBody requestBody = new RequestBody();
                requestBody.setRequired(true);
                final Content content = new Content();
                final MediaType mediaType = new MediaType();
                mediaType.setSchema(DocSpec.resolveSchema(param.getType()));
                content.addMediaType("application/json", mediaType);
                requestBody.setContent(content);
                operation.setRequestBody(requestBody);
                continue;
            }
            // Standard Params
            final Parameter swaggerParam = new Parameter();
            boolean isStandard = false;
            if (param.isAnnotationPresent(HeaderParam.class)) {
                swaggerParam.setIn("header");
                swaggerParam.setName(param.getAnnotation(HeaderParam.class).value());
                isStandard = true;
            } else if (param.isAnnotationPresent(QueryParam.class)) {
                swaggerParam.setIn("query");
                swaggerParam.setName(param.getAnnotation(QueryParam.class).value());
                isStandard = true;
            } else if (param.isAnnotationPresent(PathParam.class)) {
                swaggerParam.setIn("path");
                swaggerParam.setName(param.getAnnotation(PathParam.class).value());
                swaggerParam.setRequired(true);
                isStandard = true;
            } else if (param.isAnnotationPresent(CookieParam.class)) {
                swaggerParam.setIn("cookie");
                swaggerParam.setName(param.getAnnotation(CookieParam.class).value());
                isStandard = true;
            }

            if (isStandard) {
                swaggerParam.setSchema(DocSpec.resolveSchema(param.getType()));
                operation.addParametersItem(swaggerParam);
            }
        }
    }

    private void parseDefaultResponse(final Method method, final Operation operation) {
        final ApiResponses responses = new ApiResponses();
        final ApiResponse response200 = new ApiResponse().description("Success");
        final Content content = new Content();
        final MediaType mediaType = new MediaType();
        mediaType.setSchema(DocSpec.resolveSchema(method.getReturnType()));
        content.addMediaType("application/json", mediaType);
        response200.setContent(content);
        responses.addApiResponse("200", response200);
        operation.setResponses(responses);
    }
}