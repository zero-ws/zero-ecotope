package io.zerows.cosmic.bootstrap;

import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.management.OCacheStore;
import io.zerows.platform.constant.VString;
import io.zerows.platform.constant.VValue;
import io.zerows.platform.exception._11002Exception500EmptyIo;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
class LinearValidated extends AbstractAmbiguity implements Linear {
    private static final HFS fs = HFS.of();
    private static final String RULE_DIRECTORY = "constraints";

    LinearValidated(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public void start(final Class<?> clazz, final RunVertx runVertx) {
        final List<String> rules = fs.lsFiles(RULE_DIRECTORY, VValue.SUFFIX.YML);
        // Codex 文件定义了相关规则
        final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();

        rules.forEach(rule -> {
            try {
                final JObject ruleJ = fs.inYaml(rule);
                if (SPI.V_UTIL.isNotEmpty(ruleJ)) {
                    store.put(this.keyOf(rule), ruleJ.data());  // 追加
                }
            } catch (final _11002Exception500EmptyIo ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }

    private String keyOf(final String path) {
        final String[] segments = path.split("/");
        final String filename = segments[segments.length - 1];
        return filename.substring(0, filename.lastIndexOf(VString.DOT));
    }

    @Override
    public void stop(final Class<?> clazz, final RunVertx runVertx) {
        final List<String> rules = fs.lsFiles(RULE_DIRECTORY, VValue.SUFFIX.YML);
        // Codex 文件定义了相关规则
        final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();
        rules.forEach(rule -> {
            try {
                store.remove(this.keyOf(rule));     // 移除
            } catch (final _11002Exception500EmptyIo ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }
}
