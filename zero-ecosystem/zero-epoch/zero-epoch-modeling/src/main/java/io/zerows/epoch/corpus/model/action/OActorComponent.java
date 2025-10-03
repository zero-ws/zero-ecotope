package io.zerows.epoch.corpus.model.action;

import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.corpus.model.Receipt;
import io.zerows.enums.app.ServerType;

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
 *     - Agent 组件专用 {@link Event}
 *       以及和服务器类型相关的 Agent 元数据定义表
 *     - Worker 组件专用 {@link Receipt}
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
public class OActorComponent implements Serializable {

    private final Set<Event> events = new HashSet<>();
    private final Set<Receipt> receipts = new HashSet<>();

    private final ConcurrentMap<String, Set<Event>> filters = new ConcurrentHashMap<>();
    private final ConcurrentMap<ServerType, List<Class<?>>> agents = new ConcurrentHashMap<>();

    public void addEvents(final Set<Event> events) {
        // 追加扫描结果集
        this.events.addAll(events);
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void addReceipts(final Set<Receipt> receipts) {
        // 追加扫描结果集
        this.receipts.addAll(receipts);
    }

    public Set<Receipt> getReceipts() {
        return this.receipts;
    }

    public void addFilters(final ConcurrentMap<String, Set<Event>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<Event> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.addAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public ConcurrentMap<String, Set<Event>> getFilters() {
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

    public void removeFilters(final ConcurrentMap<String, Set<Event>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<Event> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.removeAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public ConcurrentMap<ServerType, List<Class<?>>> getAgents() {
        return this.agents;
    }

    public void add(final OActorComponent actor) {
        this.addEvents(actor.getEvents());
        this.addReceipts(actor.getReceipts());
        this.addFilters(actor.getFilters());
        this.addAgents(actor.getAgents());
    }

    public void remove(final OActorComponent actor) {
        this.events.removeAll(actor.getEvents());
        this.receipts.removeAll(actor.getReceipts());
        this.removeFilters(actor.getFilters());
        this.removeAgents(actor.getAgents());
    }
}
