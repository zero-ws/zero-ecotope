package io.zerows.epoch.boot;

import io.r2mo.base.io.HStore;
import io.r2mo.typed.cc.Cc;
import io.zerows.epoch.basicore.YmConfiguration;
import io.zerows.specification.configuration.HEnvironment;
import io.zerows.specification.configuration.HSetting;

/**
 * 配置程序客户端，替换旧版的 {@see OZeroStore} 配置信息，此处的配置客户端主要用于进行拆分从本地或远程加载相关配置
 * <pre>
 *     1. 独立程序配置（不访问 Nacos）-> vertx.yml
 *     2. 远程配置程序（访问 Nacos）-> vertx-boot.yml
 *        - 远程共享配置
 *        - 远程私有配置
 *        - 本地私有配置
 *     3. 远程的优先级配置如：本地私有配置 > 远程私有配置 > 远程共享配置
 * </pre>
 * 关于配置的实现流程表
 * <pre>
 *     主流程：{@link ZeroPower} -> compile() -> HSetting
 *     - 内置一级流程 -> 加载 String
 *       1）根据 vertx-boot.yml 的存在与否判断是否启用远程配置
 *       2）根据 vertx-boot.yml 加载的预配置判断启用哪种远程配置
 *          - nacos         -> Nacos 配置
 *          - zookeeper     -> Zookeeper 配置
 *     - 传入 {@link HEnvironment} 执行解析
 *     - 解析之后的结果填充变量得到最终配置
 *     - 将核心配置注册到配置管理器中（内存内）实现配置的本地管理
 *     所有上层消费直接对接本地内存配置，但本地内存配置源会包含多个
 * </pre>
 * 内置路径检索优先级
 * <pre>
 *     1. 先通过 {@link HStore} 检查存储中的
 *        vertx-boot.yml
 *        作第一优先级
 *     2. 再通过 ClassPath 检查 vertx-boot.yml 作第一优先级
 * </pre>
 *
 * @author lang : 2025-10-06
 */
public interface ZeroPower {

    Cc<String, ZeroPower> CC_SKELETON = Cc.openThread();

    static ZeroPower of() {
        return CC_SKELETON.pick(ZeroPowerBridge::new, ZeroPowerBridge.class.getName());
    }

    HSetting compile(Class<?> bootCls);

    interface Source {

        YmConfiguration load();
    }
}
