package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.web.WebReceipt;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scanned @Queue clazz to build Receipt metadata
 */
public class ExtractorReceipt implements Extractor<Set<WebReceipt>> {

    @Override
    public Set<WebReceipt> extract(final Class<?> clazz) {
        // 1. Class verify
        ExtractTool.verifyNoArgConstructor(clazz);
        ExtractTool.verifyIfPublic(clazz);

        // 2. Scan method to find @Address
        final Set<WebReceipt> receipts = new HashSet<>();
        final Method[] methods = clazz.getDeclaredMethods();

        Arrays.stream(methods)
            .filter(ExtractToolMethod::isValid)
            .filter(method -> method.isAnnotationPresent(Address.class))
            /*
             * New workflow of @QaS / @Queue bridge
             * -- @Queue / Zero Container Worker
             * -- @QaS   / Aeon Container Worker
             */
            .map(BridgeForAeon::receipt)
            .forEach(receipt -> {
                // 🛑 核心修改：查重并抛出异常
                // add 返回 false 表示 Address 已存在 (基于 WebReceipt.equals)
                if (!receipts.add(receipt)) {
                    final String message = Ut.fromMessage(
                        "[ ZERO ] ( 🛑 Duplicated ) 地址冲突！同一个类中定义了重复的 @Address。\n\t Class: {0}\n\t Method: {1}\n\t Address: {2}",
                        clazz.getName(),
                        receipt.getMethod().getName(),
                        receipt.getAddress()
                    );
                    // 直接抛出运行时异常，中断启动
                    throw new IllegalStateException(message);
                }
            });

        return receipts;
    }
}