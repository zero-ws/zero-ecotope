package io.zerows.epoch.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.Kv;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
class _Ir extends _From {


    // Where current condition is null
    /*
     * Query Engine API
     *
     * QH -> Query with Criteria   （全格式）
     * H  -> Criteria              （查询条件）
     * V  -> Projection            （列过滤）
     *
     * 1) irNil / irAnd / irOne
     *    - irNil:          判断查询条件是否为空，"": true | false 单节点也为空
     *    - irOp:           判断查询条件是 AND 还是 OR，AND返回true，OR返回false
     *    - irOne:          判断查询条件是否单条件（只有一个条件）
     * 2) irAndQH / irAndH
     *    - irAndQH:        参数本身为全格式:
     *      {
     *           "criteria": {}
     *      }
     *    - irAndH:         参数本身为全格式中的 criteria 纯格式
     * 3) irQV / irV
     *    - irQV:           参数本身为全格式:
     *      {
     *           "projection": []
     *      }
     *    - irV:            参数本身为全格式中的 projection 纯格式
     *
     * 全格式返回全格式，纯格式返回纯格式
     */
    public static boolean irNil(final JsonObject condition) {
        return QrIr.irNil(condition);
    }

    public static boolean irOp(final JsonObject condition) {
        return QrIr.irAnd(condition);
    }

    public static boolean irOne(final JsonObject condition) {
        return QrIr.irOne(condition);
    }

    // ---------------------- Qr Modification --------------------------
    public static JsonObject irAndQH(final JsonObject qr, final Kv<String, Object> kv) {
        Objects.requireNonNull(kv);
        return QrIr.irQH(qr, kv.key(), kv.value());
    }

    public static JsonObject irAndQH(final JsonObject qr, final String field, final Object value) {
        return QrIr.irQH(qr, field, value);
    }

    public static JsonObject irAndQH(final JsonObject query, final JsonObject criteria, final boolean clear) {
        return QrIr.irQH(query, criteria, clear);
    }

    public static JsonObject irAndH(final JsonObject original, final JsonObject criteria) {
        return QrIr.irH(original, criteria);
    }

    public static JsonObject irAndH(final JsonObject original, final String field, final Object value) {
        return QrIr.irH(original, field, value);
    }

    public static JsonObject irAndH(final JsonObject original, final Kv<String, Object> kv) {
        Objects.requireNonNull(kv);
        return QrIr.irH(original, kv.key(), kv.value());
    }

    // Qr Combine ( projection + projection )
    public static JsonObject irQV(final JsonObject query, final JsonArray projection, final boolean clear) {
        return QrIr.irQV(query, projection, clear);
    }

    public static JsonArray irV(final JsonArray original, final JsonArray projection) {
        return QrIr.irV(original, projection);
    }
}
