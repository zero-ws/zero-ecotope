/**
 * 关于缓存的整体架构设计
 * <pre>
 *     1. SharedMap：Vertx 的内脏，用于器官协调、通信、状态同步
 *        - LocalMap：内存隔离的 Verticle 共享内存区
 *        - AsyncMap：集群内存共享，支持分布式场景，依赖不同的集群管理器，如 Hazelcast、Infinispan 等
 *     2. Redis/Caffeine：外脑和口袋，存储业务数据、加速读取
 * </pre>
 * 结构处理
 * <pre>
 *
 * </pre>
 */
package io.zerows.plugins.cache;