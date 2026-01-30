package io.zerows.boot.full.base;

import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.epoch.annotations.Contract;

/**
 * ## 「Actor」顶层调度器
 * <p>
 * 在连接器{@link AbstractActor}上追加任务配置{@link Mission}。
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public abstract class AbstractActor extends AbstractConnector {
    /**
     * 「合约」{@link Mission}任务配置成员，从`I_JOB`中提取任务配置并执行计算。
     */
    @Contract
    private transient Mission mission;

    /**
     * 返回当前通道构造的任务配置信息。
     *
     * @return {@link Mission}任务配置。
     */
    protected Mission mission() {
        return this.mission;
    }
}
