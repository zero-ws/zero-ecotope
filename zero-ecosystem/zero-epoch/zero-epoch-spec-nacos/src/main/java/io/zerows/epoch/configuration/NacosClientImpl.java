package io.zerows.epoch.configuration;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * 🔌 Nacos 客户端实现 (Nacos Client Implementation)
 *
 * <p>
 * 负责与 Nacos Config Server 进行底层交互。
 * 流程参考 {@code ZeroFs}：拉取 String -> 环境变量编译 -> YAML 解析。
 * </p>
 *
 * @author lang : 2025-10-06
 */
@Slf4j
class NacosClientImpl implements NacosClient {

    @Override
    public String readConfig(final NacosMeta meta, final NacosOptions serverOptions) {
        // 1. 准备连接属性
        final Properties properties = this.buildProperties(serverOptions);

        final String dataId = meta.getDataId();

        // 确定 Group: 优先使用 DSL 中的参数 (?group=X), 其次使用 Options 中的配置
        String group = meta.getParams().getString("group");
        if (Ut.isNil(group)) {
            group = serverOptions.getConfig().getGroup();
        }

        // 确定 Timeout
        final long timeout = serverOptions.getConfig().getTimeout();

        try {
            // 2. 创建 ConfigService
            final ConfigService configService = NacosFactory.createConfigService(properties);

            log.info("[ ZERO ] ( Nacos ) 开始拉取配置: DataID={}, Group={}, Timeout={}ms", dataId, group, timeout);

            // 3. 拉取原始内容 (String)
            final String content = configService.getConfig(dataId, group, timeout);
            // 🔥🔥🔥【调试代码】打印出来看看，确认 Nacos 是否真的返回了数据
            // System.err.println(">>>>> [DEBUG NACOS] DataID: " + dataId);
            // System.err.println(">>>>> [DEBUG NACOS] Group:  " + group);
            // System.err.println(">>>>> [DEBUG NACOS] Content:\n" + content);
            // 🔥🔥🔥 调试完记得删除
            // 4. 空值处理
            if (Ut.isNil(content)) {
                if (meta.isOptional()) {
                    log.warn("[ ZERO ] ( Nacos ) 可选配置内容为空: {}", dataId);
                    return null;
                } else {
                    throw new _500ServerInternalException("Nacos 必选配置内容为空: " + dataId);
                }
            }
            return content;
        } catch (final Exception e) {
            // 7. 异常处理
            if (meta.isOptional()) {
                log.warn("[ ZERO ] ( Nacos ) 可选配置加载失败 [{}]: {}", dataId, e.getMessage());
                return null;
            }

            // 避免二次包装 RuntimeException
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            // 修正：单参数构造，不传递 cause
            throw new _500ServerInternalException("Nacos 交互异常: " + e.getMessage());
        }
    }

    private Properties buildProperties(final NacosOptions options) {
        final Properties p = new Properties();
        final NacosOptions.Config config = options.getConfig();

        // 基础连接
        p.put(PropertyKeyConst.SERVER_ADDR, config.getServerAddr());

        if (Ut.isNotNil(config.getNamespace())) {
            p.put(PropertyKeyConst.NAMESPACE, config.getNamespace());
        }

        if (Ut.isNotNil(options.getUsername())) {
            p.put(PropertyKeyConst.USERNAME, options.getUsername());
            p.put(PropertyKeyConst.PASSWORD, options.getPassword());
        }

        // 扩展配置 (Spring 对齐)
        p.put(PropertyKeyConst.ENCODE, config.getEncode());
        // 将读取超时同时也设置为长轮询超时，保持逻辑一致
        p.put(PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT, String.valueOf(config.getTimeout()));

        return p;
    }
}