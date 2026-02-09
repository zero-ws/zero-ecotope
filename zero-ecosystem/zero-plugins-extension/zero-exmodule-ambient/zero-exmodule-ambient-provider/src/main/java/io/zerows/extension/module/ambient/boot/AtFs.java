package io.zerows.extension.module.ambient.boot;

import io.r2mo.io.common.HFS;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.assembly.DI;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.module.ambient.common.AtConstant;
import io.zerows.extension.module.ambient.serviceimpl.DocBuilder;
import io.zerows.extension.module.ambient.servicespec.DocBStub;
import io.zerows.extension.skeleton.common.KeBiz;
import io.zerows.extension.skeleton.spi.ExIo;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.spi.HPI;
import io.zerows.support.Fx;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    private static final DI PLUGIN = DI.create(AtFs.class);

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
                log.info("{} 删除本地文件，数量：{}", AtConstant.K_PREFIX, files.size());
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

    @SuppressWarnings("all")
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
            AtConstant.K_PREFIX, dataL.size(), dataR.size());
        final List<Future<JsonArray>> futures = new ArrayList<>();
        if (Ut.isNotNil(dataL)) {
            futures.add(fnLocal.apply(dataL));
        }
        if (Ut.isNotNil(dataR)) {
            futures.add(fnRemote.apply(dataR));
        }
        return Fx.compressA(futures);
    }

    /**
     * 此处必须详细备注，在整个 Debug 过程中算是被折腾哭了的一个方法，此方法的完整步骤：
     * <pre><code>
     *     1. 先根据 storePath 中的所有合法路径（梯度路径）提取所有的目录记录。
     *     2. 调用 {@link ExIo} 对目录执行相关操作，包括：提取、校验、同步。
     *     3. 将新追加的目录主键追加到附件中（同步之后，directoryId 目录主键）。
     * </code></pre>
     *
     * @param attachment 附件信息
     * @param params     参数信息
     * @return {@link Future}
     */
    static Future<JsonArray> fileDir(final JsonArray attachment, final JsonObject params) {

        /*
         * 步骤一：
         * 此处基于 `directory` 重新计算最终存储的路径，`directory` 的版本是新版本的功能
         * 不仅如此，此处还会启用 JEXL 解析 `directory` 字段，解析的结果作为最终存储路径来
         * 对待，动态目录就是在这里被解析的，如带一个 `${code}` 的表达式目录。
         * 1 - 若 `directory` 为空，不解析
         * 2 - 若 `directory` 包含 "`" 字符，格式化表达式（JEXL解析）
         * 3 - 若 `directory` 不包含 "`" 字符，不解析
         *
         * 此处的版本更加安全，因为系统会检测原始输入字符串，检测是否可以被解析。
         */
        final String directory = Ut.valueString(attachment, KName.DIRECTORY);
        final String storePath = Ut.fromExpression(directory, params);


        /*
         * 步骤二：
         * 1 - 直接从参数中提取 `sigma` 信息，在单应用模式下，`sigma` 和 `id` 是等价概念。
         * 2 - 根据查询条件读取目录信息，此处会访问 I_DIRECTORY 中读取整棵树的结构。
         * 此处的查询条件
         * -- storePath：传入的是 `List<String>` 的梯度路径格式，抽取最短的路径执行 `startWith` 模式的提取
         *    二次检查会将查询结果和 `List<String>` 比对，这次检索目录是查看是否存在 `I_DIRECTORY` 记录对后续执行计算
         * -- sigma：模型统一标识符
         */
        final String sigma = params.getString(KName.SIGMA);
        final List<String> paths = Ut.ioPathSet(storePath);

        return HPI.of(ExIo.class).waitAsync(io -> io.dirTree(sigma, paths).compose(directories -> {


                /*
                 * 步骤三：
                 * 基于名称对目录执行初始化操作，初始化结构如：
                 * 默认的树为：
                 * /apps/<name>/document
                 * /apps/<name>/document/XXXX  ( Root根路径 )
                 *
                 * - `<name>`：此参数是应用的专用参数，和 `/apps/<name>/document` 完整配置到 zero-ambient 模块中，参考白皮书实现定制
                 *
                 * 此处的输入可能是：
                 *
                 * /apps/<name>/document/XXXX/AA/BB
                 *
                 * 这种情况下，AA 和 BB 就是此处的动态目录，动态目录在这个过程中应该在执行文件处理之前合法，这种场景下
                 * 会执行两个 API 来处理：
                 *
                 * - `seekDirectory`：第一次访问动态目录其上层路径是不存在的，所以依赖根路径的构造。
                 * - `dirTree`：第二次访问时此处的目录本身已经存在了，所以直接执行查询即可。
                 */
                if (directories.isEmpty()) {
                    // 检查目录专用算法
                    return seekDirectory(storePath, params);
                } else {
                    return Ux.future(directories);
                }
            }
        ).compose(directoryA -> {


            /*
             * 步骤四：
             * 配置目录的底层存储，并构造验证参数
             * 1. /apps/<name>/document         根目录
             * 2. /apps/<name>/document/XXXX    当前附件存储的路径信息
             *
             * 此处构造的输入参数格式如：
             * {
             *     "storePath": [],
             *     "sigma": "统一标识符",
             *     “updatedBy": "更新人"
             * }
             * 底层调用验证接口，确保路径的正确性
             */
            final JsonObject input = new JsonObject();
            input.put(KName.STORE_PATH, Ut.toJArray(paths));
            input.put(KName.SIGMA, sigma);
            input.put(KName.UPDATED_BY, params.getValue(KName.UPDATED_BY));
            return io.verifyIn(directoryA, input);
        })).compose(directoryJ -> {


            /*
             * 步骤五：
             * 响应替换流程，替换掉对应的属性信息（输入源为验证后的结果）
             * - directoryId，引用 I_DIRECTORY 中的记录，此处为上传附件的直接父目录主键值
             * - storePath，目录计算出来之后，可以根据目录的基础信息计算附件本身应该有的 storePath（虽然也是相对路径，但带有目录信息）
             * - storeWay，I_DIRECTORY 中可计算是否开启了集成功能，一旦开启，那么此处的 storeWay 就会是不同的值
             */
            final JsonObject verified = Ut.valueJObject(directoryJ);
            Ut.itJArray(attachment).forEach(content -> {
                content.put(KName.DIRECTORY_ID, verified.getString(KName.KEY));
                content.put(KName.STORE_PATH, Ut.ioPath(storePath, content.getString(KName.NAME)));
                content.put(KName.Attachment.STORE_WAY, verified.getString(KName.TYPE));
            });
            return Ux.future(attachment);
        });
    }

    private static Future<JsonArray> seekDirectory(final String storePath, final JsonObject params) {
        final DocBStub builder = PLUGIN.createSingleton(DocBuilder.class);


        // 提取 id / type 两个参数
        final String appId = params.getString(KName.APP_ID);
        final String type = KeBiz.TypeEntity.Directory.value();


        // 计算目录名称，相对路径提取名称为绝对路径，内置使用 DocBuilder 进行同步构建。
        final AtConfig config = Objects.requireNonNull(MANAGER.config());
        final String rootPath = config.getStorePath();

        String name = storePath.replace(rootPath, VString.EMPTY);
        name = Ut.ioPathRoot(name);
        log.info("{} Zero 将重新初始化目录以尝试处理：{}", AtConstant.K_PREFIX, storePath);
        log.info("{} 构建器参数：name = {}, type = {}, id = {}", AtConstant.K_PREFIX, name, type, appId);
        return builder.initialize(appId, type, name);
    }
}
