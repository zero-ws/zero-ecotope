package io.zerows.epoch.boot;

import io.r2mo.io.common.HFS;
import io.r2mo.typed.json.JObject;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.app.HApp;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

/**
 * HFS 模式的加载有两种情况
 * <pre>
 *     1. 存在 vertx.yml 配置
 *     2. 不存在 vertx.yml 配置（最小运行集）
 * </pre>
 *
 * @author lang : 2025-10-06
 */
@Slf4j
class ConfigLoadHFS extends ConfigLoadBase {
    private static final String FILE_VERTX = "vertx.yml";
    private final transient HFS fs = HFS.of();

    @Override
    public YmConfiguration configure(final HApp app) {
        final String content = this.fs.inContent(FILE_VERTX);

        if (Ut.isNil(content)) {
            // 不存在 vertx.yml 的默认配置
            return YmConfiguration.createDefault();
        }


        // 有内容，则直接解析之后处理
        final String parsedString = ZeroParser.compile(content);


        final JObject parsed = this.fs.ymlForJ(parsedString);
        final YmConfiguration inConfiguration = UT.deserializeJson(parsed, YmConfiguration.class);

        return this.completeConfiguration(inConfiguration, parsed, app);
    }
}
