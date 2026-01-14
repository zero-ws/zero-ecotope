package io.zerows.plugins.monitor.client;

import io.vertx.core.json.JsonObject;
import io.zerows.plugins.monitor.metadata.YmMonitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public abstract class QuotaValueBase implements QuotaValue {

    protected abstract Set<String> ofClientName();

    protected abstract Map<String, String> ofRoleName();

    protected Map<String, JsonObject> ofRoleConfig() {
        return Map.of();
    }

    protected Map<String, Integer> ofRoleAt() {
        return Map.of();
    }

    @Override
    public Set<YmMonitor.Role> ofRole() {
        // ❌ [Old Code] 严重错误：Builder 定义在循环外部
        // final YmMonitor.Role.RoleBuilder builder = YmMonitor.Role.builder();

        final Set<YmMonitor.Role> roleSet = new HashSet<>();
        final Map<String, JsonObject> roleConfigMap = this.ofRoleConfig();
        // 优化：将 ofRoleAt 提到循环外，避免每次循环都重复调用方法
        final Map<String, Integer> roleAtMap = this.ofRoleAt();

        this.ofRoleName().forEach((roleId, roleComponent) -> {
            // ✅ [New Code] 修正：将 Builder 移入循环内部
            /*
             * 📝 详细解释 - 为什么要移到这里？
             *
             * 1. 状态残留 (State Persistence)：
             * Lombok 的 Builder 本质上是一个普通的 Java 对象（Mutable）。
             * 如果定义在循环外，所有循环迭代共享同一个 Builder 实例。
             *
             * 2. 条件赋值的陷阱 (The Trap of Conditional Setting)：
             * 下方代码中有 if (roleAtMap.containsKey(roleId)) { builder.duration(...) }。
             * - 假设第 1 次循环：进入 if 分支，Builder 被设置 duration = 100。
             * - 假设第 2 次循环：没进入 if 分支（不应有 duration），但由于复用了同一个 Builder，
             * 它依然保留着第 1 次循环留下的 duration = 100。
             *
             * 3. 结果：
             * 导致第 2 个 Role 错误地继承了第 1 个 Role 的属性，造成数据污染。
             * 因此，必须在循环内 new 一个全新的 Builder，确保每次都是一张“白纸”。
             */
            final YmMonitor.Role.RoleBuilder builder = YmMonitor.Role.builder();

            final String key = QUOTA_NS_PREFIX + roleId;
            builder.id(key).component(roleComponent);

            final JsonObject config = roleConfigMap.getOrDefault(roleId, new JsonObject());
            builder.config(config);

            // 这里的判断逻辑必须基于全新的 builder 才能保证正确性
            if (roleAtMap.containsKey(roleId)) {
                builder.duration(roleAtMap.get(roleId));
            }
            roleSet.add(builder.build());
        });
        return roleSet;
    }

    @Override
    public Set<YmMonitor.Client> ofClient() {
        final YmMonitor.Client.ClientBuilder builder = YmMonitor.Client.builder();
        final Set<YmMonitor.Client> clientSet = new HashSet<>();
        this.ofClientName().stream()
            .map(client -> builder.name(client).enabled(Boolean.TRUE).build())
            .forEach(clientSet::add);
        return clientSet;
    }
}
