package io.zerows.epoch.sdk.osgi;

import org.osgi.framework.Bundle;

/**
 * 执行命令专用接口
 *
 * @author lang : 2024-04-22
 */
public interface OCommand {

    void execute(Bundle caller);
}
