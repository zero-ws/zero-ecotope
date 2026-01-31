package io.zerows.plugins.swagger;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import io.zerows.epoch.web.WebEvent;
import jakarta.ws.rs.extension.BodyParam;

import java.lang.reflect.Method;
import java.util.ArrayList;

class DocSpecSwagger implements DocSpec {

    @Override
    public void compile(final OpenAPI openAPI, final WebEvent event) {
        final Method method = event.getAction();
        if (method == null) {
            return;
        }

        // 1. 获取已存在的 Operation (由 JAX-RS Spec 创建)
        final PathItem pathItem = openAPI.getPaths().get(event.getPath());
        if (pathItem == null) {
            return;
        }

        final Operation operation = this.getOperation(pathItem, method);
        if (operation == null) {
            return;
        }

        // 2. 解析 @Tag (Class Level)
        this.parseClassTags(openAPI, method, operation);

        // 3. 解析 @Operation (Method Level)
        if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.Operation.class)) {
            final io.swagger.v3.oas.annotations.Operation opAnno = method.getAnnotation(io.swagger.v3.oas.annotations.Operation.class);
            operation.setSummary(opAnno.summary());
            operation.setDescription(opAnno.description());
            this.parseResponses(opAnno, operation, method);
        }

        // 4. 解析 @Parameter (Enhancement)
        this.enhanceParameters(method, operation);
    }

    private void parseClassTags(final OpenAPI openAPI, final Method method, final Operation operation) {
        final Class<?> clazz = method.getDeclaringClass();
        if (clazz.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class)) {
            final io.swagger.v3.oas.annotations.tags.Tag tagAnno = clazz.getAnnotation(io.swagger.v3.oas.annotations.tags.Tag.class);
            // 覆盖默认 Tag
            operation.setTags(new ArrayList<>());
            operation.addTagsItem(tagAnno.name());

            // 注册全局 Tag 定义
            if (openAPI.getTags() == null) {
                openAPI.setTags(new ArrayList<>());
            }
            final boolean exists = openAPI.getTags().stream().anyMatch(t -> t.getName().equals(tagAnno.name()));
            if (!exists) {
                openAPI.addTagsItem(new Tag().name(tagAnno.name()).description(tagAnno.description()));
            }
        }
    }

    private void parseResponses(final io.swagger.v3.oas.annotations.Operation opAnno, final Operation operation, final Method method) {
        if (opAnno.responses().length == 0) {
            return;
        }

        final ApiResponses apiResponses = new ApiResponses();
        for (final io.swagger.v3.oas.annotations.responses.ApiResponse respAnno : opAnno.responses()) {
            final ApiResponse response = new ApiResponse();
            response.setDescription(respAnno.description());

            if (respAnno.content().length > 0) {
                final Content content = new Content();
                for (final io.swagger.v3.oas.annotations.media.Content contentAnno : respAnno.content()) {
                    final MediaType mediaType = new MediaType();

                    // Schema
                    if (!contentAnno.schema().ref().isEmpty()) {
                        mediaType.setSchema(new Schema<>().$ref(contentAnno.schema().ref()));
                    } else if (contentAnno.schema().implementation() != Void.class) {
                        mediaType.setSchema(DocSpec.resolveSchema(contentAnno.schema().implementation()));
                    } else {
                        mediaType.setSchema(DocSpec.resolveSchema(method.getReturnType()));
                    }

                    // Examples
                    for (final ExampleObject exAnno : contentAnno.examples()) {
                        final Example example = new Example();
                        example.setValue(exAnno.value());
                        example.setSummary(exAnno.summary());
                        mediaType.addExamples(exAnno.name(), example);
                    }
                    content.addMediaType(contentAnno.mediaType().isEmpty() ? "application/json" : contentAnno.mediaType(), mediaType);
                }
                response.setContent(content);
            }
            apiResponses.addApiResponse(respAnno.responseCode(), response);
        }
        operation.setResponses(apiResponses);
    }

    private void enhanceParameters(final Method method, final Operation operation) {
        // 冲突点：import 了 Swagger 的 Parameter，反射的必须用全名
        final java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (final java.lang.reflect.Parameter param : parameters) {
            // Body 增强
            if (param.isAnnotationPresent(BodyParam.class) && param.isAnnotationPresent(io.swagger.v3.oas.annotations.Parameter.class)) {
                final io.swagger.v3.oas.annotations.Parameter pAnno = param.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                final RequestBody body = operation.getRequestBody();
                if (body != null) {
                    body.setDescription(pAnno.description());
                    if (!pAnno.required()) {
                        body.setRequired(false);
                    }
                }
            }

            // JAX-RS Param 增强
            if (param.isAnnotationPresent(io.swagger.v3.oas.annotations.Parameter.class)) {
                final io.swagger.v3.oas.annotations.Parameter pAnno = param.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                if (operation.getParameters() != null) {
                    for (final Parameter opParam : operation.getParameters()) {
                        // 简单的匹配逻辑：假设注解里的 name 和 JAX-RS 的 name 一致
                        if (pAnno.name().equals(opParam.getName())) {
                            if (!pAnno.description().isEmpty()) {
                                opParam.setDescription(pAnno.description());
                            }
                            if (!pAnno.example().isEmpty()) {
                                opParam.setExample(pAnno.example());
                            }
                            if (pAnno.required()) {
                                opParam.setRequired(true);
                            }
                        }
                    }
                }
            }
        }
    }
}