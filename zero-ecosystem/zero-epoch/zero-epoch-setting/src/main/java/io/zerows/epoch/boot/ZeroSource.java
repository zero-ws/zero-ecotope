package io.zerows.epoch.boot;

import cn.hutool.core.util.StrUtil;
import io.r2mo.function.Fn;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.spec.InPre;
import io.zerows.epoch.spec.YmApplication;
import io.zerows.epoch.spec.YmConfiguration;
import io.zerows.epoch.spec.YmSpec;
import io.zerows.epoch.spec.exception._41001Exception500AppNameMissing;
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
     *              配置，共享配置位于 {@link YmSpec.vertx.config#import_} 中 {@link JsonArray} 进行设置
     *
     * </pre>
     * 配置信息的路径地址：{@link YmSpec.vertx.application#name}，对应在 vertx.yml 和 vertx-boot.yml 中定义的路径也如此
     *
     * @return 配置对象
     */
    @Override
    public YmConfiguration load() {
        final InPre pre = ZeroFs.of().inPre();

        final YmConfiguration configuration;
        final HApp app;
        if (Objects.isNull(pre)) {
            final io.zerows.epoch.configuration.ConfigLoad load = ZeroEquip.ofLocal();
            app = new KApp();

            log.info("[ ZERO ] 本地 -> 加载配置文件…… ⚙️ {}", load.getClass().getName());
            configuration = load.configure(app);
        } else {
            // 日志处理（此处可保证启动前的日志信息）
            ZeroLogging.configure(pre.getLogging());

            // -41001 验证
            final YmApplication application = pre.application();
            Fn.jvmKo(Objects.isNull(application) || StrUtil.isEmpty(application.getName()),
                _41001Exception500AppNameMissing.class);


            final io.zerows.epoch.configuration.ConfigLoad load = ZeroEquip.ofCloud(pre);
            app = new KApp(application.getName());

            log.info("[ ZERO ] 云端 -> 加载配置文件…… ⚙️ {}", load.getClass().getName());
            configuration = load.configure(app);
        }
        StoreApp.of().add(app.vLog());
        return configuration;
    }
}