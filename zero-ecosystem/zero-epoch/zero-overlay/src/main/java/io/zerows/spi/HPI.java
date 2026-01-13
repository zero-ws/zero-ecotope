package io.zerows.spi;

import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.base.generator.GenProcessor;
import io.r2mo.base.io.HStore;
import io.r2mo.base.io.HTransfer;
import io.r2mo.base.secure.EDCrypto;
import io.r2mo.jaas.session.UserCache;
import io.r2mo.spi.*;
import io.r2mo.typed.cc.Cc;
import io.r2mo.vertx.dbe.DBContext;
import io.r2mo.vertx.dbe.FactoryDBAsync;
import io.vertx.core.Future;
import io.zerows.platform.constant.VString;
import io.zerows.specification.configuration.HLauncher;
import io.zerows.specification.configuration.HRegistry;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.modeling.operation.HLoad;
import io.zerows.spi.modeler.AtomDiff;
import io.zerows.spi.modeler.AtomNo;
import io.zerows.spi.modeler.AtomNs;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 直接从 SPI 继承接口，对 SPI 功能进行扩展，主要追加功能支持：是否覆盖默认的 SPI 单独执行器
 *
 * @author lang : 2025-10-02
 */
@Slf4j
public final class HPI<T> extends SPI {

    private static final List<Class<?>> SPI_SET = new ArrayList<>() {
        {
            // ------------------------ 抽象层
            // io.r2mo.spi 包
            this.addAll(List.of(
                FactoryObject.class,        // 🏭 R2MO 核心对象工厂接口，用于创建和管理基础对象实例
                FactoryIo.class,            // 💾 R2MO IO 工厂接口，处理输入输出相关操作
                FactoryDBAction.class,      // 🗄️ 数据库操作工厂接口，定义数据库基本操作规范
                FactoryWeb.class            // 🌐 Web 相关工厂接口，处理 Web 请求和响应
            ));
            // io.r2mo.jaas.session 包
            this.add(EDCrypto.class);       // 🔐 加密解密工具类，提供会话安全相关的加密功能

            // ------------------------ R2MO 默认实现层
            // io.r2mo.base.*
            this.addAll(List.of(
                HStore.class,               // 📦 数据存储接口，提供通用的数据存储能力
                HTransfer.class,            // 🔄 数据传输接口，处理数据在不同层级间的传递
                DBS.CPFactory.class,        // ⚙️ 数据库连接池工厂，负责数据库连接管理
                GenProcessor.class          // 🎛️ 通用处理器接口，提供可插拔的处理逻辑
            ));
            this.add(UniProvider.class);    // 🌉 统一提供者接口，作为统一的服务提供入口


            this.add(UserCache.class);      // 👤 用户缓存管理接口，处理用户会话和信息缓存
            // io.r2mo.vertx.*
            this.addAll(List.of(
                FactoryDBAsync.class,       // 🚀 异步数据库操作工厂，基于 Vert.x 提供异步数据库访问
                DBContext.class,            // 📂 数据库上下文接口，维护数据库操作所需的上下文信息
                DBLoad.class                // 📥 数据加载接口，处理数据库数据的加载和初始化
            ));


            // 应用部分
            this.addAll(List.of(
                BootIo.class,               // 🥾 启动 IO 接口，处理系统启动时的 IO 操作
                AtomNs.class,               // 🧬 原子命名空间接口，处理系统中的原子化命名空间
                AtomNo.class,               // 📑 模型序号专用
                AtomDiff.class              // 🧩 模型比对专用
            ));

            // io.zerows.specification.*
            this.addAll(List.of(
                HRegistry.class,            // 📋 注册表接口，管理系统中的各类注册信息
                HBundle.class,              // 📦 模块包接口，定义系统模块化打包规范
                HLauncher.class,            // 🚀 启动器接口，控制系统各组件的启动流程
                HLoad.class                 // ⚖️ 加载器接口，处理系统资源和配置的加载
            ));
        }
    };
    // ------------------- HPI 对象模式，直接处理对象引用功能
    private static final Cc<String, HPI<?>> CC_HPI = Cc.openThread();
    private final T service;

