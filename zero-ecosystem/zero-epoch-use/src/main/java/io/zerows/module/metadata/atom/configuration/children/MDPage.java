package io.zerows.module.metadata.atom.configuration.children;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.uca.metadata.MetaCachePage;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.atom.configuration.MDId;
import org.osgi.framework.Bundle;

import java.io.Serializable;
import java.util.Objects;

/**
 * 收集每个模块中 X_MODULE 中 metadata 引用的配置文件的值，形成一个完整的配置表，此处配置表直接和页面路径相关，且
 * 不会带上 /{route} 这种前缀，并且在 metadata 解析的时候会直接使用此配置表来完成，且 MDPage 的构造模式是固定的，
 * <pre><code>
 *      最终存储的结构如：
 *      {path} = MDPage
 *      ......
 * </code></pre>
 * 而在 Excel 导入过程中使用的是 WEB:{path} 的结构，这种结构是为了方便导入，这部分导入会直接跟随 {@link MDConfiguration}
 * 对象的构造来处理，一旦构造完成那么 MDPage 就会包含所有页面的配置信息。
 *
 * @author lang : 2024-06-25
 */
public class MDPage implements Serializable {

    private final MDId id;
    private final JsonObject configData = new JsonObject();
    private String key;
    private String filename;

    public MDPage(final MDId id) {
        this.id = id;
    }

    public MDPage configure(final String path) {
        // 环境区别
        final Bundle owner = this.id.owner();
        if (Objects.isNull(owner)) {
            // 非 OSGI
            final String dataPath = "plugins/" + this.id.value() + path;
            this.configData.mergeIn(Ut.ioJObject(dataPath), true);
            this.filename = dataPath.substring(dataPath.lastIndexOf("/") + 1);
            // 去掉文件名
            final String url = dataPath.substring(0, dataPath.lastIndexOf("/"));
            this.key = url.split("web")[1];
        } else {
            // OSGI 环境
            this.configData.mergeIn(Ut.Bnd.ioJObject(path, owner), true);
            this.filename = path.substring(path.lastIndexOf("/") + 1);
            final String url = path.substring(0, path.lastIndexOf("/"));
            this.key = url.split("web")[1];
        }
        // 填充 MetaCachePage，此处填充之后后续才可以读取到
        final MetaCachePage pageCache = MetaCachePage.singleton();
        pageCache.put(this.key, this.configData.copy());
        return this;
    }

    public String key() {
        return this.key;
    }

    public String filename() {
        return this.filename;
    }

    public JsonObject configData() {
        return this.configData;
    }
}
