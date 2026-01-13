package io.zerows.cosmic.plugins.job;

import io.zerows.cosmic.plugins.job.metadata.Mission;
import io.zerows.platform.enums.EmService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <pre>
 * 📦 JobStoreUnity — 统一任务存储桥接器
 *
 * 说明:
 * 1. 负责整合不同来源的任务定义（编程定义与存储定义）
 * 2. 将只读（Programming）任务与可编辑（Dynamic/Stored）任务合并为统一集合
 * 3. 对一次性任务（ONCE）的状态进行修正（当处于 STARTING 时转换为 STOPPED）
 * 4. 将最终任务集合同步到 JobPool，并在增删改操作时代理到具体实现
 *
 * 主要职责与特性:
 * - 聚合 reader 与 store 两类 JobStore 的结果
 * - 过滤、合并并保持任务一致性
 * - 修正特定类型任务的运行状态
 * - 在增删改操作时同步 JobPool，并将变更委托给 store 实现
 *
 * 注意事项:
 * - reader 为只读的编程任务来源（🔒），不可修改
 * - store 为可修改的存储任务来源（📝），可进行增删改查
 * - fetch(code) 优先通过外部 Client 拉取，降级到 reader 或 store
 *
 * Emoji 高亮:
 * 🔒 只读任务来自 reader
 * 📝 可编辑任务来自 store
 * 🔁 合并结果并同步到 JobPool
 * ⚠️ ONCE 类型任务需要状态修正
 * </pre>
 */
@Slf4j
class JobStoreUnity implements JobStore {
    private static final AtomicBoolean LOGGED = new AtomicBoolean(Boolean.TRUE);
    /*
     * 编程任务来源（只读，无法修改）
     */
    private final transient JobStore reader = new JobStoreCode();
    /*
     * 存储任务定义（可修改）
     */
    private final transient JobStore store = new JobStoreExtension();

    @Override
    public Set<Mission> fetch() {
        /*
         * 在此处将所有任务进行拆分
         * 1) 编程定义的任务均为只读（Fixed 值，不可更新）
         * 2) 存储的任务可编辑（Dynamic 存储在 I_JOB 中）
         * 3) 在此处再次校验任务的 readOnly 标志，确保其被正确设置
         */
        final Set<Mission> missions = this.reader.fetch()
            .stream()
            .filter(Mission::isReadOnly)
            .collect(Collectors.toSet());

        final Set<Mission> storage = this.store.fetch()
            .stream()
            .filter(mission -> !mission.isReadOnly())
            .collect(Collectors.toSet());
        if (LOGGED.getAndSet(Boolean.FALSE)) {
            log.info("[ ZERO ] ( Job ) 初始扫描任务：Programming = {}, Dynamic/Stored = {}",
                missions.size(), storage.size());
        }

        /* 合并 */
        final Set<Mission> result = new HashSet<>();
        result.addAll(missions);
        result.addAll(storage);

        /*
         * ONCE 类型的状态修正
         * 说明:
         * - 一次性任务如果处于 STARTING 状态，由于不会真正启动，需要将其改为 STOPPED
         */
        result.stream()
            .filter(mission -> EmService.JobType.ONCE == mission.getType())
            .filter(mission -> EmService.JobStatus.STARTING == mission.getStatus())
            .forEach(mission -> mission.setStatus(EmService.JobStatus.STOPPED));

        /* 同步到 JobPool */
        JobControl.save(result);
        return result;
    }

    @Override
    public JobStore add(final Mission mission) {
        JobControl.save(mission);
        return this.store.add(mission);
    }

    @Override
    public Mission fetch(final String code) {
        final JobClient client = JobClientAddOn.of().createSingleton();
        Mission mission = client.fetch(code);
        if (Objects.isNull(mission)) {
            mission = this.reader.fetch(code);
            if (Objects.isNull(mission)) {
                mission = this.store.fetch(code);
            }
        }
        return mission;
    }

    @Override
    public JobStore remove(final Mission mission) {
        final JobClient client = JobClientAddOn.of().createSingleton();
        client.remove(mission.getCode());
        return this.store.remove(mission);
    }

    @Override
    public JobStore update(final Mission mission) {
        JobControl.save(mission);
        return this.store.update(mission);
    }
}
