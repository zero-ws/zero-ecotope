package io.zerows.epoch.jigsaw;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConnect;
import io.zerows.epoch.basicore.MDEntity;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.epoch.constant.KName;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-12
 */
@Slf4j
class MakerIoEntity extends MakerIoBase<MDEntity> {
    MakerIoEntity(final ZeroFs io) {
        super(io);
    }

    @Override
    public ConcurrentMap<String, MDEntity> build(final String modelDir,
                                                 final HBundle bundle,
                                                 final Object... args) {
        Objects.requireNonNull(args[0]);        // args[0] = Map ( table = MDConnect )
        // 遍历 modelDir 目录提取当前环境中所有定义的实体信息
        final ConcurrentMap<String, MDEntity> entityMap = new ConcurrentHashMap<>();
        // 非 OSGI 环境
        final List<String> dirs = this.io().inDirectories(modelDir);


        Objects.requireNonNull(dirs);
        dirs.stream()
            .map(path -> this.buildOne(path, bundle, args))
            .forEach(entity -> entityMap.put(entity.identifier(), entity));
        return entityMap;
    }

    /**
     * 此处的 args[0] = Map ( table = MDConnect )，仅限内部调用，若从外部调用，则依旧要求首参信息
     * <pre><code>
     *     identifier 计算规则
     *     1. 第一优先级是 entity.json 中已经配置了 "identifier"
     *     2. 第二优先级是当前目录名
     *     新版 identifier 不根据表名执行计算，但是表名用于提取 {@link MDConnect} 的唯一依据：
     *     1. 若表名在传入的 Map 中不存在，则规则为此时 daoCls 配置的就是 Java 类全名而并非表名，留给上层 KModule 去执行。
     *     2. 若表名存在则直接修改 entityJ 中的 daoCls 字段
     * </code></pre>
     * 新版统一路径信息，如果此处是 dir，则 idFile 的计算从 plugins/{id}/model/{identifier}/entity.json 开始计算，由于此处传
     * 入的 {@link ZeroFs} 已经包含了之前的 plugins/{id}，所以 idFile 的最终计算结果应该是 model/{identifier}/entity.json，
     * 这种模式下也同样适用于 OSGI 环境和远程环境，其语义转换成唯一
     * <pre>
     *     {@link ZeroFs} 从模块根目录开始读取
     *     1. 单机环境下模块根目录为 plugins/{mid}，防止多个模块在一起的冲突
     *     2. 模块环境下根目录为当前模块目录
     *     3. 远程环境下可以直接替换 {@link ZeroFs} 的实现让它支持远程读取
     * </pre>
     *
     * @param dir  子目录名称
     * @param args 参数
     *
     * @return {@link MDEntity}
     */
    @Override
    @SuppressWarnings("unchecked")
    public MDEntity buildOne(final String dir, final HBundle bundle, final Object... args) {

        final String idOfDir = this.buildId(dir);
        // model/<identifier>/entity.json / 一旦定义了实体，此处必须包含 entity.json 文件
        final String idFile = Ut.ioPath("model/" + idOfDir, "entity.json");
        final JsonObject entityJ = this.io().inJObject(idFile);
        // model/<identifier>/column.json
        final JsonArray columnA = new JsonArray();
        final String idOfFile = Ut.valueString(entityJ, KName.IDENTIFIER);


        // --------------- 构造 MDEntity
        final MDEntity entity;
        if (Ut.isNil(idOfFile)) {
            // Bind：列
            entity = new MDEntity(idOfDir).bind(columnA);
        } else {
            // Bind：列
            entity = new MDEntity(idOfFile).bind(columnA);
        }

        final ConcurrentMap<String, MDConnect> connectMap = (ConcurrentMap<String, MDConnect>) args[0];
        final String daoCls = Ut.valueString(entityJ, "daoCls");

        final MDConnect found = this.findOr(connectMap, daoCls);
        if (Objects.nonNull(found)) {
            // 替换
            entityJ.put("daoCls", found.getDao().getName());
        }

        // 最终构造模型对应的元数据绑定信息 / Bind：entity 和 connect
        return entity.bind(entityJ).bind(found);
    }

    private MDConnect findOr(final ConcurrentMap<String, MDConnect> stored, final String daoCls) {
        // --------------- 查找 MDConnect
        final MDConnect found;
        if (stored.containsKey(daoCls)) {
            // daoCls 配置的是表名
            found = stored.getOrDefault(daoCls, null);
        } else {
            // daoCls 配置的并非表名，而是类名，类名要检索
            found = stored.values().stream()
                /*
                 * 类名检索，数据层面必须找到，否则底层执行会不正常，一般一个模块 Bundle 中 MDConnect 是全的，而 MDEntity 可能会不全，
                 * 只有定义 crud 模块才会启动 MDEntity 部分，所以这里的检索一定会找到作为基本配置合规的前提
                 */
                .filter(connect -> connect.getDao().getName().equals(daoCls))
                .findFirst().orElse(null);
            if (Objects.isNull(found)) {
                log.warn("[ ZERO ] The daoCls = {} 无法找到 MDConnect, ", daoCls);
            }
            Objects.requireNonNull(found);
        }
        return found;
    }

    private String buildId(final String path) {
        // 确保路径不为空
        if (path == null || path.isEmpty()) {
            return "";
        }

        // 移除路径末尾的斜杠或反斜杠（如果存在）
        final String normalizedPath = (path.endsWith("/") || path.endsWith("\\")) ? path.substring(0, path.length() - 1) : path;

        // 使用正则表达式来分割路径，兼容 '/' 和 '\'
        final String[] parts = normalizedPath.split("[/\\\\]");
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
}
