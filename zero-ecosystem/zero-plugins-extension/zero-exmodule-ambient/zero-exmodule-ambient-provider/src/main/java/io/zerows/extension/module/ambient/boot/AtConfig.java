package io.zerows.extension.module.ambient.boot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.function.Fn;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.zerows.extension.module.ambient.domain.tables.pojos.XAttachment;
import io.zerows.extension.module.ambient.exception._80302Exception500InitSpecification;
import io.zerows.extension.module.ambient.exception._80303Exception500PrerequisiteSpec;
import io.zerows.extension.skeleton.spi.ExInit;
import io.zerows.extension.skeleton.spi.ExPrerequisite;
import io.zerows.plugins.excel.ExcelClient;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

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
     * 存储路径，对应 {@link XAttachment#getStorePath()}
     */
    private String storePath;
    /**
     * 集成配置，对应 zero-exmodule-integration 模块中集成相关
     */
    private String fileIntegration;
    /**
     * 数据导入的专用数据目录，{@link ExcelClient} 导入数据时必须存在
     */
    private String dataFolder;

    // ------------------- 替换原始的 ExInit 特定配置 -------------------

    public ExInit ofInit() {
        if (Objects.isNull(this.initializer)) {
            return null;
        }
        Fn.jvmKo(!Ut.isImplement(this.initializer, ExInit.class), _80302Exception500InitSpecification.class, this.initializer.getName());
        return ExInit.of(this.initializer);
    }

    public ExInit ofLoad() {
        if (Objects.isNull(this.loader)) {
            return null;
        }
        Fn.jvmKo(!Ut.isImplement(this.loader, ExInit.class), _80302Exception500InitSpecification.class, this.loader.getName());
        return ExInit.of(this.loader);
    }

    public ExPrerequisite ofPre() {
        if (Objects.isNull(this.prerequisite)) {
            return null;
        }
        Fn.jvmKo(!Ut.isImplement(this.prerequisite, ExPrerequisite.class), _80303Exception500PrerequisiteSpec.class, this.prerequisite.getName());
        return ExPrerequisite.of(this.prerequisite);
    }
}
