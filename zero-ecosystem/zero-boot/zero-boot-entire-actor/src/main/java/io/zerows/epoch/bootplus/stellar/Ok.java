package io.zerows.epoch.bootplus.stellar;

import io.r2mo.spi.SPI;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.boot.supply.Electy;
import io.zerows.epoch.bootplus.stellar.owner.DevelopmentA;
import io.zerows.epoch.bootplus.stellar.owner.MockitoA;
import io.zerows.epoch.bootplus.stellar.owner.OkA;
import io.zerows.epoch.bootplus.stellar.owner.ProductionA;
import io.zerows.epoch.bootplus.stellar.vendor.OkB;
import io.zerows.component.shared.boot.KConfigurer;
import io.zerows.component.shared.boot.KEnvironment;
import io.zerows.component.shared.datamation.KFabric;
import io.zerows.epoch.corpus.Ux;
import io.zerows.enums.Environment;
import io.zerows.exception.boot._11010Exception500BootIoMissing;
import io.zerows.spi.BootIo;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.exception._80517Exception404DataAtomNull;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HEnergy;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-13
 */
@Up
public class Ok {

    public static Future<KFabric> fabric(final DataAtom atom, final String nameB) {
        if (Objects.isNull(atom)) {
            return FnVertx.failOut(_80517Exception404DataAtomNull.class);
        }
        return of(Environment.Production).compose(okA -> {
            final OkB partyB = okA.partyB(nameB);
            return partyB.fabric(atom.identifier()).compose(fabric -> {
                fabric.mapping().bind(atom.type());
                return Ux.future(fabric);
            });
        });
    }

    public static Future<OkA> of(final Environment environment) {
        return of(Ux.nativeVertx(), environment).otherwise(Ux.otherwise(null));
    }

    public static Future<OkA> of(final Vertx vertx, final Environment environment) {
        return switch (environment) {
            case Mockito -> of(vertx, MockitoA::new);
            case Production -> of(vertx, ProductionA::new);
            case Development -> of(vertx, DevelopmentA::new);
        };
    }

    private static Future<OkA> of(final Vertx vertx, final Supplier<OkA> supplier) {
        final KConfigurer<Vertx> configurer = configurer();
        final Promise<OkA> promise = Promise.promise();
        // 触发 Pre
        final HConfig config = configurer.onConfig();
        configurer.preExecute(vertx, config);
        // whenInstruction 中会执行应用注册，得到最终的 HAmbient 信息
        Electy.whenInstruction((vertxRef, configRef) -> {
            final OkA okA = supplier.get();
            promise.complete(okA);
        }).accept(vertx, configurer.onConfig());
        return promise.future();
    }

    private static KConfigurer<Vertx> configurer() {
        /*  提取SPI部分，严格模式  */

        final BootIo io = SPI.findOne(BootIo.class);
        if (Objects.isNull(io)) {
            throw new _11010Exception500BootIoMissing(Ok.class);
        }
        final HEnergy energy = io.energy(Ok.class, new String[]{});


        /*
         * 环境提取，此处环境变量提取为非容器启动环境，而是单纯的模拟环境处理，这样的处理模式之下
         * 环境变量为测试 Mock 提供了第一模拟环境，来完成环境变量基础注入流程
         **/
        KEnvironment.initialize();

        return KConfigurer.of(energy);
    }
}
