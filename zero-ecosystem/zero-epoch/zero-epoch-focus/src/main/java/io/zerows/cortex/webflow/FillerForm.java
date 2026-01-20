package io.zerows.cortex.webflow;

import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.zerows.weaver.ZeroType;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 「Co」JSR311 for .@FormParam
 * <p>
 * This `Filler` is for form attributes extracting.
 * It supports both `application/x-www-form-urlencoded` and `multipart/form-data`.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public class FillerForm implements Filler {

    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);

        // 1. 兜底：如果 Content-Type 为空，直接尝试作为普通属性读取
        if (contentType == null) {
            return ZeroType.value(paramType, request.getFormAttribute(name));
        }

        try {
            // 解析 MediaType (jakarta.ws.rs.core.MediaType 能够处理带 charset 的情况)
            final MediaType mediaType = MediaType.valueOf(contentType);

            // 2. 场景 A: application/x-www-form-urlencoded
            // 标准的 Key-Value 表单，不涉及文件上传逻辑
            if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(mediaType)) {
                return ZeroType.value(paramType, request.getFormAttribute(name));
            }

            // 3. 场景 B: multipart/form-data
            // 这种模式下，参数可能是文件，也可能是混在 Multipart 中的普通文本字段
            if (MediaType.MULTIPART_FORM_DATA_TYPE.isCompatible(mediaType)) {
                return this.extractMultipart(name, paramType, context);
            }

        } catch (final Exception e) {
            // 如果 Content-Type 格式非法，降级为普通读取，并记录调试日志
            log.debug("Mime type parsing failed: {}, fallback to attribute.", contentType);
        }

        // 4. 默认 fallback
        return ZeroType.value(paramType, request.getFormAttribute(name));
    }

    private Object extractMultipart(final String name,
                                    final Class<?> paramType,
                                    final RoutingContext context) {
        final Set<FileUpload> uploadSet = new HashSet<>(context.fileUploads());

        // 3.1 即使是 Multipart，也可能没有实际上传文件，或者参数在普通部分
        if (uploadSet.isEmpty()) {
            return ZeroType.value(paramType, context.request().getFormAttribute(name));
        }

        // 3.2 整理文件映射 (假设 ResolverUtil 能够处理重复 name 的文件组)
        final ConcurrentMap<String, Set<FileUpload>> compressed = ResolverUtil.toFile(uploadSet);

        if (compressed.containsKey(name)) {
            // 3.3 命中：该参数名对应的是文件流
            // 此时 paramType 应该是 File, byte[], InputStream 等类型
            final Set<FileUpload> uploadParam = compressed.get(name);
            final FileSystem fileSystem = context.vertx().fileSystem();
            return ResolverUtil.toFile(uploadParam, paramType, fileSystem::readFileBlocking);
        } else {
            // 3.4 未命中文件：尝试读取 Multipart body 中的普通文本字段
            // 例如：multipart 请求中包含 name="username" 的文本部分
            return ZeroType.value(paramType, context.request().getFormAttribute(name));
        }
    }
}