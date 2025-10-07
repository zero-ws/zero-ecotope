package io.zerows.epoch.metadata;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * # 🔄 链接器数据，用于以下内容
 * <pre><code>
 * 1. 🧩 系统字段信息:
 * - createdBy    : 👤 创建人
 * - createdAt    : ⏰ 创建时间
 * - updatedBy    : 👤 更新人
 * - updatedAt    : ⏰ 更新时间
 * - language     : 🌐 语言
 * - sigma        : 🔑 系统标识
 * - active       : ✅ 激活状态
 * - appId        : 🆔 应用ID
 * - tenantId     : 🏢 租户ID
 * 2. 📋 系统二级信息:
 * - key          : 🔑 系统主键
 * - code         : 🏷️ 系统编码
 * - name         : 📝 系统名称
 * - type         : 🏷️ 系统类型
 * - category     : 📁 系统分类
 * - serial       : 📊 系统编号和序列化
 * 3. 🔗 系统链接信息:
 * - modelCategory: 📁 模型分类
 * - modelId      : 🆔 模型ID
 * - modelKey     : 🔑 模型主键
 * - reference    : 🔗 引用ID
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class MMName implements Serializable {
    // 🔍 查询条件专用
    private final Set<String> qrKeys = new HashSet<>();
    // 📋 系统业务字段
    private String key;
    private String code;
    private String name;
    private String type;
    private String category;
    private String serial;
    // 🧩 系统默认字段
    private String language;
    private String sigma;
    private Boolean active;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    // 🏢 租户和应用字段
    private String appId;
    private String tenantId;
    // 🔗 链接字段
    private String reference;
    private String identifier;
    private String modelKey;
    private String modelCategory;

    public void setQrKeys(final Set<String> qrKeys) {
        this.qrKeys.clear();
        this.qrKeys.addAll(qrKeys);
    }

    public boolean multiple() {
        return !this.qrKeys.isEmpty();
    }

    @Override
    public String toString() {
        return "KSpec{" +
            "key='" + this.key + '\'' +
            ", code='" + this.code + '\'' +
            ", name='" + this.name + '\'' +
            ", type='" + this.type + '\'' +
            ", category='" + this.category + '\'' +
            ", serial='" + this.serial + '\'' +
            ", language='" + this.language + '\'' +
            ", sigma='" + this.sigma + '\'' +
            ", active=" + this.active +
            ", createdBy='" + this.createdBy + '\'' +
            ", createdAt=" + this.createdAt +
            ", updatedBy='" + this.updatedBy + '\'' +
            ", updatedAt=" + this.updatedAt +
            ", appId='" + this.appId + '\'' +
            ", tenantId='" + this.tenantId + '\'' +
            ", reference='" + this.reference + '\'' +
            ", identifier='" + this.identifier + '\'' +
            ", modelKey='" + this.modelKey + '\'' +
            ", modelCategory='" + this.modelCategory + '\'' +
            '}';
    }
}