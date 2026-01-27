package io.zerows.epoch.web;

import io.zerows.platform.enums.EmWeb;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 核心组件集，主要针对如下
 * <pre><code>
 *     - Agent 组件专用 {@link WebEvent}
 *       以及和服务器类型相关的 Agent 元数据定义表
 *     - Worker 组件专用 {@link WebReceipt}
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
@Getter
public class WebActor implements Serializable {

    private final Set<WebEvent> events = new HashSet<>();
    private final Set<WebReceipt> receipts = new HashSet<>();

    private final ConcurrentMap<String, Set<WebEvent>> filters = new ConcurrentHashMap<>();
    private final ConcurrentMap<EmWeb.ServerType, List<Class<?>>> agents = new ConcurrentHashMap<>();

    public void addEvents(final Set<WebEvent> events) {
        // 追加扫描结果集
        this.events.addAll(events);
    }

    public void addReceipts(final Set<WebReceipt> receipts) {
        // 追加扫描结果集
        this.receipts.addAll(receipts);
    }

    public void addFilters(final ConcurrentMap<String, Set<WebEvent>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<WebEvent> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.addAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public void addAgents(final ConcurrentMap<EmWeb.ServerType, List<Class<?>>> agents) {
        agents.forEach((type, agent) -> {
            if (Objects.nonNull(agent) && !agent.isEmpty()) {
                final List<Class<?>> stored = this.agents.getOrDefault(type, new ArrayList<>());
                agent.stream().filter(agentItem -> !stored.contains(agentItem)).forEach(stored::add);
                this.agents.put(type, stored);
            }
        });
    }

    public void removeAgents(final ConcurrentMap<EmWeb.ServerType, List<Class<?>>> agents) {
        agents.forEach((type, agent) -> {
            if (Objects.nonNull(agent) && !agent.isEmpty()) {
                final List<Class<?>> stored = this.agents.getOrDefault(type, new ArrayList<>());
                stored.removeAll(agent);
                this.agents.put(type, stored);
            }
        });
    }

    public void removeFilters(final ConcurrentMap<String, Set<WebEvent>> filters) {
        filters.forEach((path, filter) -> {
            if (Objects.nonNull(filter) && !filter.isEmpty()) {
                final Set<WebEvent> stored = this.filters.getOrDefault(path, new HashSet<>());
                stored.removeAll(filter);
                this.filters.put(path, stored);
            }
        });
    }

    public void add(final WebActor actor) {
        this.addEvents(actor.getEvents());
        this.addReceipts(actor.getReceipts());
        this.addFilters(actor.getFilters());
        this.addAgents(actor.getAgents());
    }

    public void remove(final WebActor actor) {
        this.events.removeAll(actor.getEvents());
        this.receipts.removeAll(actor.getReceipts());
        this.removeFilters(actor.getFilters());
        this.removeAgents(actor.getAgents());
    }
}
