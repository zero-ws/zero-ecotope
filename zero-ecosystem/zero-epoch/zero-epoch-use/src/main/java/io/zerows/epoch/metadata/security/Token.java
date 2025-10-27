package io.zerows.epoch.metadata.security;

import io.vertx.core.json.JsonObject;

import java.io.Serializable;

/*
 * Token core interface
 */
public interface Token extends Serializable {
    /*
     * 读取 Token 的值
     */
    String token();

    /*
     * 生成 `Authorization` 请求头
     */
    String authorization();

    /*
     * 读取用户 ID
     */
    default String user() {
        return null;
    }

    JsonObject data();
}
