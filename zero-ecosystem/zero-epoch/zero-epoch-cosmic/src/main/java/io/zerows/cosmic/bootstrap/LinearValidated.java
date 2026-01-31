package io.zerows.cosmic.bootstrap;

import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.management.OCacheStore;
import io.zerows.platform.constant.VString;
import io.zerows.platform.exception._11002Exception500EmptyIo;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Slf4j
class LinearValidated extends AbstractAmbiguity implements Linear {

    private static final HFS fs = HFS.of();

    // 1. 扫描的根目录
    private static final String RULE_DIRECTORY = "openapi/operation";
    // 2. 每个目录下的目标文件名
    private static final String TARGET_FILE = "validated.yml";

    LinearValidated(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public void start(final Class<?> clazz, final RunVertx runVertx) {
        // 1. 获取所有子目录 (例如: openapi/operation/hi.beanv.validated.post)
        final List<String> dirs = fs.lsDirs(RULE_DIRECTORY);

        final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();

        dirs.forEach(dir -> {
            // 2. 构造目标文件的完整路径
            // 路径 = 目录路径 + / + validated.yml
            final String rulePath = dir + VString.SLASH + TARGET_FILE;

            try {
                // 3. 尝试读取 validated.yml
                // 注意：这里需要处理文件不存在的情况，fs.inYaml 可能会抛异常或返回空，取决于实现
                final JObject ruleJ = fs.inYaml(rulePath);

                if (SPI.V_UTIL.isNotEmpty(ruleJ)) {
                    // 4. 提取 Key (目录名)
                    final String key = this.keyOf(dir);
                    store.put(key, ruleJ.data());
                    log.debug("加载验证规则: key={}, path={}", key, rulePath);
                }
            } catch (final _11002Exception500EmptyIo ex) {
                // 如果目录下没有 validated.yml，通常忽略或记录 debug 日志
                // log.debug("目录下未找到规则文件: path={}", rulePath);
            } catch (final Exception ex) {
                log.error("加载验证规则异常: path={}", rulePath, ex);
            }
        });
    }

    /**
     * 从目录路径中提取 Key
     * Input:  openapi/operation/hi.beanv.validated.post
     * Output: hi.beanv.validated.post
     */
    private String keyOf(final String dirPath) {
        if (dirPath == null) {
            return null;
        }

        // 统一分隔符处理，防止 Windows 环境下的反斜杠问题
        String normalized = dirPath.replace("\\", VString.SLASH);

        // 去除末尾可能存在的斜杠
        if (normalized.endsWith(VString.SLASH)) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        // 截取最后一个 / 之后的内容
        final int lastIndex = normalized.lastIndexOf(VString.SLASH);
        if (lastIndex != -1) {
            return normalized.substring(lastIndex + 1);
        }
        return normalized;
    }

    @Override
    public void stop(final Class<?> clazz, final RunVertx runVertx) {
        final List<String> dirs = fs.lsDirs(RULE_DIRECTORY);
        final ConcurrentMap<String, JsonObject> store = OCacheStore.CC_CODEX.get();

        dirs.forEach(dir -> {
            try {
                // 移除时只需要 Key (目录名) 即可
                final String key = this.keyOf(dir);
                if (Ut.isNotNil(key)) {
                    store.remove(key);
                }
            } catch (final Exception ex) {
                log.error("移除验证规则失败: dir={}", dir, ex);
            }
        });
    }
}