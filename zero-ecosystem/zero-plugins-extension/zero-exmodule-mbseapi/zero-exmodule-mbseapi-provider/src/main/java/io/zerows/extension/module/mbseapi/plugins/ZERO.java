package io.zerows.extension.module.mbseapi.plugins;

import io.r2mo.typed.cc.Cc;
import io.zerows.extension.module.mbseapi.component.JtAim;

import java.util.HashSet;
import java.util.Set;

interface POOL {

    Cc<String, JtAim> CC_AIM = Cc.openThread();

    // Address
    Set<Class<?>> WORKER_SET = new HashSet<>();
    Set<Class<?>> WORKER_JS_SET = new HashSet<>();
}

interface INFO {
    String DYNAMIC_DETECT = "( {0} ) The system is detecting dynamic routing component...";

    String DYNAMIC_SKIP = "( {0} ) Skip dynamic routing because clazz is null or class {1} is not assignable from \"io.zerows.extension.router.PlugRouter\".";

    String DYNAMIC_FOUND = "( {0} ) Zero system detect class \"{1}\" with config {2}.";
}