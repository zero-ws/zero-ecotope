package io.zerows.boot.graphic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.app.HArk;

import java.util.Set;

/*
 * 绘图仪
 */
public interface Plotter {

    Plotter bind(HArk app);

    /*
     * 初始化图专用
     */
    Future<JsonObject> drawAsync(String recordId, String relationId);

    Future<JsonObject> drawAsync(String recordId, String relationId, Set<String> ignore);
}
