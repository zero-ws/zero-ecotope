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

import java.net.URL;
import java.util.Objects;

/**
 * @author lang : 2025-10-06
 */
@Slf4j
class ZeroSource implements ZeroPower.Source {
    private static final JUtil UT = SPI.V_UTIL;
    private static final String FILE_BOOT = "vertx-boot.yml";
    private final transient HFS fs = HFS.of();

    @Override
    public YmConfiguration load() {
        final InPre pre = this.ioPre();
        if (Objects.isNull(pre)) {
            log.info("[ ZERO ] 本地 -> 加载配置文件……");
            return ConfigLoad.ofLocal().configure(null);
        } else {
            log.info("[ ZERO ] 云端 -> 加载配置文件……");
            final HApp app = this.ioApp(pre);
            return ConfigLoad.ofCloud(pre).configure(app);
        }
    }

    private HApp ioApp(final InPre pre) {
        final YmVertxConfig.Application application = pre.application();

        Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
            _41001Exception500AppNameMissing.class);
        return new KApp(application.getName());
    }

    private InPre ioPre() {
        String content = null;
        if (this.fs.isExist(FILE_BOOT)) {
            /*
             * 情况一：直接检查当前环境中文件已存在，直接读取，文件存在时启用的是上层的 HStore 抽象存储方法，简单说
             *        如果实现是本地环境则使用本地环境加载此文件，如果是远程环境则使用远程环境加载此文件。
             */
            content = this.fs.inString(FILE_BOOT);
        }


        if (Objects.isNull(content)) {
            /*
             * 情况二：文件不存在时，尝试从类路径中读取 vertx-boot.yml 文件
             */
            final URL urlPath = Thread.currentThread().getContextClassLoader().getResource(FILE_BOOT);
            content = this.fs.inString(urlPath);
        }


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