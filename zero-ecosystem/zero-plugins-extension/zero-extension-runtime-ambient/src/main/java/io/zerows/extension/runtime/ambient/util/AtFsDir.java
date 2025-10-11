package io.zerows.extension.runtime.ambient.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.assembly.DiPlugin;
import io.zerows.epoch.constant.KName;
import io.zerows.extension.runtime.ambient.agent.service.file.DocBStub;
import io.zerows.extension.runtime.ambient.agent.service.file.DocBuilder;
import io.zerows.extension.runtime.ambient.bootstrap.AtConfig;
import io.zerows.extension.runtime.ambient.bootstrap.AtPin;
import io.zerows.extension.runtime.skeleton.eon.em.BizInternal;
import io.zerows.extension.runtime.skeleton.osgi.spi.business.ExIo;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

import java.util.List;

import static io.zerows.extension.runtime.ambient.util.At.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class AtFsDir {
    private static final LogOf LOGGER = LogOf.get(AtFsDir.class);
    private static final DiPlugin PLUGIN = DiPlugin.create(AtFs.class);

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
     *
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

        return Ux.channel(ExIo.class, () -> null, io -> io.dirTree(sigma, paths).compose(directories -> {


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
        }).compose(directoryA -> {


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
        final String type = BizInternal.TypeEntity.Directory.value();


        // 计算目录名称，相对路径提取名称为绝对路径，内置使用 DocBuilder 进行同步构建。
        final AtConfig config = AtPin.getConfig();
        final String rootPath = config.getStorePath();

        String name = storePath.replace(rootPath, VString.EMPTY);
        name = Ut.ioPathRoot(name);
        LOG.File.info(LOGGER, "Zero will re-initialize directory try to process {0}", storePath);
        LOG.File.info(LOGGER, "The builder parameters: name = {0}, type = {1}, id = {2}",
            name, type, appId);
        return builder.initialize(appId, type, name);
    }
}
