package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.vertx.core.http.HttpMethod;
import io.zerows.epoch.web.WebApi;
import io.zerows.epoch.web.WebEvent;
import io.zerows.platform.constant.VString;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
class DocSpecOpenAPI implements DocSpec {

    private static final String DIR_ROOT = "openapi/operations";
    private static final String FILE_NAME = "metadata.md";

    @Override
    public void compile(final OpenAPI openAPI, final WebEvent event) {
        final Method method = event.getAction();
        if (method == null) {
            return;
        }

        final String key = WebApi.nameOf(event);
        if (key == null) {
            return;
        }

        final String filePath = DIR_ROOT + VString.SLASH + key + VString.SLASH + FILE_NAME;

        try (final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            if (in == null) {
                return;
            }

            final Operation externalOp = LoaderMarkdown.load(in, Operation.class);

            if (externalOp == null) {
                log.warn("{} 非法格式的 YAML/JSON: {}", SwaggerConstant.K_PREFIX_DOC, filePath);
                return;
            }

            this.mergeToOpenAPI(openAPI, event, externalOp);

            // 2. 打印追加的新的接口，包含文件路径
            log.info("{} > API 解析追加: {} {} -> {} \t - 文档路径: {}",
                SwaggerConstant.K_PREFIX_DOC,
                event.getMethod(),
                event.getPath(),
                key,
                filePath
            );

        } catch (final Exception e) {
            log.error("{} 解析出错 Error: {}", SwaggerConstant.K_PREFIX_DOC, filePath, e);
        }
    }

    private void mergeToOpenAPI(final OpenAPI openAPI, final WebEvent event, final Operation source) {
        final String path = event.getPath();
        PathItem pathItem = openAPI.getPaths().get(path);

        if (pathItem == null) {
            pathItem = new PathItem();
            openAPI.getPaths().addPathItem(path, pathItem);
        }

        final Operation target = this.getOperation(pathItem, event.getAction());

        if (target != null) {
            this.mergeOperation(target, source);
        } else {
            this.setOperation(pathItem, event.getMethod(), source);
        }
    }

    private void setOperation(final PathItem item, final HttpMethod method, final Operation op) {
        if (method == null) {
            return;
        }
        switch (method.name()) {
            case "GET" -> item.setGet(op);
            case "POST" -> item.setPost(op);
            case "PUT" -> item.setPut(op);
            case "DELETE" -> item.setDelete(op);
            case "PATCH" -> item.setPatch(op);
            case "HEAD" -> item.setHead(op);
            case "OPTIONS" -> item.setOptions(op);
            default -> log.warn("{} 不支持的 Http Method: {}", SwaggerConstant.K_PREFIX_DOC, method);
        }
    }

    private void mergeOperation(final Operation target, final Operation source) {
        if (Objects.nonNull(source.getSummary())) {
            target.setSummary(source.getSummary());
        }
        if (Objects.nonNull(source.getDescription())) {
            target.setDescription(source.getDescription());
        }
        if (Objects.nonNull(source.getOperationId())) {
            target.setOperationId(source.getOperationId());
        }

        if (Objects.nonNull(source.getTags()) && !source.getTags().isEmpty()) {
            target.setTags(source.getTags());
        }
        if (Objects.nonNull(source.getParameters()) && !source.getParameters().isEmpty()) {
            target.setParameters(source.getParameters());
        }
        if (Objects.nonNull(source.getRequestBody())) {
            target.setRequestBody(source.getRequestBody());
        }
        if (Objects.nonNull(source.getResponses()) && !source.getResponses().isEmpty()) {
            target.setResponses(source.getResponses());
        }
        if (Objects.nonNull(source.getDeprecated())) {
            target.setDeprecated(source.getDeprecated());
        }
    }
}