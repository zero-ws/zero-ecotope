package io.zerows.extension.crud.boot;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.epoch.annotations.Validated;
import io.zerows.epoch.basicore.MDConfiguration;
import io.zerows.epoch.boot.ZeroFs;
import io.zerows.extension.crud.common.IxConfig;
import io.zerows.extension.skeleton.common.KeConstant;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-12-24
 */
@Slf4j
class IxSetupRule extends IxSetupBase<ConcurrentMap<String, List<WebRule>>> {

    /**
     * 全局管理：验证规则处理
     * <pre>
     *     uri --> field-01 --> List<WebRule>
     *             field-02 --> List<WebRule>
     * </pre>
     * 后期 Worker 验证，这些模块相关的验证规则都是动态注入，此处不需要实现 {@link Validated} 注解之下的验证规则，相反而是使用扩展的方式处理，
     * 但扩展方式处理的结果和 {@link Validated} 是一致的。
     */
    private static final ConcurrentMap<String, ConcurrentMap<String, List<WebRule>>> STORED = new ConcurrentHashMap<>();
    /**
     * 由于 {@link Validated} 注解的限制，必须指定配置文件，所以这里统一定义配置的路径用来解决验证问题
     * <pre>
     *     1. 旧版的 {@link Validated} 配置路径位于：codex/{uri}
     *     2. CRUD 模块配置的部分位于：codex/_crud/????
     * </pre>
     */
    private static final String CFG_VALIDATOR = "codex/_crud/";
    private static final ZeroFs FS = ZeroFs.of();
    IxSetupRule(final IxConfig config) {
        super(config);
    }

    @Override
    public Boolean configure(final Set<MDConfiguration> waitFor) {
        // 加载 yml 配置文件
        final List<String> files = FS.inFiles(CFG_VALIDATOR, VValue.SUFFIX.YML);
        files.forEach(filename -> {


            /*
             * key      -> 直接移除 .yml 后缀
             * data     -> 规则数据 JsonArray
             */
            final String key = filename.replace(VString.DOT + VValue.SUFFIX.YML, VString.EMPTY);
            final JsonObject rules = FS.inYamlJ(filename);
            final ConcurrentMap<String, List<WebRule>> ruleMap = new ConcurrentHashMap<>();
            rules.fieldNames().forEach(field -> {



                /* 字段基础规则表 */
                final JsonArray ruleArray = rules.getJsonArray(field);
                final List<WebRule> ruleList = new ArrayList<>();
                Ut.itJArray(ruleArray).forEach(ruleJ -> {
                    final WebRule rule = WebRule.create(ruleJ);
                    ruleList.add(rule);
                });
                ruleMap.put(field, ruleList);


                log.info("{} --- 规则文件: `{}` / key = {}", KeConstant.K_PREFIX_BOOT, filename, key);
                STORED.put(key, ruleMap);
            });
        });
        log.info("{} === IxSetupRule 加载完成，规则数量：{}", KeConstant.K_PREFIX_BOOT, STORED.size());
        return true;
    }

    @Override
    public ConcurrentMap<String, ConcurrentMap<String, List<WebRule>>> map() {
        return STORED;
    }

    @Override
    public ConcurrentMap<String, List<WebRule>> map(final String key) {
        return STORED.getOrDefault(key, new ConcurrentHashMap<>());
    }
}
