package io.zerows.extension.module.ambient.common.em;

import java.util.Locale;

public enum InstanceStatus {
    READY,
    DEPLOYING,
    DEPLOYED,
    RUNNING,
    STOPPED,
    FAILED,
    UNDEPLOYED,
    NOT_FOUND;

    public String value() {
        return this.name();
    }

    public boolean matches(final String value) {
        return this == from(value);
    }

    public static InstanceStatus from(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        final String normalized = value.trim().toUpperCase(Locale.ROOT);
        for (final InstanceStatus status : values()) {
            if (status.name().equals(normalized)) {
                return status;
            }
        }
        return null;
    }

    public static String normalize(final String value, final InstanceStatus fallback) {
        final InstanceStatus status = from(value);
        if (status != null) {
            return status.value();
        }
        if (value == null || value.trim().isEmpty()) {
            return fallback == null ? null : fallback.value();
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    public static String dockerStatus(final String containerStatus) {
        final String normalized = normalize(containerStatus, NOT_FOUND);
        switch (normalized) {
            case "RUNNING":
                return RUNNING.value();
            case "EXITED":
            case "CREATED":
            case "RESTARTING":
            case "PAUSED":
                return STOPPED.value();
            case "DEAD":
                return FAILED.value();
            default:
                return normalized;
        }
    }

    public static boolean isFailure(final String value) {
        return FAILED.matches(value);
    }
}
