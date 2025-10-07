package io.zerows.epoch.boot;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.zerows.epoch.basicore.InPre;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmVertxConfig;
import io.zerows.epoch.basicore.exception._41001Exception500AppNameMissing;
import io.zerows.platform.metadata.KApp;
import io.zerows.specification.app.HApp;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
@Slf4j
class ZeroSource implements ZeroPower.Source {
    private static final JUtil UT = SPI.V_UTIL;
    private static final String FILE_BOOT = "vertx-boot.yml";
    private final transient HFS fs = HFS.of();

    /**
     * 加载过程中的名空间分配等相关信息
     * <pre>
     *                  名空间                 应用名称                配置来源
     *    无配置
     * </pre>
     *
     * @return 配置对象
     */
    @Override
    public YmConfiguration load() {
        final InPre pre = this.ioPre();
        if (Objects.isNull(pre)) {
            final ConfigLoad load = ConfigLoad.ofLocal();
            log.info("[ ZERO ] 本地 -> 加载配置文件…… ⚙️ {}", load.getClass().getName());
            final HApp app = new KApp();
            return load.configure(app);
        } else {


            // -41001 验证
            final YmVertxConfig.Application application = pre.application();
            Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
                _41001Exception500AppNameMissing.class);


            final HApp app = new KApp(application.getName());
            final ConfigLoad load = ConfigLoad.ofCloud(pre);
            log.info("[ ZERO ] 云端 -> 加载配置文件…… ⚙️ {}", load.getClass().getName());
            return load.configure(app);
        }
    }

    private InPre ioPre() {
        final String content = this.fs.inContent(FILE_BOOT);


        // ------------- 两次加载都失败则直接返回 null
        if (Objects.isNull(content)) {
            return null;
        }


        final String parsedString = ZeroParser.compile(content);


        final JObject parsed = this.fs.ymlForJ(parsedString);
        final InPre inPre = UT.deserializeJson(parsed, InPre.class);


        // 设置日志
        ZeroLogging.configure(inPre.getLogging());

        log.debug("[ ZERO ] 读取到的配置内容：\n{}", parsed.encodePretty());
        return inPre;
    }
}