package io.zerows.specification.development.program;

import io.zerows.epoch.annotations.monitor.One2One;
import io.zerows.epoch.common.shared.boot.KRepo;
import io.zerows.epoch.constant.VValue;
import io.zerows.specification.development.compiled.HTrash;

import java.util.Set;

/**
 * 「代码库」Repository
 * <hr/>
 * 代码库用于描述当前环境中 HToolkit 对应的代码库相关信息，它的关系如：
 * <pre><code>
 *     1. 和 HToolkit 是 1:N 的关系
 *     2. 和 HProject 是 1:1 的关系
 * </code></pre>
 *
 * @author lang : 2023-05-20
 */
public interface HRepo {
    /**
     * 当前代码库对应的项目
     *
     * @return {@link HProject}
     */
    @One2One(interaction = true)
    HProject project();

    /**
     * 当前代码库对应的工作空间
     *
     * @return {@link KRepo}
     */
    KRepo repository();

    /**
     * 当前代码库对应的工作空间
     *
     * @return {@link HWorkshop}
     */
    HWorkshop workshop();

    /**
     * 返回当前库所有分支信息，默认必须带 master
     *
     * @return {@link Set<String>}
     */
    default Set<String> branches() {
        return Set.of(VValue.DEFAULT_BRANCH_MASTER);
    }

    /**
     * 「本地库」Local Repository
     * 本地代码库直接从库中直接继承，而限制条件如：
     * <pre><code>
     *     1. 本地库不可以发布，即不可以执行类似 HCabe 应用区域的部分
     *     2. 本地库会包含本地回收站引用，可以将删除内容扔到回收站程序中
     * </code></pre>
     *
     * @author lang : 2023-05-20
     */
    interface HLocal extends HRepo {
        /**
         * 返回回收站引用
         *
         * @return {@link HTrash}
         */
        @One2One
        HTrash trash();
    }

    /**
     * 「远程库」Remote Repository
     * 远程库中的库信息中会包含详细的版本信息，所以远程库可以发布，而且可以执行类似 HCabe 应用区域的部分
     *
     * @author lang : 2023-05-20
     */
    interface HRemote extends HRepo {
        /**
         * 返回当前工作分支
         *
         * @return {@link HBranch}
         */
        @One2One
        HBranch working();
    }
}
