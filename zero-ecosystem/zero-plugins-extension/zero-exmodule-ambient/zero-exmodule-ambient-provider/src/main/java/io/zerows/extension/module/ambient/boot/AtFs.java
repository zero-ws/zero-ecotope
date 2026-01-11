package io.zerows.extension.module.ambient.boot;

import io.r2mo.io.common.HFS;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.skeleton.spi.ExIo;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The directory data structure should be as following
 * // <pre><code class="json">
 * {
 *     "directoryId": "I_DIRECTORY key field"
 * }
 * // </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class AtFs {

    private static final MDAmbientManager MANAGER = MDAmbientManager.of();

    static Future<JsonObject> fileMeta(final JsonObject appJ) {
        final AtConfig config = MANAGER.config();
        if (Objects.nonNull(config)) {
            appJ.put(KName.STORE_PATH, config.getStorePath());
        }
        return Ux.futureJ(Ut.valueToJObject(appJ, KName.App.LOGO));
    }

    static Future<Buffer> fileDownload(final JsonArray attachment) {
        if (Ut.isNil(attachment)) {
            return Ux.future(Buffer.buffer());
        } else {
            return splitRun(attachment, (directoryId, fileMap) ->
                HPI.of(ExIo.class).waitAsync(
                    io -> io.fsDownload(directoryId, fileMap),
                    Buffer::buffer
                )
            );
        }
    }

    static Future<Buffer> fileDownload(final JsonObject attachment) {
        final String directoryId = attachment.getString(KName.DIRECTORY_ID);
        final String filePath = attachment.getString(KName.Attachment.FILE_PATH);
        if (Ut.ioExist(filePath)) {
            // Existing temp file here, it means that you can download faster
            return Ux.future(Ut.ioBuffer(filePath));
        }
        if (Ut.isNil(directoryId)) {
            return Ux.future(Buffer.buffer());
        } else {
            final String storePath = attachment.getString(KName.STORE_PATH);
            return HPI.of(ExIo.class).waitAsync(
                io -> io.fsDownload(directoryId, storePath),
                Buffer::buffer
            );
        }
    }

    /*
     * Step:
     * 1. Extract `directoryId` first.
     * 2. Build the mapping of `FILE_PATH = STORE_PATH` here.
     * 3. Pass two parameters to `ExIo`
     */
    static Future<JsonArray> fileUpload(final JsonArray attachment) {
        if (Ut.isNil(attachment)) {
            return Ux.futureA();
        } else {
            return splitInternal(attachment, Ux::future,
                remote -> splitRun(remote, (directoryId, fileMap) -> HPI.of(ExIo.class).waitAsync(
                    io -> io.fsUpload(directoryId, fileMap)
                        .compose(removed -> {
                            HFS.of().rm(fileMap.keySet());
                            return Future.succeededFuture(Boolean.TRUE);
                        })
                        .compose(removed -> Ux.future(remote)),
                    () -> remote
                )));
        }
    }


    static Future<JsonArray> fileRemove(final JsonArray attachment) {
        if (Ut.isNil(attachment)) {
            return Ux.futureA();
        } else {
            return splitInternal(attachment, local -> {
                final Set<String> files = new HashSet<>();
                Ut.itJArray(local).forEach(each -> files.add(each.getString(KName.Attachment.FILE_PATH)));
                HFS.of().rm(files);
                log.info("{} 删除本地文件，数量：{}", AtConstant.K_PREFIX_AMB, files.size());
                return Ux.future(local);
            }, remote -> splitRun(remote, (directoryId, fileMap) -> HPI.of(ExIo.class).waitAsync(
                io -> io.fsRemove(directoryId, fileMap).compose(removed -> Ux.future(remote)),
                () -> remote
            )));
        }
    }

    private static <T> Future<T> splitRun(
        final JsonArray source,
        final BiFunction<String, ConcurrentMap<String, String>, Future<T>> executor) {
        final ConcurrentMap<String, String> fileMap = new ConcurrentHashMap<>();
        final Set<String> directorySet = new HashSet<>();
        Ut.itJArray(source).forEach(json -> {
            if (json.containsKey(KName.DIRECTORY_ID)) {
                final String filePath = json.getString(KName.Attachment.FILE_PATH);
                final String storePath = json.getString(KName.STORE_PATH);
                if (Ut.isNotNil(filePath) && Ut.isNotNil(storePath)) {
                    directorySet.add(json.getString(KName.DIRECTORY_ID));
                    fileMap.put(filePath, storePath);
                }
            }
        });
        return executor.apply(directorySet.iterator().next(), fileMap);
    }

    private static Future<JsonArray> splitInternal(
        final JsonArray source,
        final Function<JsonArray, Future<JsonArray>> fnLocal,
        final Function<JsonArray, Future<JsonArray>> fnRemote) {
        final JsonArray dataL = new JsonArray();
        final JsonArray dataR = new JsonArray();
        Ut.itJArray(source).forEach(item -> {
            final String directoryId = item.getString(KName.DIRECTORY_ID);
            if (Ut.isNil(directoryId)) {
                dataL.add(item);
            } else {
                dataR.add(item);
            }
        });
        log.info("{} 数据拆分，目录文件数量：本地 = {}，远程 = {}",
            AtConstant.K_PREFIX_AMB, dataL.size(), dataR.size());
        final List<Future<JsonArray>> futures = new ArrayList<>();
        if (Ut.isNotNil(dataL)) {
            futures.add(fnLocal.apply(dataL));
        }
        if (Ut.isNotNil(dataR)) {
            futures.add(fnRemote.apply(dataR));
        }
        return Fx.compressA(futures);
    }
}
