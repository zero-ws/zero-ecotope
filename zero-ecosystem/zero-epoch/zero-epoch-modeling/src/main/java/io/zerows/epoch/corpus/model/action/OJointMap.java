package io.zerows.epoch.corpus.model.action;

import io.zerows.epoch.enums.EmAction;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 方法区域专用链接器，用来存储
 * <pre><code>
 *     name = Method 的专用配置
 * </code></pre>
 *
 * @author lang : 2024-04-21
 */
public class OJointMap implements Serializable {

    private final ConcurrentMap<EmAction.JoinPoint, OJointAction> jointMap = new ConcurrentHashMap<>() {
        {
            // FIX Issue: Cannot invoke "io.zerows.core.web.model.action.atom.OJointAction.get(String)" because "action" is null
            this.put(EmAction.JoinPoint.IPC, OJointAction.of(EmAction.JoinPoint.IPC));
            this.put(EmAction.JoinPoint.QAS, OJointAction.of(EmAction.JoinPoint.QAS));
        }
    };

    public void put(final EmAction.JoinPoint joinPoint, final OJointAction jointAction) {
        this.jointMap.put(joinPoint, jointAction);
    }

    public void add(final OJointAction jointAction) {
        final OJointAction stored = this.jointMap.get(jointAction.type());
        if (Objects.isNull(stored)) {
            this.put(jointAction.type(), jointAction);
        } else {
            stored.add(jointAction);
            this.put(jointAction.type(), stored);
        }
    }

    public void remove(final OJointAction jointAction) {
        final EmAction.JoinPoint type = jointAction.type();
        final OJointAction stored = this.jointMap.get(type);
        if (Objects.nonNull(stored)) {
            stored.remove(jointAction);
        }
        if (stored.methods().isEmpty()) {
            this.jointMap.remove(type);
        }
    }

    public OJointMap add(final OJointMap jointMap) {
        jointMap.jointMap.forEach((join, action) -> this.add(action));
        return this;
    }

    public void remove(final EmAction.JoinPoint joinPoint) {
        this.jointMap.remove(joinPoint);
    }

    public OJointAction get(final EmAction.JoinPoint joinPoint) {
        return this.jointMap.get(joinPoint);
    }
}
