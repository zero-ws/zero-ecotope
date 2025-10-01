package io.zerows.module.cloud.atom;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.EmCloud;
import io.zerows.core.constant.KName;
import io.zerows.core.running.boot.KPlot;
import io.zerows.core.running.boot.KRepo;
import io.zerows.core.util.Ut;
import io.zerows.module.cloud.cache.CStoreCloud;
import io.zerows.module.cloud.eon.HName;
import io.zerows.module.cloud.eon.HPath;
import io.zerows.module.cloud.util.Ho;
import io.zerows.module.metadata.uca.environment.MatureOn;
import io.zerows.specification.development.ncloud.HAeon;
import io.zerows.specification.development.ncloud.HStarter;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AeonConfig implements HAeon, Serializable {
    // 代码仓库
    private final ConcurrentMap<EmCloud.Runtime, KRepo> repos = new ConcurrentHashMap<>();
    // 三种模式核心支持
    private final EmCloud.Mode mode;
    // 工作目录
    private final String workspace;
    private final String name;

    private final KPlot plot;

    // 启动配置
    private HStarter boot;

    /* 三种库 */
    private AeonConfig(final JsonObject configuration) {
        this.mode = Ut.toEnum(() -> Ut.valueString(configuration, KName.MODE),
            EmCloud.Mode.class, EmCloud.Mode.MIN);
        // 上层工作区
        this.name = Ut.valueString(configuration, HName.NAME);
        this.workspace = Ut.valueString(configuration, HName.WORKSPACE, HPath.WORKSPACE);
        // 云工作区 Plot
        JsonObject plotJ = Ut.valueJObject(configuration, KName.PLOT);
        plotJ = MatureOn.envPlot(plotJ);
        this.plot = Ut.deserialize(plotJ, KPlot.class);


        // 遍历读取 Repo, kinect, kidd, kzero
        this.initRepo(configuration);
    }

    public static HAeon configure(final JsonObject configJ) {
        // kidd 为出厂设置环境，所以以它为缓存键值
        final JsonObject repoJ = Ut.valueJObject(configJ, HName.REPO);
        final JsonObject kiddJ = Ut.valueJObject(repoJ, HName.KIDD);
        if (Ut.isNil(kiddJ)) {
            Ho.LOG.Aeon.warn(AeonConfig.class, "`kidd` configuration missing!!");
            return null;
        }
        // 初始化
        return CStoreCloud.CC_AEON.pick(() -> new AeonConfig(configJ), kiddJ.hashCode());
    }

    private void initRepo(final JsonObject configuration) {
        final JsonObject repoJ = Ut.valueJObject(configuration, HName.REPO);
        Ut.<JsonObject>itJObject(repoJ, (itemJ, field) -> {
            final EmCloud.Runtime repoType = Ut.toEnum(() -> field, EmCloud.Runtime.class, null);
            if (Objects.nonNull(repoType)) {
                final KRepo repo = Ut.deserialize(itemJ, KRepo.class);
                // 绑定仓库工作区：workspace + running
                final String wsRepo = Ut.ioPath(this.workspace, repoType.name());
                this.repos.put(repoType, repo.assemble(wsRepo));
            }
        });
    }

    // ------------------------- 提取配置专用
    // 装配专用
    @Override
    public void boot(final HStarter boot) {
        this.boot = boot;
    }

    @Override
    public HStarter boot() {
        return this.boot;
    }

    @Override
    public KPlot plot() {
        return this.plot;
    }

    @Override
    public EmCloud.Mode mode() {
        return this.mode;
    }

    @Override
    public String workspace() {
        return this.workspace;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public KRepo repo(final EmCloud.Runtime runtime) {
        return this.repos.getOrDefault(runtime, null);
    }

    @Override
    public ConcurrentMap<EmCloud.Runtime, KRepo> repo() {
        return this.repos;
    }
}
