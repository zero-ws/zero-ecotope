package io.zerows.extension.crud.boot;

import io.zerows.cortex.metadata.WebRule;
import io.zerows.epoch.web.MDConfiguration;
import io.zerows.extension.crud.common.IxConfig;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import io.zerows.mbse.metadata.KModule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * CRUD 模块中的特殊配置信息说明
 *
 * @author lang : 2025-12-24
 */
@Slf4j
public class MDCRUDManager extends MDModuleManager<Boolean, IxConfig> {
    private static MDCRUDManager INSTANCE;
    private IxSetup<KModule> setupModule;
    private IxSetup<ConcurrentMap<String, List<WebRule>>> setupRule;

    private MDCRUDManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDCRUDManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDCRUDManager();
        }
        return INSTANCE;
    }

    @Override
    public void config(final IxConfig config) {
        // 重写后先调用父类
        super.config(config);
        this.setupModule = IxSetup.of(IxSetupModule::new, config);
        this.setupRule = IxSetup.of(IxSetupRule::new, config);
    }

    void handleAfter(final Set<MDConfiguration> configurationSet) {
        // 处理模块配置
        Objects.requireNonNull(this.setupModule).configure(configurationSet);
        // 处理规则配置
        Objects.requireNonNull(this.setupRule).configure(configurationSet);
    }

    // ------------------- 获取相关数据 -------------------
    public KModule getActor(final String actor) {
        return this.setupModule.map(actor);
    }

    public ConcurrentMap<String, List<WebRule>> getRules(final String actor) {
        return this.setupRule.map(actor);
    }

    public Set<String> getUris() {
        return IxSetupModule.stored();
    }

    public String getColumnKey() {
        return this.config().getColumnKeyField();
    }

    public String getColumnLabel() {
        return this.config().getColumnLabelField();
    }
}
