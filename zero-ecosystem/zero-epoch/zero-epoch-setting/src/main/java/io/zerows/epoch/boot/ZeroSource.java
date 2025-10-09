package io.zerows.epoch.boot;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.r2mo.io.common.HFS;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.basicore.InPre;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.epoch.basicore.YmVertxConfig;
import io.zerows.epoch.basicore.exception._41001Exception500AppNameMissing;
import io.zerows.platform.management.StoreApp;
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
     *                  名空间                         应用名称                    配置来源
     *    🚫 无配置     io.zerows.app.???                 （随机16位字符串）            （无）
     *              这种模式下，应用名称没有任何意义，因为它既不持久化也不会被发布到环境中，最好的方式是只在开发过程、学习过程
     *              中使用这种无配置模式，零代码零配置启动而已。
     *
     *    🏠 本地       io.zerows.app.[应用名称]            (应用名称)                   vertx.yml
     *              这种模式下，应用名称必须在 vertx.yml 中进行配置，否则报错，而环境变量中的名称优先级更高。
     *              --------------------- {@link ConfigLoadHFS} 负责 ---------------------------------
     *
     *    ☁️ 远程       io.zerows.app.[应用名称]            (应用名称)                   vertx-boot.yml
     *              这种模式下，应用名称必须在 vertx-boot.yml 中进行配置，且配置的应用名称会在远程配置中心产生一个同名的配置
     *              项，这种情况多半是 Cloud 中的某个小应用、微服务等，非单体。
     *              --------------------- {@link ConfigLoadCloud} 负责 -------------------------------
     *              注：远程模式下目前版本访问的是 Nacos 配置中心，所以要支持合并配置 vertx-boot.yml + vertx.yml + 共享 yml
     *              配置，共享配置位于 {@link VertxYml.vertx.config#import_} 中 {@link JsonArray} 进行设置
     *
     * </pre>
     * 配置信息的路径地址：{@link VertxYml.vertx.application#name}，对应在 vertx.yml 和 vertx-boot.yml 中定义的路径也如此
     *
     * @return 配置对象
     */
    @Override
    public YmConfiguration load() {
        final InPre pre = this.ioPre();
        if (Objects.isNull(pre)) {
            final ConfigLoad load = ConfigLoad.ofLocal();
            final HApp app = new KApp().vLog();

            StoreApp.of().add(app);
            
            log.info("[ ZERO ] 本地 -> 加载配置文件…… ⚙️ {}", load.getClass().getName());
            return load.configure(app);
        } else {
            // -41001 验证
            final YmVertxConfig.Application application = pre.application();
            Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
                _41001Exception500AppNameMissing.class);


            final ConfigLoad load = ConfigLoad.ofCloud(pre);
            final HApp app = new KApp(application.getName()).vLog();

            StoreApp.of().add(app);

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