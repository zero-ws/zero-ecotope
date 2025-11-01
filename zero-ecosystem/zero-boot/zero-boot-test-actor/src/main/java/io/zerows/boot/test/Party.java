package io.zerows.boot.test;

import io.r2mo.base.dbe.Database;
import io.zerows.boot.extension.component.ArmsStore;
import io.zerows.platform.metadata.KGlobal;
import io.zerows.specification.app.HArk;

/**
 * 合同术语中，Party A 代表甲方，Party B 代表乙方，所以此处的 Party 接口代表甲乙双方相关信息。
 * <pre><code>
 *     1. 甲方直接走 {@link KGlobal} 全局配置用于测试和开发（包括模拟模式）
 *     2. 乙方则考虑从 {@link ArmsStore#stellar()} 中提取所有乙方信息，包括多个乙方等相关信息。
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Party {

    /**
     * 数据库配置对象读取方法，测试专用库加载，可直接通过 {@link HArk} 中提取，且此处是服务于测试，所以在提取数据库时只提取动态数据库，不考虑静态数据库，静态数据库有额外的方式再次提取，也只有动态库在测试时依赖加载的基本环境需要基于部分初始化流程在框架中执行相关流程，不过此处数据库等相关信息可以直接被重写。
     *
     * @return {@link Database}
     */
    default Database configDatabase() {
        return this.configArk().database();
    }

    /**
     * 应用配置对象读取方法。
     *
     * @return {@link HArk}
     */
    HArk configArk();
}
