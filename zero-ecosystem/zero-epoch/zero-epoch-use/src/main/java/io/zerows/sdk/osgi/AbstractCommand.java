package io.zerows.sdk.osgi;

import io.zerows.epoch.constant.OConstant;
import io.zerows.platform.constant.VString;
import io.zerows.support.Ut;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2024-04-17
 */
public abstract class AbstractCommand {
    protected final BundleContext context;

    protected AbstractCommand(final BundleContext context) {
        this.context = context;
    }

    /**
     * command pName pValue
     * <pre><code>
     *     command  - 命令名称
     *     pName    - 命令参数名
     *     pValue   - 命令参数值，参数值中会包含命令的执行组件 {@link OCommand} 相关信息
     * </code></pre>
     *
     * @param input        输入值
     * @param commanderMap 值处理列表
     *
     * @return 返回值
     */
    protected String execCommon(final String input, final Map<String, OCommand> commanderMap) {
        if (Ut.isInteger(input)) {
            // Auto Bundle Id
            return this.execBundle(Long.parseLong(input), commanderMap);
        } else {
            final OCommand commander = Ut.Bnd.commandBuild(input, commanderMap);
            if (Objects.nonNull(commander)) {
                commander.execute(this.context.getBundle());
            }
            return VString.EMPTY;
        }
    }

    private String execBundle(final Long bundleId, final Map<String, OCommand> commanderMap) {
        final OCommand commander = commanderMap.get(OConstant.CMD_BY_BUNDLE_ID);
        assert null != commander;
        final Bundle target = this.context.getBundle(bundleId);
        if (Objects.nonNull(target)) {
            commander.execute(target);
        } else {
            System.out.println("The bundleId = \"" + bundleId + "\" does not exist in context.");
        }
        return VString.EMPTY;
    }
}
