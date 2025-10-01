package io.zerows.extension.runtime.workflow.bootstrap;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.VPath;
import io.zerows.epoch.constant.VString;
import io.zerows.epoch.program.Ut;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.zerows.extension.runtime.workflow.util.Wf.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
final class WfTodo {
    private static final ConcurrentMap<String, JsonObject> TODO_DEF =
        new ConcurrentHashMap<>();

    private WfTodo() {
    }

    /**
     * 遗留方法，用于读取旧版中：plugin/wf/todo/ 目录下的基本待确认相关配置，作为扩展配置对待
     * 待办信息的生成过程中，也可以根据此处的基础分类待办来处理对应内容
     */
    @Deprecated
    static void initLegacy() {
        final String FOLDER_TODO = "plugin/wf/todo/";
        if (TODO_DEF.isEmpty()) {
            final List<String> files = Ut.ioFiles(FOLDER_TODO, VPath.SUFFIX.JSON);
            LOG.Init.info(WfTodo.class, "Wf Todo Files: {0}", files.size());
            files.forEach(file -> {
                final String path = FOLDER_TODO + file;
                final JsonObject todoDef = Ut.ioJObject(path);
                final String key = file.replace(VString.DOT + VPath.SUFFIX.JSON, VString.EMPTY);
                TODO_DEF.put(key, todoDef);
            });
        }
    }

    /**
     * 返回对应类型的配置信息，直接传入分类（文件名），根据文件名提取待办基础配置
     *
     * @param type {@link java.lang.String} 传入的分类信息
     *
     * @return {@link io.vertx.core.json.JsonObject}
     */
    static JsonObject getTodo(final String type) {
        final JsonObject todoDef = TODO_DEF.get(type);
        return Objects.isNull(todoDef) ? new JsonObject() : todoDef.copy();
    }
}
