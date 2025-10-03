package io.zerows.component.normalize;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.ChatGPT;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.configuration.MDConnect;
import io.zerows.epoch.configuration.MDEntity;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-12
 */
class MakerEntity implements MakerIo<MDEntity> {
    @Override
    public ConcurrentMap<String, MDEntity> build(final String modelDir, final Bundle owner,
                                                 final Object... args) {
        Objects.requireNonNull(args[0]);        // args[0] = Map ( table = MDConnect )
        // 遍历 modelDir 目录提取当前环境中所有定义的实体信息
        final ConcurrentMap<String, MDEntity> entityMap = new ConcurrentHashMap<>();
        final List<String> pathes;
        if (Objects.isNull(owner)) {
            // 非 OSGI 环境
            final List<String> dirs = Ut.ioDirectories(modelDir);
            pathes = dirs.stream()
                .map(identifier -> Ut.ioPath(modelDir, identifier))
                .toList();
        } else {
            // OSGI 环境
            pathes = Ut.Bnd.ioDirectory(modelDir, owner);
        }


        Objects.requireNonNull(pathes);
        pathes.stream()
            .map(path -> this.buildOne(path, owner, args))
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
     *
     * @param dir   子目录名称
     * @param owner Bundle
     * @param args  参数
     *
     * @return {@link MDEntity}
     */
    @Override
    @SuppressWarnings("unchecked")
    public MDEntity buildOne(final String dir, final Bundle owner, final Object... args) {

        final String idOfDir = this.buildId(dir);
        // model/<identifier>/entity.json / 一旦定义了实体，此处必须包含 entity.json 文件
        final JsonObject entityJ = Ut.Bnd.ioJObject("entity.json", owner, dir);
        // model/<identifier>/column.json
        JsonArray columnA = new JsonArray();
        if (Ut.Bnd.ioExist("column.json", owner, dir)) {
            columnA = Ut.Bnd.ioJArray("column.json", owner, dir);
        }

        final String idOfFile = Ut.valueString(entityJ, KName.IDENTIFIER);


        // 构造 MDEntity
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

        final MDConnect found;
        if (connectMap.containsKey(daoCls)) {
            // daoCls 配置的是表名
            found = connectMap.getOrDefault(daoCls, null);

            // 替换
            entityJ.put("daoCls", found.getDao().getName());
        } else {
            // daoCls 配置的并非表名，而是类名，类名要检索
            found = connectMap.values().stream()
                /*
                 * 类名检索，数据层面必须找到，否则底层执行会不正常，一般一个模块 Bundle 中 MDConnect 是全的，而 MDEntity 可能会不全，
                 * 只有定义 crud 模块才会启动 MDEntity 部分，所以这里的检索一定会找到作为基本配置合规的前提
                 */
                .filter(connect -> connect.getDao().getName().equals(daoCls))
                .findFirst().orElse(null);
            if (Objects.isNull(found)) {
                Ut.Log.uca(this.getClass()).warn("The daoCls = {0} could not be found in MDConnect, ", daoCls);
            }
            Objects.requireNonNull(found);
        }


        // Bind：entity 和 connect
        return entity.bind(entityJ).bind(found);
    }

    @ChatGPT
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
