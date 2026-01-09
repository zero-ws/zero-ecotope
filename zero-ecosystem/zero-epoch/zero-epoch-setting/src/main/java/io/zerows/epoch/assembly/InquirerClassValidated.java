package io.zerows.epoch.assembly;

import io.zerows.epoch.annotations.Validated;
import io.zerows.epoch.configuration.Inquirer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class InquirerClassValidated implements Inquirer<Set<Class<?>>> {

    public static final String MESSAGE = "[ ZERO ] ( {} Validated ) \uD83E\uDDEC Zero 中扫描到 {} 个包含 @Validated 的组件。";

    @Override
    public Set<Class<?>> scan(final Set<Class<?>> clazzes) {
        final Set<Class<?>> validated = clazzes.stream()
            .filter(this::isValid)
            .collect(Collectors.toSet());
        if (!validated.isEmpty()) {
            log.info(MESSAGE, validated.size(), validated.size());
        }
        return validated;
    }

    private boolean isValid(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
            .filter(method -> !Modifier.isStatic(method.getModifiers()))
            .anyMatch(method -> Arrays.stream(method.getParameters())
                .anyMatch(parameter -> parameter.isAnnotationPresent(Validated.class)));
    }
}
