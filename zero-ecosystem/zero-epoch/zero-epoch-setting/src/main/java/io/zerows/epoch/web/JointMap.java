package io.zerows.epoch.web;

import io.zerows.platform.enums.EmDeploy;

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
public class JointMap implements Serializable {

    private final ConcurrentMap<EmDeploy.JoinPoint, JointAction> jointMap = new ConcurrentHashMap<>() {
        {
            // FIX Issue: Cannot invoke "io.zerows.core.web.model.action.atom.OJointAction.findRunning(String)" because "action" is null
            this.put(EmDeploy.JoinPoint.IPC, JointAction.of(EmDeploy.JoinPoint.IPC));
            this.put(EmDeploy.JoinPoint.QAS, JointAction.of(EmDeploy.JoinPoint.QAS));
        }
    };

    public void put(final EmDeploy.JoinPoint joinPoint, final JointAction jointAction) {
        this.jointMap.put(joinPoint, jointAction);
    }

    public void add(final JointAction jointAction) {
        final JointAction stored = this.jointMap.get(jointAction.type());
        if (Objects.isNull(stored)) {
            this.put(jointAction.type(), jointAction);
        } else {
            stored.add(jointAction);
            this.put(jointAction.type(), stored);
        }
    }

    public void remove(final JointAction jointAction) {
        final EmDeploy.JoinPoint type = jointAction.type();
        final JointAction stored = this.jointMap.get(type);
        if (Objects.nonNull(stored)) {
            stored.remove(jointAction);
        }
        if (stored.methods().isEmpty()) {
            this.jointMap.remove(type);
        }
    }

    public JointMap add(final JointMap jointMap) {
        jointMap.jointMap.forEach((join, action) -> this.add(action));
        return this;
    }

    public void remove(final EmDeploy.JoinPoint joinPoint) {
        this.jointMap.remove(joinPoint);
    }

    public JointAction get(final EmDeploy.JoinPoint joinPoint) {
        return this.jointMap.get(joinPoint);
    }
}
