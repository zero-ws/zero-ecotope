package io.zerows.extension.module.ambient.boot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.zerows.extension.module.ambient.domain.tables.pojos.XAttachment;
import io.zerows.plugins.excel.ExcelClient;
import lombok.Data;

import java.io.Serializable;

/**
 * 对应 configuration.json 的核心配置类
 * <pre>
 *    {
 *        "initializer": "初始化应用接口",
 *        "prerequisite": "预处理应用接口",
 *        "loader": "应用数据加载接口"
 *    }
 * </pre>
 *
 * @author lang : 2025-12-22
 */
@Data
public class AtConfig implements Serializable {
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> initializer;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> prerequisite;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> loader;

    // ------------------ 当前模块的全局存储关联专用 ------------------
    /**
     * 附件属性关联 {@link XAttachment#getStoreWay()}
     */
    private String fileStorage;
    /**
     * 附件属性关联 {@link XAttachment#getLanguage()}
     */
    private String fileLanguage;
    /**
     * 集成配置，对应 zero-exmodule-integration 模块中集成相关
     */
    private String fileIntegration;
    /**
     * 数据导入的专用数据目录，{@link ExcelClient} 导入数据时必须存在
     */
    private String dataFolder;
    /**
     * 存储路径，对应 {@link XAttachment#getStorePath()}
     */
    private String storePath;
}
