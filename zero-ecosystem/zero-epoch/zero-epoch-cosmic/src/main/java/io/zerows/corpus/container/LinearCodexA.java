package io.zerows.corpus.container;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.management.OCacheStore;
import io.zerows.platform.constant.VPath;
import io.zerows.platform.constant.VString;
import io.zerows.platform.exception._11002Exception500EmptyIo;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-03
 */
@Slf4j
class LinearCodexA implements StubLinear {

    @Override
    public void runUndeploy(final Class<?> clazz, final RunVertx runVertx) {
        final List<String> rules = Ut.ioFiles("codex", VPath.SUFFIX.YML);
        rules.forEach(rule -> {
            try {
                // Codex 文件定义了相关规则
                final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();
                // 移除
                store.remove(rule.substring(0, rule.lastIndexOf(VString.DOT)));
            } catch (final _11002Exception500EmptyIo ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }

    @Override
    public void runDeploy(final Class<?> clazz, final RunVertx runVertx) {
        final List<String> rules = Ut.ioFiles("codex", VPath.SUFFIX.YML);
        rules.forEach(rule -> {
            try {
                final String ruleFile = "codex/" + rule;
                final JsonObject ruleData = Ut.ioYaml(ruleFile);


                // Codex 文件定义了相关规则
                final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();
                // 追加
                store.put(rule.substring(0, rule.lastIndexOf(VString.DOT)), ruleData);
            } catch (final _11002Exception500EmptyIo ex) {
                log.error(ex.getMessage(), ex);
            }
        });
    }
}
