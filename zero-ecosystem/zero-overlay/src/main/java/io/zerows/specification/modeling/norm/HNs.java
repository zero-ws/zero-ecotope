package io.zerows.specification.modeling.norm;

import io.zerows.platform.constant.VString;
import io.zerows.specification.access.cloud.HDomain;
import io.zerows.specification.access.cloud.HPlatform;
import io.zerows.specification.access.cloud.HTenant;
import io.zerows.specification.modeling.HLife;

/**
 * 名空间定义，定义对应名空间的规范相关信息，当一个元素转换成标准时，一定会包含名空间的引用，名空间引用使用组合模式（非继承），不同名空间
 * 的元素定义会有所区别，对应于XML中的如下抽象定义：
 * <pre><code class="xml">
 *     <myNS nsURI="" nsPrefix="">
 *     </myNS>
 * </code></pre>
 *
 * 所以名空间中通常会包含如下元素集：
 *
 * <ol>
 *     <li>name：表示名空间本身的名称，对应下层的所属</li>
 *     <li>uri：当前名空间绑定的URI，可转换成 nsURI 部分</li>
 *     <li>prefix：当前名空间使用的默认的前缀，通常是 XML 中的前缀信息，默认为 ""</li>
 * </ol>
 *
 * 和原始建模项目的区别 zero-atom，zero-atom 是不跨应用部分的建模处理，而高阶建模部分是跨应用部分的建模，该模型由于包含了 uri 部分，
 * Domain部分的区别直接限定了其名空间信息，该接口和 {@link HLife} 接口的属性含义和对应关系：
 *
 * <pre><code>
 * 1. HLife 用于描述模型所属应用，之中包含了
 *    - resource(): 模型/实体, 绑定的资源目录
 *    - identifier(): 统一标识符
 *    - namespace(): 模型所属名空间
 * 2. HNS 用于描述所有元素（不仅限模型）的名空间，其中包括
 *    - name(): 元素所属名空间，和 HLife 中的 namespace() 对应
 *    - uri(): 元素名空间所对应的 XML 中的引用 uri 地址
 *    - prefix(): 元素名空间对应的 XML 中的 nsPrefix 前缀定义
 * </code></pre>
 *
 * @author lang : 2023-05-08
 */
public interface HNs {
    /**
     * @return 名空间名称
     */
    String name();

    /**
     * @return 名空间的URI地址（全网唯一）
     */
    default String nsUri() {
        return VString.EMPTY;
    }

    /**
     * @return 名空间的默认前缀
     */
    default String nsPrefix() {
        return VString.EMPTY;
    }

    /**
     * 「命名空间」后续建模会和当前命名空间直接关联，实现命名空间的相关关联
     *
     * @author lang : 2023-05-21
     */
    interface HMeta extends HNs {
        /**
         * Namespace名空间的网络标识符
         *
         * @return {@link HUri}
         */
        HUri uri();

        /**
         * 名空间所属 Domain 域，此处 Domain 域会关联到三个不同的区域
         * <pre><code>
         *     1. {@link HTenant}
         *     2. {@link HPlatform}
         *     3. {@link HDomain}
         * </code></pre>
         *
         * @return 区域标识符
         */
        String domain();
    }
}
