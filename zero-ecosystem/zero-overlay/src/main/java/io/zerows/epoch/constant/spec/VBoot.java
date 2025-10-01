package io.zerows.epoch.constant.spec;

import io.zerows.epoch.constant.VName;

/**
 * 启动器核心目录设计
 * <p>
 * 内置配置对象 {@see HStarter}，统一配置接口
 * <pre><code>
 * boot:                # 启动器主配置
 *     launcher:        # 「路径」主容器配置 {@see HLauncher}
 *                           # 主容器1：OSGI容器 -> Jetty Bundle
 *                           # 主容器2：OSGI容器 -> Rx Vertx
 *                           # 主容器3：VertxApplication（单机版Zero标准）
 *                           # 主容器4：AeonApplication（云端启动器）
 *     component:       # {@see BootIo} 中提供并执行初始化
 *        on:           # 配置内置于 {@see HConfig.HOn}
 *        off:          # 配置内置于 {@see HConfig.HOff}
 *        run:          # 配置内置于 {@see HConfig.HRun}
 *     config:          # 实现层所需启动的基础配置，提供路径格式
 *                           # - OSGI容器配置路径
 *                           # - Web容器配置外置路径
 *     rad:             # 配置内置于 {@see HRAD}
 *     connect:         # 空间连接专用配置
 *        frontier:     # 配置内置于 {@see HFrontier}
 *        galaxy:       # 配置内置于 {@see HGalaxy}
 *        space:        # 配置内置于 {@see HSpace}
 * </code></pre>
 * 所有组件对应的配置使用 {@see BootIo} 的 SPI提供，此 SPI 会用于构造所有启动节点的核心
 * 区域组件，核心接口改造流程处理：
 * <pre><code>
 * 1. {@see HLauncher} 启动容器
 *    1.1. 内置初始化 {@see HStarter} 组装所有配置数据
 *         提取上述节点所有配置信息，内置调用 {@see BootIo} 提取不同的配置组件
 *         开启启动组件表，而配置数据则位于不同节点
 *    1.2. 抽取配置信息以初始化配置对象（配置对象只有一套）
 *    1.3. 根据最终结果，构造核心启动组件，然后触发 {@see HLauncher}
 * 2. 组件启动
 *    -- {@see HLauncher}              start
 *       -- {@see HConfig.HOn}               configure 启动配置初始化
 *       -- {@see HInstall}             start Bundle启动，有多少启动多少
 *    -- {@see HRAD}                  开发中心启动（可选）
 *    -- connect                                                      安全连接器启动
 * 3. 组件职责
 *    - launcher：主启动器
 *    - on：核心启动器，内置可调用多个 on，实现配置检查
 *      - 环境变量连接器
 *      - 目录检查器
 *      - 配置验证器（启动保证）
 *      - 可选组件扫描器
 *      - 启动Bundle /
 *          - 提取所有 Bundle 走内置流程，Bundle 流程依旧可使用生命周期组件 on / off / run（递归调用）
 *    - off：用于关闭流程
 *    - run：用于更新流程
 *    - rad：用于开发中心
 *    - connect：根据不同的方式获取 {@see HAccount}，连接安全中心实现
 *               不同空间模式的整体连接流程
 * </code></pre>
 *
 * </p>
 */
public interface VBoot {

    String __KEY = "boot";

    String _ENV_DEVELOPMENT = ".env.development";

    String LAUNCHER = "launcher";
    String COMPONENT = VName.COMPONENT;
    String EXTENSION = VName.EXTENSION;
    String CONFIG = VName.CONFIG;
    String RAD = "rad";
    String CONNECT = "CONNECT";

    interface component {
        String ON = "on";
        String OFF = "off";
        String RUN = "run";
        String PRE = "pre";
    }

    interface connect {
        String FRONTIER = "frontier";
        String GALAXY = "galaxy";
        String SPACE = "space";
    }

    interface extension {
        String EXECUTOR = "executor";
    }
}
