package io.zerows.module.metadata.osgi.spi;

import io.vertx.core.json.JsonObject;
import io.zerows.core.spi.HorizonIo;
import io.zerows.core.uca.log.internal.Log4JAnnal;
import io.zerows.module.metadata.store.OCacheFailure;

/**
 * 新版此处变成纯提取，是否有无 Bundle 都会在初始化过程中将内置存储的数据填充。
 *
 * @author lang : 2023/4/28
 */
public class HorizonIoLoad implements HorizonIo {

    @Override
    public JsonObject ofError() {
        return OCacheFailure.entireError();
    }

    @Override
    public JsonObject ofFailure() {
        return OCacheFailure.entireFailure();
    }

    @Override
    public Class<?> ofLogger() {
        return Log4JAnnal.class;
    }
}
