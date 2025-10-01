package io.zerows.extension.runtime.integration.util;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.program.Kv;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.common.log.Log;
import io.zerows.epoch.common.log.LogModule;
import io.zerows.extension.runtime.integration.domain.tables.pojos.IDirectory;
import io.zerows.extension.runtime.integration.uca.command.Fs;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class Is {
    /*
     * Trash Path calculation
     */
    public static Kv<String, String> trashIn(final IDirectory directory) {
        Objects.requireNonNull(directory);
        return IsDir.trash(directory.getStorePath());
    }

    public static Kv<String, String> trashIn(final JsonObject directoryJ) {
        final String path = directoryJ.getString(KName.STORE_PATH);
        Objects.requireNonNull(path);
        return IsDir.trash(path);
    }

    public static ConcurrentMap<String, String> trashIn(final Set<String> pathSet) {
        return IsDir.trash(pathSet);
    }

    public static Kv<String, String> trashOut(final IDirectory directory) {
        Objects.requireNonNull(directory);
        return IsDir.rollback(directory.getStorePath());
    }

    public static Kv<String, String> trashOut(final JsonObject directoryJ) {
        final String path = directoryJ.getString(KName.STORE_PATH);
        Objects.requireNonNull(path);
        return IsDir.rollback(path);
    }

    public static ConcurrentMap<String, String> trashOut(final Set<String> pathSet) {
        return IsDir.rollback(pathSet);
    }

    public static JsonObject dataIn(final JsonObject input) {
        return IsDir.input(input);
    }

    public static JsonArray dataIn(final JsonArray input) {
        return IsDir.input(input);
    }

    public static Future<JsonObject> dataOut(final JsonObject output) {
        return IsDir.output(output);
    }

    public static Future<JsonArray> dataOut(final JsonArray output) {
        return IsDir.output(output);
    }

    /*
     * X_DIRECTORY Operation
     */
    public static Future<List<IDirectory>> directoryQr(final JsonObject condition) {
        return IsDir.query(condition);
    }

    public static Future<List<IDirectory>> directoryQr(final IDirectory directory) {
        return IsDir.query(directory);
    }


    /**
     * 特殊目录查询模式，直接根据目录现存数据查询同步完成的目录清单
     * 查询流程如：
     * <pre><code>
     *     1. 从输入目录中提取 storePath 字段的数据集
     *        storePath 字段信息依靠 storeField 字段来提取（可定制其他字段，默认使用 storePath）
     *     2. strict 表示了提取模式
     *        - 严格模式，直接使用 IN 语法，严格提取独立目录数据
     *        - 非严格模式，使用 LIKE 语法，模糊提取目录数据，以目录前缀为准
     * </code></pre>
     *
     * @param data       JsonArray 目录基础数据
     * @param storeField 目录字段
     * @param strict     严格模式和非严格模式
     *
     * @return 从系统中读取的所有目录信息
     */
    public static Future<List<IDirectory>> directoryQr(final JsonArray data, final String storeField, final boolean strict) {
        return IsDir.query(data, storeField, strict);
    }

    public static Future<IDirectory> directoryBranch(final String key, final String updatedBy) {
        return IsDir.updateBranch(key, updatedBy);
    }

    public static Future<IDirectory> directoryLeaf(final JsonArray directoryJ, final JsonObject params) {
        return IsDir.updateLeaf(directoryJ, params);
    }

    /*
     * X_DIRECTORY `runComponent` execution
     */
    public static Future<JsonObject> fsRun(final JsonObject data, final Function<Fs, Future<JsonObject>> fsRunner) {
        return IsFs.run(data, fsRunner);
    }

    public static Future<JsonArray> fsRun(final JsonArray data, final BiFunction<Fs, JsonArray, Future<JsonArray>> fsRunner) {
        return IsFs.run(data, fsRunner);
    }

    public static Future<ConcurrentMap<Fs, Set<String>>> fsGroup(final ConcurrentMap<String, String> fileMap) {
        return IsFs.group(fileMap);
    }

    public static <V> ConcurrentMap<Fs, V> fsGroup(final ConcurrentMap<String, V> map, final Predicate<V> fnKo) {
        return IsFs.group(map, fnKo);
    }

    public static ConcurrentMap<Fs, Set<String>> fsCombine(final ConcurrentMap<Fs, Set<String>> directoryMap,
                                                           final ConcurrentMap<Fs, Set<String>> fileMap) {
        return IsFs.combine(directoryMap, fileMap);
    }

    public static Future<Fs> fsComponent(final String directoryId) {
        return IsFs.component(directoryId);
    }


    /**
     * 此接口为存储的核心数据结构，其中包含了目录的基本数据集，最典型的数据结构如：
     * <pre><code>
     *     data = []
     *     - storePath:      当前目录的存储路径
     *     - storeParent:    当前目录的父目录存储路径
     *     config = {}
     *     - runComponent:   当前目录挂载的执行组件名称
     *     - storePath:      当前目录的基础存储路径
     * </code></pre>
     *
     * 注：storeRoot 部分是在加载时完成，所以storeRoot部分的数据会直接受到两部分内容影响
     * <pre><code>
     *     - 环境变量（新）：Z_SIS_STORE 中指定了初始化目录的根目录
     *     - 配置：plugin/is/configuration.json 配置中
     *     {
     *         "storeRoot": "目录根路径"
     *     }
     * </code></pre>
     * 上述列表指定了优先级信息，数据库中只存储相对路径，不存储绝对路径，以保证整体迁移，例：
     * <pre><code>
     *     storePath        = /apps/xc/document/系统文档
     *     storeParent      = /apps/xc/document/
     * </code></pre>
     *
     * @param data   读取的目录基础数据
     * @param config 配置数据集
     *
     * @return 返回同步完成的所有目录
     */
    public static Future<JsonArray> fsDocument(final JsonArray data, final JsonObject config) {
        return IsStore.document(data, config);
    }

    public interface LOG {
        String MODULE = "Ολοκλήρωση";

        LogModule Init = Log.modulat(MODULE).extension("Init");
        LogModule Web = Log.modulat(MODULE).extension("Web");
        LogModule File = Log.modulat(MODULE).extension("File/Directory");
        LogModule Path = Log.modulat(MODULE).extension("Path");
    }
}
