package io.zerows.extension.commerce.rbac.uca.authorization;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

interface Pool {
    ConcurrentMap<String, ScDetent> DETENT_POOL =
        new ConcurrentHashMap<>();
}

