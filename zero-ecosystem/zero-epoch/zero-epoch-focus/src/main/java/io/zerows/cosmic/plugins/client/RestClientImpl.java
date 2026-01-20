package io.zerows.cosmic.plugins.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.zerows.epoch.annotations.Defer;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * REST 客户端实现类
 * [ ZERO ] ( Rest ) 负责处理底层 HTTP 交互与响应解析
 */
@Defer
@Slf4j
class RestClientImpl implements RestClient {

    private final WebClient client;

    RestClientImpl(final Vertx vertx, final RestClientConfig config) {
        Objects.requireNonNull(config, "[ ZERO ] ( Rest ) RestClientConfig 不能为空。");
        // --- 1. 计算默认值 (防止配置为空) ---

        // SSL 默认为 false (使用 Boolean.TRUE.equals 防止 null 指针)
        final boolean isSsl = Boolean.TRUE.equals(config.getSsl());

        // Host 默认为 "localhost"
        final String host = (config.getHost() == null || config.getHost().trim().isEmpty())
            ? "localhost"
            : config.getHost();

        // Port 默认为 443 (SSL) 或 80 (非 SSL)
        final int port = (config.getPort() == null)
            ? (isSsl ? 443 : 80)
            : config.getPort();

        // --- 2. 根据配置初始化 WebClientOptions ---
        final WebClientOptions options = new WebClientOptions()
            .setDefaultHost(host)  // 使用计算后的 host
            .setDefaultPort(port)  // 使用计算后的 port
            .setSsl(isSsl)
            .setTrustAll(config.getTrustAll()) // Boolean 类型 Vert.x 会自动处理 null 为 false
            .setUserAgent(config.getUserAgent())
            .setKeepAlive(config.getKeepAlive())
            .setConnectTimeout(config.getConnectTimeout())
            .setIdleTimeout(config.getIdleTimeout());

        this.client = WebClient.create(vertx, options);

        // 日志中记录实际使用的 Host 和 Port
        log.info("[ ZERO ] ( Rest ) RestClient 已初始化，默认目标: {}://{}:{}",
            isSsl ? "https" : "http", host, port);
    }

    // =========================================================================
    // 核心重构：智能创建 Request
    // =========================================================================

    /**
     * 根据 URI 格式智能选择 "相对路径模式" 或 "绝对路径模式"
     */
    private HttpRequest<Buffer> createRequest(final HttpMethod method, final String uri) {
        // 如果 URI 是绝对路径 (http://...)，使用 requestAbs，忽略默认配置
        if (uri.startsWith("http://") || uri.startsWith("https://")) {
            return this.client.requestAbs(method, uri);
        }

        // 如果 URI 是相对路径 (/path)，使用 request，Vert.x 会自动应用 options 中的 host/port/ssl
        return this.client.request(method, uri);
    }

    // =========================================================================
    // HTTP 方法实现
    // =========================================================================

    @Override
    public Future<JsonObject> doGet(final String uri, final JsonObject queryParams, final MultiMap headers) {
        // ✅ 使用 createRequest 替代原来的 prepareUrl + getAbs
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.GET, uri);
        this.addHeaders(request, headers);
        this.addQueryParams(request, queryParams);
        return this.send(request, null);
    }

    @Override
    public Future<JsonObject> doPost(final String uri, final JsonObject body, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.POST, uri);
        this.addHeaders(request, headers);
        return this.send(request, body);
    }

    @Override
    public Future<JsonObject> doPostForm(final String uri, final JsonObject formParams, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.POST, uri);
        this.addHeaders(request, headers);
        // 设置表单提交专用的 Content-Type
        request.putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");

        final MultiMap form = MultiMap.caseInsensitiveMultiMap();
        if (Objects.nonNull(formParams)) {
            formParams.forEach(entry -> form.add(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return request.sendForm(form).compose(this::processResponse);
    }

    @Override
    public Future<JsonObject> doPut(final String uri, final JsonObject body, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.PUT, uri);
        this.addHeaders(request, headers);
        return this.send(request, body);
    }

    @Override
    public Future<JsonObject> doPatch(final String uri, final JsonObject body, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.PATCH, uri);
        this.addHeaders(request, headers);
        return this.send(request, body);
    }

    @Override
    public Future<JsonObject> doDelete(final String uri, final JsonObject queryParams, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.DELETE, uri);
        this.addHeaders(request, headers);
        this.addQueryParams(request, queryParams);
        return this.send(request, null);
    }

    @Override
    public Future<JsonObject> doOptions(final String uri, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.OPTIONS, uri);
        this.addHeaders(request, headers);
        return this.send(request, null);
    }

    @Override
    public Future<JsonObject> doHead(final String uri, final MultiMap headers) {
        final HttpRequest<Buffer> request = this.createRequest(HttpMethod.HEAD, uri);
        this.addHeaders(request, headers);
        return this.send(request, null);
    }

    // =========================================================================
    // 内部私有辅助方法
    // =========================================================================

    // ❌ 【移除】 prepareUrl 方法已不再需要，逻辑合并入 createRequest

    /**
     * 填充 HTTP 头信息
     */
    private void addHeaders(final HttpRequest<Buffer> request, final MultiMap headers) {
        if (Objects.nonNull(headers)) {
            request.putHeaders(headers);
        }
    }

    /**
     * 填充 Query 查询参数
     */
    private void addQueryParams(final HttpRequest<Buffer> request, final JsonObject queryParams) {
        if (Objects.nonNull(queryParams)) {
            queryParams.forEach(entry -> request.addQueryParam(entry.getKey(), String.valueOf(entry.getValue())));
        }
    }

    /**
     * 发送请求：根据是否存在 Body 选择不同的发送方法
     */
    private Future<JsonObject> send(final HttpRequest<Buffer> request, final JsonObject body) {
        if (Objects.nonNull(body)) {
            return request.sendJsonObject(body).compose(this::processResponse);
        } else {
            return request.send().compose(this::processResponse);
        }
    }

    /**
     * 统一响应处理逻辑
     */
    private Future<JsonObject> processResponse(final HttpResponse<Buffer> response) {
        final int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            try {
                final Buffer buffer = response.body();
                // 容错处理：当响应体为空时直接返回空 Json 对象
                if (Objects.isNull(buffer) || buffer.length() == 0) {
                    return Future.succeededFuture(new JsonObject());
                }
                return Future.succeededFuture(response.bodyAsJsonObject());
            } catch (final Exception ex) {
                log.error("[ ZERO ] ( Rest ) 解析 JSON 响应失败: {}", ex.getMessage());
                return Future.failedFuture("[ ZERO ] ( Rest ) 响应内容不是有效的 JSON 格式。");
            }
        } else {
            // 记录异常状态码日志
            log.warn("[ ZERO ] ( Rest ) 请求失败: 状态码={}, 消息={}", statusCode, response.statusMessage());
            return Future.failedFuture("[ ZERO ] ( Rest ) HTTP 错误: " + statusCode + " " + response.statusMessage());
        }
    }
}