package io.zerows.core.web.model.uca.extract;

import io.zerows.core.annotations.Address;
import io.zerows.core.web.model.atom.Receipt;
import io.zerows.core.web.model.uca.bridge.AeonBridge;

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
        ToolVerifier.noArg(clazz, this.getClass());
        ToolVerifier.modifier(clazz, this.getClass());
        // 2. Scan method to find @Address
        final Set<Receipt> receipts = new HashSet<>();
        final Method[] methods = clazz.getDeclaredMethods();
        Arrays.stream(methods)
            .filter(ToolMethod::isValid)
            .filter(method -> method.isAnnotationPresent(Address.class))
            /*
             * New workflow of @QaS / @Queue bridge
             * -- @Queue / Zero Container Worker
             * -- @QaS   / Aeon Container Worker
             */
            .map(AeonBridge::receipt)
            .forEach(receipts::add);
        return receipts;
    }
}
