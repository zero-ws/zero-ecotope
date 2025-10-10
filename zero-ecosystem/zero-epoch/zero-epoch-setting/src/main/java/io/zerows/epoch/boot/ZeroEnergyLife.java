package io.zerows.epoch.boot;

import io.zerows.epoch.management.ORepository;
import io.zerows.epoch.management.ORepositoryClass;
import io.zerows.epoch.management.ORepositoryOption;
import io.zerows.specification.configuration.HLife;
import io.zerows.specification.configuration.HSetting;
import lombok.extern.slf4j.Slf4j;

/**
 * 启停管理专用
 *
 * @author lang : 2025-10-10
 */
@Slf4j
class ZeroEnergyLife implements HLife {
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

        log.info("[ ZERO ] ✅️ ============== 环境启动完成！");
    }
}
