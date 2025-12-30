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
        final YmMonitor.Role.RoleBuilder builder = YmMonitor.Role.builder();
        final Set<YmMonitor.Role> roleSet = new HashSet<>();
        final Map<String, JsonObject> roleConfigMap = this.ofRoleConfig();
        this.ofRoleName().forEach((roleId, roleComponent) -> {
            final String key = QUOTA_NS_PREFIX + roleId;
            builder.id(key).component(roleComponent);
            final JsonObject config = roleConfigMap.getOrDefault(roleId, new JsonObject());
            builder.config(config);
            final Map<String, Integer> roleAtMap = this.ofRoleAt();
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
