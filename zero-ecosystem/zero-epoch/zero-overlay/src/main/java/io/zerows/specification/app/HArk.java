package io.zerows.specification.app;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.zerows.platform.constant.VName;
import io.zerows.platform.constant.VString;
import io.zerows.platform.metadata.KDS;
import io.zerows.specification.vital.HOI;

import java.util.Objects;
import java.util.function.Function;

/**
 * 「方舟」Ark
 * <hr/>
 * 运行实例的容器对象，和应用执行一对一的绑定关系，之中会包含信息如下：
 * <pre><code>
 *     1. 应用信息
 *     2. 租户信息
 *     3. 所有数据源信息
 * </code></pre>
 * 数据结构如
 * <pre>
 *     - {@link HApp}，基础应用环境
 *     - {@link KDS}，数据源定义
 *         正常模式下一个应用会对接多个数据源，每个数据源绑定一个合法数据库，默认数据源如下：
 *         master           = {@link DBS}    元数据库 + 业务数据库
 *         master-history   = {@link DBS}    历史数据库
 *         master-workflow  = {@link DBS}    工作流数据库
 *     - {@link Database}：当前默认数据库基础配置
 *
 *     - {@link HOI}：租户对接模型，此处接口为租户基本接口，内置树型结构如：
 *       整体结构如：
 *         {@link HOI}
 *             id-01 = {@link HOI}
 *                     - id-0101 = {@link HOI}
 *                     - id-0102 = {@link HOI}
 *             id-02 = {@link HOI}
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HArk extends Function<HArk, HArk> {

    HApp app();


    /**
     * 应用配置容器中的数据源定义
     *
     * @return {@link KDS} 数据源定义
     */
    KDS datasource();

    Database database();

    /**
     * 当前应用所属的拥有者信息
     *
     * @return {@link HOI} 拥有者
     */
    default HOI owner() {
        return null;
    }

    @Override
    default HArk apply(final HArk app) {
        return this;
    }

    // 高频属性部分：----------------------------------------------------------

    /**
     * 当前容器的维度信息，字符串类型，在不同场景使用方法有所区别
     *
     * @return 维度信息
     */
    default String sigma() {
        return this.app().option(VName.SIGMA);
    }

    /**
     * 当前容器的语言信息，字符串类型，在不同场景使用方法有所区别
     *
     * @return 语言信息
     */
    default String language() {
        return this.app().option(VName.LANGUAGE);
    }

    /**
     * 根据传入的 identifier 提供专用缓存键
     *
     * @param identifier 传入的 identifier
     * @return 缓存键
     */
    default String cached(final String identifier) {
        Objects.requireNonNull(identifier);
        return this.app().ns() + VString.SLASH + identifier;
    }
}
