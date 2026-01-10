package io.zerows.extension.module.ambient.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.metadata.UObject;
import io.zerows.extension.module.ambient.boot.MDAmbientManager;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.platform.enums.Result;
import io.zerows.plugins.excel.ExcelActor;
import io.zerows.plugins.excel.ExcelClient;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ExInitDatum implements ExInit {
    private static final MDAmbientManager MANAGER = MDAmbientManager.of();

    @Override
    public Function<JsonObject, Future<JsonObject>> apply() {
        return appJson -> {
            log.info("{} 数据加载，应用配置：{}", AtConstant.K_PREFIX_AMB, appJson.encode());
            return this.doLoading(appJson)
                /* Extension */
                .compose(this::doExtension);
        };
    }

    public Future<JsonObject> doExtension(final JsonObject appJson) {
        final ExInit loader = Objects.requireNonNull(MANAGER.config()).ofLoad();
        if (Objects.isNull(loader)) {
            return Ux.future(appJson);
        }
        return loader.apply().apply(appJson);
    }

    private Future<JsonObject> doLoading(final JsonObject appJson) {
        /* Datum Loading */
        final String dataFolder = Objects.requireNonNull(MANAGER.config()).getDataFolder();
        final List<String> files = Ut.ioFiles(dataFolder);
        /* List<Future> */
        final List<Future<JsonObject>> futures = files.stream()
            .filter(Ut::isNotNil)
            /* Remove temp file of Excel */
            .filter(file -> !file.startsWith("~$"))
            .map(file -> dataFolder + file)
            .map(this::doLoading)
            .collect(Collectors.toList());
        return Fx.combineA(futures)
            /* Stored each result */
            .compose(results -> UObject.create().append(KName.RESULT, results)
                .toFuture())
            .compose(results -> Ux.future(this.result(results, appJson)));
    }

    private Future<JsonObject> doLoading(final String filename) {
        return Ux.nativeWorker(filename, pre -> {
            /* ExcelClient */
            final ExcelClient client = ExcelActor.ofClient();
            client.importAsync(filename, result -> {
                log.info("{} 数据加载，文件源：{}", AtConstant.K_PREFIX_AMB, filename);
                if (result.succeeded()) {
                    final JsonObject endJ = new JsonObject();
                    endJ.put(filename, Result.SUCCESS.name());
                    pre.complete(endJ);
                } else {
                    pre.fail(result.cause());
                }
            });
        });
    }

    @Override
    public JsonObject result(final JsonObject input, final JsonObject appJson) {
        /* Extract Failure Filename, No thing to do or */
        return appJson;
    }
}
