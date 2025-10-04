package io.zerows.epoch.boot;

import io.vertx.core.Vertx;
import io.zerows.epoch.corpus.web.scheduler.store.ORepositoryJob;
import io.zerows.epoch.corpus.web.security.store.ORepositorySecurity;
import io.zerows.epoch.corpus.web.websocket.store.ORepositorySock;
import io.zerows.epoch.management.ORepositoryClass;
import io.zerows.epoch.management.ORepositoryMeta;
import io.zerows.management.ORepositoryOption;
import io.zerows.platform.metadata.KRunner;
import io.zerows.sdk.management.ORepository;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.function.BiConsumer;

/**
 * 容器统一归口，处理容器层的统一调用处理，用来控制容器底层的所有基础信息
 *
 * @author lang : 2023-06-11
 */
public class Electy {

    // ---------------------- 注册专用
    public static void initialize() {
        /*
         * Phase 0:
         * 由于所有的配置都是从配置文件中读取，属于默认行为，所以此处先执行配置文件读取，再执行扫描
         * 这样可以保证在任何所需的类加载流程中都可以读取相关配置。
         */
        ORepository.ofOr(ORepositoryOption.class).whenStart();
        /*
         * Phase 1:
         * -- Scan the whole environment to extract all classes those will be analyzed.
         * -- Guice Module Start to extract all ( DI clazz )
         * -- Class<?> Seeking ( EndPoint, Queue, HQueue )
         */
        ORepository.ofOr(ORepositoryClass.class).whenStart();


        /*
         * Phase 2:
         * 1) IPC / QaS
         * 2) @EndPoint -> Event
         *    @Queue -> Receipt
         *    @WebFilter -> JSR340
         *    Agent Component
         */
        ORepository.ofOr(ORepositoryMeta.class).whenStart();

        /*
         * Phase 2:
         * Component Scan of following:
         * -- @EndPoint -> Event
         * -- @Wall
         * -- @WebFilter
         * -- @Queue/@QaS
         * -- @Ipc
         * -- Agent Component
         * -- @WebSock
         * -- @Job
         */
        final long start = System.currentTimeMillis();
        KRunner.run("meditate-feature-component",
            // @Wall -> Authenticate, Authorize
            ORepository.ofOr(ORepositorySecurity.class)::whenStart,
            // @WebSock
            ORepository.ofOr(ORepositorySock.class)::whenStart,
            // @Job
            ORepository.ofOr(ORepositoryJob.class)::whenStart
        );
        final long end = System.currentTimeMillis();
        Ut.Log.boot(Electy.class)
            .info("Zero Timer: Meditate Feature Component = {0} ms", String.valueOf(end - start));
    }

    public static BiConsumer<Vertx, HConfig> whenInstruction(final BiConsumer<Vertx, HConfig> endFn) {
        return ElectyEntry.whenInstruction(endFn);
    }

    public static BiConsumer<Vertx, HConfig> whenContainer(final BiConsumer<Vertx, HConfig> endFn) {
        return ElectyEntry.whenContainer(endFn);
    }
}
