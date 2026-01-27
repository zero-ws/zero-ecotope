package io.zerows.epoch.management;

import io.zerows.epoch.jigsaw.NodeNetwork;
import io.zerows.epoch.jigsaw.Processor;
import io.zerows.epoch.jigsaw.ProcessorNetwork;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.platform.management.StoreSetting;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
@Slf4j
public class ORepositoryOption extends AbstractAmbiguity implements ORepository {

    public ORepositoryOption(final HBundle bundle) {
        super(bundle);
    }

    /**
     * 单机版和非单机版统一调用此流程实现配置的基本管理，直接根据传入的 {@link HSetting} 来执行配置的初始化流程
     * <pre><code>
     *     1. 单机版
     *     2. OSGI版：执行其他流程
     * </code></pre>
     *
     * <pre><code>
     *     1. {@link HBundle}
     *     2. {@link HSetting}
     * </code></pre>
     */
    @Override
    public void whenStart(final HSetting setting) {
        // 构造缓存基础数据
        final NodeNetwork network = new NodeNetwork();

        // 构造处理器
        final Processor<NodeNetwork, HSetting> processor = ProcessorNetwork.of();
        processor.makeup(network, setting);
        StoreSetting.of().add(setting, network);
    }

    @Override
    public void whenUpdate(final HSetting setting) {
        Objects.requireNonNull(this.caller());
        // 构造缓存基础数据
        final NodeNetwork network = StoreSetting.of().getNetwork(setting);

        /*
         * 和单机版不一样，单机版本会在类就在时就初始化 NodeNetwork，而多个 Bundle 安装时有可能每个 NodeNetwork 的安装流程不一致，
         * 所以此处会判断是否已经初始化，如果已经初始化则不再初始化，否则会初始化。
         */
        if (network.isOk()) {
            final Processor<NodeNetwork, HSetting> processor = ProcessorNetwork.of();
            processor.makeup(network, setting);
            log.info("[ ZERO ] whenUpdate 更新 -> 为 Bundle = {} 更新配置完成！", this.caller().name());
        }
    }
}