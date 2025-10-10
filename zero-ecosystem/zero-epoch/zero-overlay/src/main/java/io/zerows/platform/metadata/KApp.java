package io.zerows.platform.metadata;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;
import io.zerows.platform.constant.VName;
import io.zerows.platform.exception._40101Exception500CombineApp;
import io.zerows.specification.app.HApp;
import io.zerows.specification.development.HLog;
import io.zerows.support.base.UtBase;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2023-06-06
 */
@Data
@Accessors(chain = true)
@Slf4j
public class KApp implements HApp, HLog {

    private String id;
    private String name;
    private String ns;
    private String tenant;
    private final JsonObject configuration = new JsonObject();
    private final JsonObject data = new JsonObject();

    /**
     * 🎲 随机应用构造函数 - 临时开发场景
     * <pre>
     * 使用场景：适用于开发、测试、学习等临时性场景
     * 环境变量：
     * - 🌷 Z_APP: 应用名称环境变量（可选）
     * - 🌷 Z_TENANT: 租户环境变量（可选）
     * </pre>
     * 功能：自动生成16位随机字符串作为应用名，租户为null
     * 限制：不适用于生产环境，因为应用名是随机的，无法持久化，这种模式下每次启动时候应用会被分配一个随机应用，不适合做应用管理，同样不适合做应用商店
     * <pre>
     *     ⚡️ 随机数的作用：在学习环境和实验环境
     *     - 实验环境：用于快速验证功能、概念验证、原型开发
     *     - 学习环境：用于教学演示、个人练习、代码测试
     *     - 实验环境之后：可以快速清理和重新开始，无需担心数据持久化问题
     *
     *     🎯 设计理念：
     *     - 无状态：每次启动都是全新的应用实例
     *     - 临时性：适合快速迭代和频繁重启
     *     - 隔离性：避免与正式应用产生冲突
     *     - 易清理：无需担心数据残留问题
     *
     *     ⚠️ 注意事项：
     *     - 不能用于生产环境
     *     - 不支持应用数据持久化
     *     - 不支持应用状态管理
     *     - 不适合团队协作开发
     *
     *     🎨 适用场景：
     *     - 单元测试和集成测试
     *     - 功能演示和原型验证
     *     - 个人学习和实验
     *     - CI/CD 流水线测试
     * </pre>
     */
    public KApp() {
        this(UtBase.randomString(16));
    }

    /**
     * 🏷️ 应用名称构造函数 - 本地单体场景
     * <pre>
     * 使用场景：本地开发、单体应用、已知应用名的场景
     * 环境变量：
     * - 🌷 Z_APP: 应用名称环境变量（优先级高于参数 name）
     * - 🌷 Z_TENANT: 租户环境变量（可选）
     * </pre>
     * 功能：使用指定的应用名，租户从环境变量获取或为null
     * 优先级：环境变量 Z_APP > 参数 name
     *
     * @param name 应用名称
     */
    public KApp(final String name) {
        this(name, null);
    }

    /**
     * 🏘️ 多租户构造函数 - 生产云环境场景
     * <pre>
     * 使用场景：生产环境、云环境、多租户场景
     * 环境变量：
     * - 🌷 Z_APP: 应用名称环境变量（优先级高于参数 name）
     * - 🌷 Z_TENANT: 租户环境变量（优先级高于参数 tenant）
     * </pre>
     * 功能：同时指定应用名和租户，支持完整的多租户架构
     * 优先级：环境变量 Z_APP > 参数 name，环境变量 Z_TENANT > 参数 tenant
     *
     * @param name   应用名称
     * @param tenant 租户标识
     */
    public KApp(final String name, final String tenant) {
        final String nameApp = ENV.of().get(EnvironmentVariable.Z_APP, name);
        final String nameTenant = ENV.of().get(EnvironmentVariable.Z_TENANT, tenant);
        this.initialize(nameApp, nameTenant);
    }

    private void initialize(final String name, final String tenant) {
        // 应用名称
        this.name = name;
        // 名空间
        this.ns = HApp.nsOf(name);
        // 租户信息
        this.tenant = tenant;
    }

    @Override
    public JsonObject option() {
        return this.configuration;
    }

    @Override
    public HApp option(final JsonObject configuration) {
        if (UtBase.isNil(configuration)) {
            return this;
        }
        this.configuration.clear();
        this.configuration.mergeIn(configuration, true);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T option(final String key) {
        return (T) this.configuration.getValue(key, null);
    }

    @Override
    public <T> HApp option(final String key, final T value) {
        this.configuration.put(key, value);
        return this;
    }

    public JsonObject data() {
        return this.data;
    }

    @Override
    public HApp data(final JsonObject data) {
        if (UtBase.isNil(data)) {
            return this;
        }
        this.data.clear();
        this.data.mergeIn(data, true);
        return this;
    }

    @Override
    public HApp apply(final HApp target) {
        if (Objects.isNull(target)) {
            return this;
        }
        if (target.equals(this)) {
            this.option().mergeIn(UtBase.valueJObject(target.option()));
            return this;
        } else {
            throw new _40101Exception500CombineApp(this.ns, this.name);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public HApp name(final String name) {
        this.name = name;
        this.ns = HApp.nsOf(name);
        return this;
    }

    @Override
    public String ns() {
        return this.ns;
    }

    @Override
    public HApp ns(final String ns) {
        this.ns = ns;
        return this;
    }

    @Override
    public HApp tenant(final String tenant) {
        this.tenant = tenant;
        return this;
    }

    @Override
    public String tenant() {
        return this.tenant;
    }

    @Override
    public String id() {
        if (UtBase.isNil(this.id)) {
            this.id = this.option(VName.APP_ID);
            if (UtBase.isNil(this.id)) {
                this.id = this.option(VName.KEY);
            }
        }
        return this.id;
    }

    @Override
    public HApp id(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final KApp kApp = (KApp) o;
        return Objects.equals(this.name, kApp.name) && Objects.equals(this.ns, kApp.ns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.ns);
    }

    @Override
    @SuppressWarnings("unchecked")
    public HApp vLog() {
        final String content = """
            [ ZERO ] APP 应用信息:
            \t\uD83C\uDF38 应用名: {}, 🧩 应用ID: {}, \uD83E\uDDCA 租户: {}
            \t🏷️ 命名空间: {}
            """;
        log.info(content, this.name, this.id, this.tenant, this.ns);
        return this;
    }
}
