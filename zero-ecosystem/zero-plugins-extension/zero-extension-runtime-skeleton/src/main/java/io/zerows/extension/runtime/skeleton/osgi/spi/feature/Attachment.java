package io.zerows.extension.runtime.skeleton.osgi.spi.feature;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Set;

/**
 * 上层附件处理器，提供了和附件相关的所有操作
 * <pre><code>
 *     - 1. 插入新附件
 *     - 2. 删除旧附件
 * </code></pre>
 * 此处的条件如下：
 * // <pre><code class="json">
 * {
 *     "modelId": "广义关联部分的模型统一标识符 identifier",
 *     "modelCategory": "模型中的 category 属性，和分类树直接绑定，附件中的字段：MODEL_CATEGORY"
 * }
 * // </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Attachment {
    Future<JsonArray> uploadAsync(JsonArray data);

    Future<JsonArray> uploadAsync(JsonArray data, JsonObject params);

    /**
     * 直接创建附件的接口，只负责附件上传，不负责附件的集成部分的关联，参数构造不来自于外部，而是内部
     * 直接从 {@link JsonArray} 中提取，提取的数据结构如：
     * <pre><code>
     *     {
     *         "sigma": "模型统一标识符",
     *         "directory": "目录路径信息",
     *         "updatedBy": "更新人"
     *     }
     * </code></pre>
     * 此接口内部调用了两个核心方法（若传输的数据不为 [] 或 null）
     * <pre><code>
     *     1. {@see updateParams} 构造上述提到的结构化参数。
     *     2. {@link Attachment#uploadAsync(JsonArray, JsonObject)} 方法上传附件。
     * </code></pre>
     *
     * @param data 参数信息
     *
     * @return {@link Future}
     */
    Future<JsonArray> saveAsync(JsonObject condition, JsonArray data);

    /**
     * 附件上传接口，此接口提供了附件上传的核心逻辑，包括，此处会提取原始附件信息然后进行上传，它的执行流程如下：
     * <pre><code>
     *     1. 比对旧附件 / 新附件，计算增删的结果。
     *     2. 得到附件本身的三个执行通道：ADD / UPDATE / DELETE
     *        - ADD：        添加通道，上传新附件
     *        - DELETE：     删除通道，删除旧附件
     *        - UPDATE：     附件本身已经存在此处不再做任何操作
     * </code></pre>
     * 之所以更新什么都不做，在于更新部分维持原始附件引用地址，不可更改对应的 metadata 操作，若更改了 metadata
     * 如 fileKey 或其他核心的元数据，那么附件本身有可能会失效。
     *
     * @param data   附件数据
     * @param params 附件参数
     *
     * @return {@link Future}
     */
    Future<JsonArray> saveAsync(JsonObject condition, JsonArray data, JsonObject params);

    /*
     * 1. Remove Original Only
     */
    Future<JsonArray> removeAsync(JsonObject condition);

    Future<JsonArray> purgeAsync(JsonArray attachment);

    Future<JsonArray> updateAsync(JsonArray attachment, boolean active);

    /*
     * 1. Fetch attachments in single field
     * 2. Here deeply fetch will put `visit` information into attachment
     *    to inherit from `directory`
     */
    Future<JsonArray> fetchAsync(JsonObject condition);

    // ----------------- File Interface ----------------------
    Future<Buffer> downloadAsync(Set<String> keys);

    Future<Buffer> downloadAsync(String key);

    // ----------------- Remove Condition ----------------------
}
