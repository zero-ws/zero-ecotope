package io.zerows.support;

import io.r2mo.vertx.function.FnVertx;

/**
 * 锚点函数，直接继承自 {@link FnVertx} ，用于在 Zero 生态中统一引用，由于 FnVertx 全部是静态方法，所以无需实例化，
 * 这里的“继承”并非面向对象意义上的继承，而是为了在 Zero 生态中提供一个统一的访问点，属于：静态聚合，做工具使用。
 */
public final class Fx extends FnVertx {
    private Fx() {
    }

    public static void runTimer() {
        System.out.println("TIME@" + Thread.currentThread() + " / " + System.currentTimeMillis());
    }

    public static void runTimer(final int prefix) {
        System.out.println("TIME#" + prefix + "@" + Thread.currentThread() + " / " + System.currentTimeMillis());
    }
}
