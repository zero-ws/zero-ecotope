package io.zerows.epoch.bootplus.extension.refine;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.bootplus.extension.cv.em.TypeLog;
import io.zerows.epoch.bootplus.stellar.ArgoStore;
import io.zerows.epoch.constant.VDBC;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.platform.constant.VString;
import io.zerows.platform.metadata.KIdentity;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 环境配置工具
 * <pre><code>
 *    1. 基本介绍
 *       系统中的基础环境配置专用工具类。
 *
 *    2. 支持功能
 *       - 检查是否开启了ITSM环境。
 *       - 读取<strong>六维度</strong>的基础配置信息。
 *       - 日志配置解析和读取。
 *       - 标识规则选择器读取/Commutator生命周期选择器读取。
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
final class OxConfig {
    /**
     * 错误信息Map，存储了错误信息的哈希表，从`ko`中提取错误信息。
     * ```json
     * // <pre><code class="json">
     *     {
     *          "ko": {
     *              "INTEGRATION_ERROR": "集成环境出现了不可预知的异常，请联系管理员！",
     *              "TODO_ONGOING": "将要生成待确认的配置项正在＂待确认＂流程，完成待确认后才可重新生成！",
     *              "PUSH_NONE": "标识规则没满足，系统自动过滤该数据不执行推送！",
     *              "PUSH_FAIL": "推送过程中出现了不可预知的错误信息！"
     *          }
     *     }
     * // </code></pre>
     * ```
     */
    private static final ConcurrentMap<TypeLog, String> MESSAGE = new ConcurrentHashMap<>();

    static {
        /*
         * 日志
         */
        final JsonObject configuration = ArgoStore.configuration();
        final JsonObject koJson = configuration.getJsonObject("ko");
        if (Ut.isNotNil(koJson)) {
            koJson.fieldNames().forEach(field -> {
                final String message = koJson.getString(field);
                if (Ut.isNotNil(message)) {
                    final TypeLog logKey = Ut.toEnum(field, TypeLog.class);
                    if (Objects.nonNull(logKey)) {
                        MESSAGE.put(logKey, message);
                    }
                }
            });
        }
    }

    /*
     * 私有构造函数（工具类转换）
     */
    private OxConfig() {
    }

    static boolean onOff(final String key) {

        final JsonObject configuration = ArgoStore.configuration();

        final JsonObject onOff = Ut.valueJObject(configuration, "join-off");

        return onOff.getBoolean(key, Boolean.FALSE);
    }

    /**
     * <findRunning>cmdb.commutator</findRunning>，反射专用生命周期处理器配置（下层调用上层，使用反射，不能直接使用类型）。
     *
     * @param commutator `io.zerows.epoch.bootplus.extension.operation.workflow.Commutator`类型默认值
     *
     * @return {@link Class} 返回最终的 clazz 值
     */
    static Class<?> toCommutator(final Class<?> commutator) {
        // 新版方法内部调用 ArgoStore.configuration()
        final JsonObject configuration = ArgoStore.configuration();
        final String clsStr = configuration.getString("cmdb.commutator");
        if (Ut.isNil(clsStr)) {
            return commutator;
        } else {
            return Ut.clazz(clsStr, commutator);
        }
    }

    /**
     * 构造标识规则选择器，读取插件<findRunning>plugin.identifier</findRunning>值提取标识规则选择器。
     *
     * @param atom    {@link DataAtom} 模型定义
     * @param options {@link JsonObject} 服务配置选项
     *
     * @return {@link KIdentity} 构造好的标识规则选择器
     */
    static KIdentity toIdentity(final DataAtom atom, final JsonObject options) {
        final String identifierCls = options.getString(VDBC.I_SERVICE.SERVICE_CONFIG.PLUGIN_IDENTIFIER);
        /*
         * KIdentity 的静态构造
         * 注：sigma 是必须的参数
         */
        final KIdentity identity = new KIdentity();
        identity.setIdentifier(atom.identifier());
        identity.setIdentifierComponent(Ut.clazz(identifierCls));
        identity.setSigma(atom.ark().sigma());
        return identity;
    }

    /**
     * 根据日志类型读取日志信息。
     *
     * @param log {@link TypeLog} 日志类型
     *
     * @return 返回该日志类型中的打印日志内容
     */
    static String toMessage(final TypeLog log) {
        if (Objects.isNull(log)) {
            return VString.EMPTY;
        } else {
            return MESSAGE.getOrDefault(log, VString.EMPTY);
        }
    }
}