    private HPI(final Class<T> interfaceCls) {
        final T service = findOverwrite(interfaceCls);
        if (Objects.isNull(service)) {
            log.warn("[ ZERO ] 功能性旁路 HPI / 接口 = {} 对应的实现服务在环境中未找到！", interfaceCls.getName());
        }
        this.service = service;
    }

    public static void registry(final Class<?>... spiArray) {
        SPI_SET.addAll(Arrays.asList(spiArray));
    }

    public static HBundle findBundle(final Class<?> clazzLoader) {
        return findOverwrite(HBundle.class, clazzLoader);
    }

    public static void vLog() {

        log.info("[ ZERO ] SPI 监控详情：");
        for (final Class<?> spiClass : SPI_SET) {
            final List<?> implementations = findMany(spiClass);
            final String implNames = implementations.isEmpty()
                ? VString.EMPTY
                : implementations.stream()
                .map(impl -> impl.getClass().getName())
                .distinct()
                .collect(Collectors.joining(", "));
            log.info("[ ZERO ]    \uD83D\uDCCC {} = [{}]", String.format("%-64s", spiClass.getName()), implNames);
        }
    }

    /**
     * 此处有一点需要特殊说明：为何 wait?? 所有方法签名的第二参采用了 {@link Supplier} 而非直接对象引用？
     * <pre>
     *     1. 为了延迟加载默认对象，避免不必要的对象创建开销
     *     2. 保持与异步方法签名的一致性，便于理解和使用
     *     3. 避免在默认对象创建过程中出现副作用，确保只有在需要时才会执行相关逻辑
     * </pre>
     *
     * @param interfaceCls SPI 接口
     * @param <R>          SPI 中的组件类型
     * @return 返回 {@link HPI} 引用
     */
    @SuppressWarnings("unchecked")
    public static <R> HPI<R> of(final Class<R> interfaceCls) {
        Objects.requireNonNull(interfaceCls);
        return (HPI<R>) CC_HPI.pick(() -> new HPI<>(interfaceCls), interfaceCls.getName());
    }

    public <O> Future<O> waitAsync(final Function<T, Future<O>> executor, final Supplier<O> defaultSupplier) {
        // 默认流程处理 null
        if (Objects.isNull(this.service)) {
            final O defaultValue = Objects.isNull(defaultSupplier) ? null : defaultSupplier.get();
            return Future.succeededFuture(defaultValue);
        }


        // 非默认流程处理 null
        return Objects.isNull(executor) ? Future.succeededFuture() : executor.apply(this.service);
    }

    public <O> Future<O> waitAsync(final Function<T, Future<O>> executor) {
        // 默认流程处理 null
        if (Objects.isNull(this.service)) {
            return Future.succeededFuture();
        }


        // 非默认流程处理 null
        return Objects.isNull(executor) ? Future.succeededFuture() : executor.apply(this.service);
    }

    public <O> Future<O> waitOr(final Function<T, Future<O>> executor, final Supplier<Future<O>> defaultSupplier) {
        // 默认流程处理 null
        if (Objects.isNull(this.service)) {
            return Objects.isNull(defaultSupplier) ? Future.succeededFuture() : defaultSupplier.get();
        }


        // 非默认流程处理 null
        return Objects.isNull(executor) ? Future.succeededFuture() : executor.apply(this.service);
    }

    public <O> O waitUntil(final Function<T, O> executor, final Supplier<O> defaultSupplier) {
        // 默认流程处理 null
        if (Objects.isNull(this.service)) {
            return Objects.isNull(defaultSupplier) ? null : defaultSupplier.get();
        }


        // 非默认流程处理 null
        return Objects.isNull(executor) ? null : executor.apply(this.service);
    }
}
