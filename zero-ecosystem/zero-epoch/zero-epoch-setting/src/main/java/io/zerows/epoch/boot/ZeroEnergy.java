package io.zerows.epoch.boot;

import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.assembly.DiFactory;
import io.zerows.epoch.jigsaw.NodeNetwork;
import io.zerows.epoch.management.ORepository;
import io.zerows.epoch.management.ORepositoryClass;
import io.zerows.epoch.management.ORepositoryMeta;
import io.zerows.epoch.management.ORepositoryOption;
import io.zerows.platform.enums.EmApp;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;
import io.zerows.specification.configuration.HLife;
import io.zerows.specification.configuration.HSetting;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 「能量」配置数据标准化结构，此处和 {@link HSetting} 完成底层绑定
 * <pre>
 *     - {@link HSetting} 中包含了众多统一的 {@link HConfig} 配置，而此处的 {@link HEnergy} 就负责
 *       将这些 {@link HConfig} 进行统一管理
 *     - {@link HEnergy} 不负责组件这一层，但会负责 HActor 的收集，最终会完成 HActor 的分配动作
 *       - 主容器
 * </pre>
 * 组件职责
 * <pre>
 *     1. 根据 {@link HSetting} 计算 {@link NodeNetwork} 和 {@link NodeVertxLegacy}
 *     2. 构造底层所需的各种 Options，并且保证 {@link HSetting} 不再往上传递
 *     3. 所有节点都不允许往上传递 {@link HSetting}
 *     4. 后期移除 DEV 检查，所以类似 hasInfix 的判断由 Energy 来完成
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class ZeroEnergy implements HEnergy {
    private static final Cc<String, ZeroEnergy> CC_ENERGY = Cc.open();
    private final HSetting setting;
    private final HLife engage;
    private String[] args;

    private ZeroEnergy(final HSetting setting) {
        /*
         * 构造生命周期管理部分，此部分只会和 HBoot 协同，在 ZeroLauncher 内部执行过程中完成相关初始化
         * 1. - pre 前序执行
         * 2. - on  启动过程中的核心步骤
         * 3. - off 停止过程中的核心步骤
         * 4. - run 重启/刷新过程中的核心步骤
         */
        this.setting = setting;
        this.engage = new Life();
    }

    static ZeroEnergy of(final HSetting setting) {
        Objects.requireNonNull(setting, "[ ZERO ] 传入配置不可以为 null.");
        Objects.requireNonNull(setting.id(), "[ ZERO ] 配置必须包含唯一标识符.");
        return CC_ENERGY.pick(() -> new ZeroEnergy(setting), setting.id());
    }

    public HEnergy args(final String[] args) {
        this.args = args;
        return this;
    }

    @Override
    public void initialize() {
        this.engage.whenStart(this.setting);
    }

    @Override
    public String[] args() {
        return this.args;
    }

    @Override
    public HSetting setting() {
        return this.setting;
    }

    @Override
    public HConfig boot(final EmApp.LifeCycle lifeCycle) {
        if (Objects.isNull(lifeCycle)) {
            log.warn("[ ZERO ] 传入生命周期为 null，必须是 EmApp.LifeCycle 的合法值！");
            return null;
        }
        return this.setting.boot(lifeCycle);
    }

    /**
     * 启停管理专用
     *
     * @author lang : 2025-10-10
     */
    @Slf4j
    private static class Life implements HLife {
        @Override
        public void whenStart(final HSetting setting) {
            log.info("[ ZERO ] ============== 环境启动中……");
            /*
             * 🟤BOOT-006：配置项 Option 级别的初始化流程
             *   - 构造 NodeNetwork / NodeVertx 两种核心节点配置
             *   - 内置的 Options 也一并构造完成
             *   - 打印详细的构造基础，如果后续扫描依赖配置信息，则可以考虑配置合并的模式扫描
             */
            ORepository.ofOr(ORepositoryOption.class).whenStart(setting);



            /*
             * 🟤BOOT-007: 元数据 Class 级别的初始化流程
             *   - 扫描整个环境提取符合条件的 Class 元数据
             *   - 构造 DI 容器所需的整体准备环境（后期 JSR 330 / JSR 365 可考虑引入）
             *   - 扫描 EndPoint/Queue 等核心类
             */
            ORepository.ofOr(ORepositoryClass.class).whenStart(setting);



            /*
             * 🟤BOOT-008: 元数据 Meta 级别的初始化流程
             */
            ORepository.ofOr(ORepositoryMeta.class).whenStart(setting);


            /*
             * 🟤BOOT-009：DI 环境单独启动
             */
            DiFactory.singleton().build();
            log.info("[ ZERO ] ✅️ ============== （核心）环境启动完成！");
        }
    }
}
