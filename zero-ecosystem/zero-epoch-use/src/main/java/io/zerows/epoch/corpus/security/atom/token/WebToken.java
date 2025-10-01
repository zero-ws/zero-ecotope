package io.zerows.epoch.corpus.security.atom.token;

import io.vertx.core.json.JsonObject;

import java.io.Serializable;

/*
 * WebToken core interface
 */
public interface WebToken extends Serializable {
    /*
     * 读取 WebToken 的值
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
