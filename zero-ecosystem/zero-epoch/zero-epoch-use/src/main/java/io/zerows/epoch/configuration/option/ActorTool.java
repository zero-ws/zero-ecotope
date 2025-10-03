package io.zerows.epoch.configuration.option;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;
import io.zerows.epoch.annotations.Agent;
import io.zerows.epoch.annotations.Worker;
import io.zerows.epoch.component.transformer.TransformerActor;
import io.zerows.enums.EmDeploy;
import io.zerows.epoch.program.Ut;

import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * 配合双流程的专用工具类
 * <pre><code>
 *     1. 标准模式下，class 会优先进入类加载器，这种场景中 delay = false，则直接在 {@link TransformerActor} 中就直接将
 *        {@link DeploymentOptions} 构造完成，针对 Annotation 中配置的数据执行附加元数据的处理
 *        - ha
 *        - instances
 *        双数据覆盖
 *     2. OSGI 模式下，由于 Configuration 配置中心在进入环境的时候 class 可能还没有准备好，所以 delay = true，这种模式下
 *        就只能在真正发布 Verticle 的时候执行修改，延迟处理。
 * </code></pre>
 * 只有模式为 CODE 的时候会考虑此工具的注入流程，如果模式不为 CODE，则直接忽略跳过。
 *
 * @author lang : 2024-04-29
 */
public class ActorTool {

    public static void setupWith(final DeploymentOptions deploymentOptions,
                                 final Class<?> clazz,
                                 final EmDeploy.Mode mode) {

        Annotation annotation = clazz.getDeclaredAnnotation(Worker.class);
        if (Objects.isNull(annotation)) {
            annotation = clazz.getDeclaredAnnotation(Agent.class);
            if (Objects.isNull(annotation)) {
                return;
            }
        }


        if (Worker.class == annotation.annotationType()) {
            // Worker
            if (ThreadingModel.VIRTUAL_THREAD != deploymentOptions.getThreadingModel()) {
                deploymentOptions.setThreadingModel(ThreadingModel.WORKER);
            }
        } else {
            // Agent
            deploymentOptions.setThreadingModel(ThreadingModel.EVENT_LOOP);
        }


        if (EmDeploy.Mode.CODE == mode) {
            // ha processing
            final boolean ha = Ut.invoke(annotation, "ha");
            deploymentOptions.setHa(ha);
            // instances
            final int instances = Ut.invoke(annotation, "instances");
            deploymentOptions.setInstances(instances);
        }
    }
}
