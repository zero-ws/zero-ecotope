package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.basicore.Receipt;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scanned @Queue clazz to build Receipt metadata
 */
public class ExtractorReceipt implements Extractor<Set<Receipt>> {

    @Override
    public Set<Receipt> extract(final Class<?> clazz) {
        // 1. Class verify
        ExtractToolVerifier.noArg(clazz);
        ExtractToolVerifier.modifier(clazz);
        // 2. Scan method to find @Address
        final Set<Receipt> receipts = new HashSet<>();
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
            .forEach(receipts::add);
        return receipts;
    }
}
