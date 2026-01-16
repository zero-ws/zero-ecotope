package io.zerows.boot.test;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Up;
import io.zerows.epoch.configuration.ZeroConfigurer;
import io.zerows.extension.module.mbsecore.exception._80517Exception404DataAtomNull;
import io.zerows.extension.module.mbsecore.metadata.builtin.DataAtom;
import io.zerows.platform.enums.Environment;
import io.zerows.platform.exception._11010Exception500BootIoMissing;
import io.zerows.platform.metadata.KFabric;
import io.zerows.program.Ux;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.BootIo;
import io.zerows.spi.HPI;
import io.zerows.support.Fx;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2023-06-13
 */
@Up
public class LauncherTest {

    public static Future<KFabric> fabric(final DataAtom atom, final String nameB) {
        if (Objects.isNull(atom)) {
            return FnVertx.failOut(_80517Exception404DataAtomNull.class);
        }
        return of(Environment.Production).compose(okA -> {
            final PartyB partyB = okA.partyB(nameB);
            return partyB.fabric(atom.identifier()).compose(fabric -> {
                fabric.mapping().bind(atom.type());
                return Ux.future(fabric);
            });
        });
    }

    public static Future<PartyA> of(final Environment environment) {
        return of(Ux.nativeVertx(), environment)
            .otherwise(Fx.otherwiseFn(null));
    }

    public static Future<PartyA> of(final Vertx vertx, final Environment environment) {
        return switch (environment) {
            case Mockito -> of(vertx, ForMockito::new);
            case Production -> of(vertx, ForProduction::new);
            case Development -> of(vertx, ForDevelopment::new);
        };
    }

    private static Future<PartyA> of(final Vertx vertx, final Supplier<PartyA> supplier) {
        final ZeroConfigurer<Vertx> configurer = configurer();
        final Promise<PartyA> promise = Promise.promise();
        // 触发 Pre
        final HConfig config = configurer.onConfig();
        configurer.preExecute(vertx, config);
        // whenInstruction 中会执行应用注册，得到最终的 HAmbient 信息
        //        Electy.whenInstruction((vertxRef, configRef) -> {
        //            final PartyA okA = supplier.get();
        //            promise.complete(okA);
        //        }).accept(vertx, configurer.onConfig());
        return promise.future();
    }

    private static ZeroConfigurer<Vertx> configurer() {
        /*  提取SPI部分，严格模式  */

        final BootIo io = HPI.findOne(BootIo.class);
        if (Objects.isNull(io)) {
            throw new _11010Exception500BootIoMissing(LauncherTest.class);
        }
        // final HEnergy energy = io.energy(Ok.class, new String[]{});

        return null; // ZeroConfigurer.of(energy);
    }
}
