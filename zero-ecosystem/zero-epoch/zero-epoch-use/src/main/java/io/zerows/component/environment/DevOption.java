package io.zerows.component.environment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.zerows.platform.ENV;
import io.zerows.platform.EnvironmentVariable;

import java.io.Serializable;

/**
 * 调试专用环境变量（统一处理）
 * 基本命名规范
 * - Z_DEV_:    开发专用参数
 * - Z_CACHE_:  缓存相关参数
 * 检索优先级
 * 1. 先检查环境变量
 * 2. 再检查配置中的信息，配置格式如下
 * <pre><code class="yaml">
 *     # vertx-deployment.yml
 *     development:
 *       ENV:
 *         Z_DEV_XX: xxx
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class DevOption implements Serializable, EnvironmentVariable {
    // ------------- dev -------------------
    // 是否显示URI路由检测状况，默认 false，不检测
    @JsonProperty(EnvironmentVariable.DEV_WEB_URI)
    private Boolean devWebUri = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_JVM_STACK)
    private Boolean devJvmStack = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_JOB_BOOT)
    private Boolean devJobBoot = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_EXCEL_RANGE)
    private Boolean devExcelRange = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_JOOQ_COND)
    private Boolean devJooqCond = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_EXPR_BIND)
    private Boolean devExprBind = Boolean.FALSE;
    @JsonProperty(EnvironmentVariable.DEV_DAO_BIND)
    private Boolean devDaoBind = Boolean.TRUE;

    // 是否开启认证日志，默认false，关闭（5个地方调用）
    @JsonProperty(EnvironmentVariable.DEV_AUTHORIZED)
    private Boolean devAuthorized = Boolean.FALSE;

    // Z_DEV_AUTHORIZED
    public Boolean getDevAuthorized() {
        return ENV.of().get(EnvironmentVariable.DEV_AUTHORIZED, this.devAuthorized, Boolean.class);
    }

    public void setDevAuthorized(final Boolean devAuthorized) {
        this.devAuthorized = devAuthorized;
    }

    // Z_DEV_WEB_URI
    public Boolean getDevWebUri() {
        return ENV.of().get(EnvironmentVariable.DEV_WEB_URI, this.devWebUri, Boolean.class);
    }

    public void setDevWebUri(final Boolean devWebUri) {
        this.devWebUri = devWebUri;
    }

    // Z_DEV_JVM_STACK
    public Boolean getDevJvmStack() {
        return ENV.of().get(EnvironmentVariable.DEV_JVM_STACK, this.devJvmStack, Boolean.class);
    }

    public void setDevJvmStack(final Boolean devJvmStack) {
        this.devJvmStack = devJvmStack;
    }

    // Z_DEV_JOB_BOOT
    public Boolean getDevJobBoot() {
        return ENV.of().get(EnvironmentVariable.DEV_JOB_BOOT, this.devJobBoot, Boolean.class);
    }

    public void setDevJobBoot(final Boolean devJobBoot) {
        this.devJobBoot = devJobBoot;
    }

    // Z_DEV_EXCEL_RANGE
    public Boolean getDevExcelRange() {
        return ENV.of().get(EnvironmentVariable.DEV_EXCEL_RANGE, this.devExcelRange, Boolean.class);
    }

    public void setDevExcelRange(final Boolean devExcelRange) {
        this.devExcelRange = devExcelRange;
    }

    // Z_DEV_JOOQ_COND
    public Boolean getDevJooqCond() {
        return ENV.of().get(EnvironmentVariable.DEV_JOOQ_COND, this.devJooqCond, Boolean.class);
    }

    public void setDevJooqCond(final Boolean devJooqCond) {
        this.devJooqCond = devJooqCond;
    }

    // Z_DEV_EXPR_BIND
    public Boolean getDevExprBind() {
        return ENV.of().get(EnvironmentVariable.DEV_EXPR_BIND, this.devExprBind, Boolean.class);
    }

    public void setDevExprBind(final Boolean devExprBind) {
        this.devExprBind = devExprBind;
    }

    // Z_DEV_DAO_BIND
    public Boolean getDevDaoBind() {
        return ENV.of().get(EnvironmentVariable.DEV_DAO_BIND, this.devDaoBind, Boolean.class);
    }

    public void setDevDaoBind(final Boolean devDaoBind) {
        this.devDaoBind = devDaoBind;
    }
}
