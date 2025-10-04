package io.zerows.epoch.basicore;

import io.zerows.platform.enums.app.ServerType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心组件集，主要针对如下
 * <pre><code>
 *     - Agent 组件专用 {@link ActorEvent}
 *       以及和服务器类型相关的 Agent 元数据定义表
 *     - Worker 组件专用 {@link ActorReceipt}
 *       Worker 专用表
 *     - JSR340 专用的 Filter 组件表
 * </code></pre>
 * 此处不包含的组件
 * <pre><code>
 *     下边组件插件处理
 *     - 安全相关
 *     - WebSocket 相关
 * </code></pre>
 *
 * @author lang : 2024-04-21
 */
public class ActorComponent implements Serializable {

    private final Set<ActorEvent> events = new HashSet<>();
    private final Set<ActorReceipt> receipts = new HashSet<>();

    private final ConcurrentMap<String, Set<ActorEvent>> filters = new ConcurrentHashMap<>();
    private final ConcurrentMap<ServerType, List<Class<?>>> agents = new ConcurrentHashMap<>();

    public void addEvents(final Set<ActorEvent> events) {
        // 追加扫描结果集
        this.events.addAll(events);
    }

    public Set<ActorEvent> getEvents() {
        return this.events;
    }

    public void addReceipts(final Set<ActorReceipt> receipts) {
        // 追加扫描结果集
        this.receipts.addAll(receipts);
    }

    public Set<ActorReceipt> getReceipts() {
        return this.receipts;
    }

    public void addFilters(final ConcurrentMap<String, Set<ActorEvent>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<ActorEvent> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.addAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public ConcurrentMap<String, Set<ActorEvent>> getFilters() {
        return this.filters;
    }

    public void addAgents(final ConcurrentMap<ServerType, List<Class<?>>> agents) {
        agents.forEach((type, agent) -> {
            if (Objects.nonNull(agent) && !agent.isEmpty()) {
                final List<Class<?>> stored = this.agents.getOrDefault(type, new ArrayList<>());
                agent.stream().filter(agentItem -> !stored.contains(agentItem)).forEach(stored::add);
                this.agents.put(type, stored);
            }
        });
    }

    public void removeAgents(final ConcurrentMap<ServerType, List<Class<?>>> agents) {
        agents.forEach((type, agent) -> {
            if (Objects.nonNull(agent) && !agent.isEmpty()) {
                final List<Class<?>> stored = this.agents.getOrDefault(type, new ArrayList<>());
                stored.removeAll(agent);
                this.agents.put(type, stored);
            }
        });
    }

    public void removeFilters(final ConcurrentMap<String, Set<ActorEvent>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<ActorEvent> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.removeAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public ConcurrentMap<ServerType, List<Class<?>>> getAgents() {
        return this.agents;
    }

    public void add(final ActorComponent actor) {
        this.addEvents(actor.getEvents());
        this.addReceipts(actor.getReceipts());
        this.addFilters(actor.getFilters());
        this.addAgents(actor.getAgents());
    }

    public void remove(final ActorComponent actor) {
        this.events.removeAll(actor.getEvents());
        this.receipts.removeAll(actor.getReceipts());
        this.removeFilters(actor.getFilters());
        this.removeAgents(actor.getAgents());
    }
}
