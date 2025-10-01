package io.zerows.epoch.corpus.model.uca.extract;

import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.corpus.model.atom.Receipt;
import io.zerows.epoch.corpus.model.uca.bridge.AeonBridge;

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
        ToolVerifier.noArg(clazz);
        ToolVerifier.modifier(clazz);
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
