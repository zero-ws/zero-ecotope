package io.zerows.module.metadata.atom.configuration.modeling;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;

import java.io.Serializable;
import java.util.Objects;

/**
 * 改写原始的 KModule 流程，修订所有的底层配置部分，让配置变得更加纯粹，直接在 {@link MDEntity} 中进行基础模块的配置，完整配置则
 * 可直接管理起来，且 MDConnect 部分可直接忽略不计，直接通过表名直接引用即可，这样省略掉 Excel 中的多一层操作，直接通过 Excel 中解析的表名
 * 就可定位当前导入 {@link MDEntity} 实体的基本配置信息，且连接上 KModule，实体信息在三个地方有可能会被引用。
 * <pre><code>
 *     1. CRUD 引擎中，直接从 MDEntity 中提取 KModule 相关信息
 *     2. 工作流引擎中，KHybrid 和 KClass 两层消费封装内置实体
 *     3. Excel 导入过程中，要根据表名提取信息，替换原始的 MDConnect 的信息，省掉这个步骤，直接消费实体信息
 * </code></pre>
 * 对于一个实体而言，有可能不存在 moduleJ，系统未配置 CRUD 引擎消费内容，但是一定会存在 MDConnect 部分，最底层骨架应该是 MDConnect，
 * 和表名直接挂钩的内容，解析过程中会直接消费这个部分，而 moduleJ 本身是可选的，存在 moduleJ 证明这个实体是可以被 CRUD 识别的，identifier
 * 在此处严格按照目录名进行约定，以实现实体的唯一化，配合外层的 应用 和 名空间，将所有实体统一管理起来。
 *
 * @author lang : 2024-05-07
 */
public class MDEntity implements Serializable {
    // CRUD 专用配置
    private final JsonObject moduleJ = new JsonObject();
    // 实体表名衍生出来的列配置信息
    private final JsonArray columns = new JsonArray();
    // 实体对应的 identifier 标识符
    private final String identifier;
    // Excel 专用配置（内置 MDMeta）
    private MDConnect connect;

    public MDEntity(final String identifier) {
        this.identifier = identifier;
    }

    public String identifier() {
        return this.identifier;
    }

    public JsonArray inColumns() {
        return this.columns;
    }

    public JsonObject inModule() {
        return this.moduleJ;
    }

    public MDConnect refConnect() {
        return this.connect;
    }

    public MDEntity bind(final JsonObject moduleJ) {
        if (Ut.isNotNil(moduleJ)) {
            this.moduleJ.mergeIn(moduleJ, true);
        }
        return this;
    }

    public MDEntity bind(final JsonArray columns) {
        if (Ut.isNotNil(columns)) {
            this.columns.addAll(columns);
        }
        return this;
    }

    public MDEntity bind(final MDConnect connect) {
        if (Objects.nonNull(connect)) {
            this.connect = connect;
        }
        return this;
    }
}
