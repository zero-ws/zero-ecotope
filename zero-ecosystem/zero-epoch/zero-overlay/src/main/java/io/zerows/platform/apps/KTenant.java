package io.zerows.platform.apps;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.exception._40102Exception500CombineOwner;
import io.zerows.specification.app.HAmbient;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HLot;
import io.zerows.specification.cloud.HTenant;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.specification.security.HOwner;
import io.zerows.support.base.UtBase;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「租户维度」
 * 多租户专用的租户对象标识（该对象规范从 {@see XHeader} 中提取
 * <pre>
 *     - tenantId / sigma: 统一标识
 *     - language：语言信息
 *     - appKey / id / appName （内置包含 {@link HApp}
 *     拥有者ID，特殊维度（云环境专用，处理核心维度）
 * </code>
 * <pre>
 *     新版对接协议
 *         实现：              {@link HRegistry}
 *         默认：              {@link RegistryCommon}
 *         zero-ambient:      {@see RegistryExtension}
 *                               |
 *                               |
 *                               |
 *                            ( initialized )
 *                            {@link HAmbient}  -----------> {@link HLot}
 *                               |
 *                               |
 *                            {@link HArk} ( running ) ----> key ---------> {@link HLot}
 *                               |
 *                               |
 *         connect:           1st / sigma,   tenantId
 *                                  X_SIGMA, X_TENANT_ID
 *                            2nd / id ( appId ), code, name
 *                                  X_APP_ID
 *                            3rd / appKey
 *                                  X_APP_KEY
 *
 *     充当了上下文环境的应用/租户 请求信息，和 HArk 不同的点在于，当前静态中，可执行全局化调用，
 *     且请求产生时执行初始化，依赖 XHeader 中数据
 * </code></pre>
 * <p>
 * > 为了区别于 {@link HTenant} 和 {@link HOwner}
 * 此处的拼写改成 Tenant，具有多层租户含义，对应接口为 {@link HLot}
 *
 * @author lang : 2023-06-06
 */
class KTenant implements HLot {

    private final static Cc<String, HLot> CC_TENANT = Cc.open();
    /**
     * 子租户容器，支持多级租户架构
     */
    private final ConcurrentMap<String, HLot> children = new ConcurrentHashMap<>();

    /**
     * 租户标识 (Tenant ID / Owner ID)
     */
    private final String id;

    /**
     * 租户元数据存储 (Metadata)
     */
    private final JsonObject data = new JsonObject();

    private KTenant(final String id) {
        // 使用 UtBase 生成标准化的 Owner ID
        this.id = id;
    }

    public static HLot getOrCreate(final String id) {
        // 租户ID
        final String tenantId = UtBase.keyOwner(id);
        return CC_TENANT.pick(() -> new KTenant(tenantId), tenantId);
    }

    // ------------------- HBelong 接口实现 -------------------

    @Override
    public String owner() {
        return this.id;
    }

    // ------------------- HLot 接口实现 (Child) -------------------

    @Override
    public HLot child(final String id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return this.children.getOrDefault(id, null);
    }

    @Override
    public void child(final String id, final HLot hoi) {
        if (Objects.nonNull(id)) {
            Optional.ofNullable(hoi).ifPresent(h -> this.children.put(id, h));
        }
    }

    // ------------------- HLot 接口实现 (Data) -------------------

    @Override
    public JsonObject data() {
        return this.data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T data(final String field) {
        if (Objects.isNull(field) || field.isBlank()) {
            return null;
        }
        // 直接从 JsonObject 中提取值，依赖 Vert.x 的类型转换
        // 如果需要支持 "a.b.c" 这种路径提取，可引入 Ut.visit(data, field)
        return (T) this.data.getValue(field);
    }

    @Override
    public JsonObject data(final JsonObject data) {
        if (Objects.nonNull(data)) {
            // 使用 mergeIn 进行深合并 (true)，保留已有字段，更新新字段
            this.data.mergeIn(data, true);
        }
        return this.data;
    }

    @Override
    public <T> void data(final String field, final T value) {
        if (Objects.nonNull(field)) {
            // value 为 null 时可能无法 put，视 Vert.x 版本而定，这里不做非空检查以允许移除操作(如果 put null 是移除语义)
            // 通常 put null 在 Vert.x JsonObject 中是合法的
            this.data.put(field, value);
        }
    }

    // ------------------- Function 接口实现 -------------------

    @Override
    public HLot apply(final HLot target) {
        if (Objects.nonNull(target)) {
            // 防止租户合并时出现 ID 不一致的严重错误
            if (!target.equals(this)) {
                throw new _40102Exception500CombineOwner(this.id, target.owner());
            }
            // 合并元数据
            this.data(target.data());
            // 合并子租户 (如果有)
            // 注意：此处仅做顶层数据合并，暂不递归合并子节点，视业务需求而定
        }
        return this;
    }

    // ------------------- Object 基础方法 -------------------

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KTenant owner = (KTenant) o;
        return Objects.equals(this.id, owner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "KTenant{id='" + this.id + '\'' + ", children=" + this.children.size() + '}';
    }
}
