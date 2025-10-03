package io.zerows.epoch.bootplus.stellar;

import io.vertx.core.json.JsonObject;
import io.zerows.constant.VWeb;
import io.zerows.epoch.corpus.metadata.element.JSix;
import io.zerows.enums.Environment;
import io.zerows.enums.typed.ChangeFlag;
import io.zerows.epoch.program.Ut;
import io.zerows.runtime.HMacrocosm;
import io.zerows.extension.runtime.skeleton.refine.Ke;
import io.zerows.specification.access.app.HAmbient;

/**
 * 读取配置文件
 * <pre><code>
 *     running/configuration.json
 * </code></pre>
 * 根据配置文件中的信息直接初始化所需的基础信息，并链接 `stellar` （旧版开发）系统为模拟测试系统做支撑
 * 此处加载的内容为上下文信息，虽然和 {@link HAmbient} 不等价，但此处
 * 的系统给开发人员提供了模拟测试环境，方便搭建测试框架，针对任何开发内容进行测试。
 * <pre><code>
 *     1. stellar 和 quiz 的区别
 *        stellar 提供的是测试上下文（开发上下文，核心环境对接器）
 *        quiz 提供的是测试抽象类以及专用测试工具
 *     2. 翻译语义：Argo，南船座，原是最大的星座，十八世纪被拆成了四个星座的组合，星座内有800多颗星星。
 *        神话之一：南船座即阿格号（Argonauts），是希腊神话中的一支船队，由英雄们组成，他们乘坐阿格号
 *        （Argo）号船，前往科尔基斯寻找金羊毛。
 * </code></pre>
 * 当前类的主要作用在于从配置文件中加载 stellar 系统的专用配置信息，根据配置信息构造对应的环境
 * <pre><code>
 *     1. 开发环境
 *     2. 测试环境
 *     3. Inst指令集环境
 *     4. Shell专用辅助环境（和Shell配合协作）
 * </code></pre>
 * 此处环境路径为固定值，暂时先放到目录规划中
 *
 * @author lang : 2023-06-11
 */
public class ArgoStore {
    private static final JsonObject CONFIGURATION = new JsonObject();

    private static final JsonObject OPTIONS = new JsonObject();
    private static final JsonObject STELLAR = new JsonObject();

    private static final JSix HEX;

    static {
        final String envValue = Ut.env(HMacrocosm.ZERO_ENV);
        final Environment environment = Ut.toEnum(envValue, Environment.class, Environment.Production);
        final String vPath = Ut.ioPath(VWeb.runtime.CONFIGURATION_JSON, environment);
        Ke.LOG.Ok.info(ArgoStore.class, "Environment On = {0}, Path = {1}", environment, vPath);
        // 加载基础配置，目录规范中 running/configuration.json 路径
        final JsonObject configuration = Ut.ioJObject(vPath);
        CONFIGURATION.mergeIn(configuration, true);
        // 六维数据专用
        HEX = JSix.create(CONFIGURATION);
        // stellar 专用数据
        final String stellarIo = Ut.valueString(CONFIGURATION, VWeb.runtime.configuration.STELLAR);
        final JsonObject stellar = Ut.ioJObject(stellarIo);
        STELLAR.mergeIn(stellar, true);
        // options 专用数据
        final JsonObject options = Ut.valueJObject(CONFIGURATION, VWeb.runtime.configuration.OPTIONS);
        OPTIONS.mergeIn(options, true);
    }

    /**
     * 此处位于文件 running/configuration.json 中的结构是是一个字符串，该字符串描述了
     * stellar 系统的配置文件位置，然后才此位置中直接提取 stellar 的核心配置，构造的才
     * 是最终的核心配置。
     * <pre><code>
     *     {
     *         "stellar": "xxxx"
     *     }
     * </code></pre>
     *
     * @return {@link JsonObject}
     */
    public static JsonObject stellar() {
        return STELLAR;
    }

    /**
     * 提取 options 和 Service Config 中内容实现绑定，这两个配置文件的内容都是
     * <pre><code>
     *     {
     *         "options": {
     *
     *         }
     *     }
     * </code></pre>
     *
     * @return {@link JsonObject}
     */
    public static JsonObject options() {
        return OPTIONS.copy();
    }

    /**
     * 原始配置文件，直接读取 running/configuration.json 文件的内容
     *
     * @return {@link JsonObject}
     */
    public static JsonObject configuration() {
        return CONFIGURATION;
    }

    /**
     * 六维数据专用，核心维度数据结构，通常是 3 x 2 的维度结构
     * <pre><code>
     *     维度分两部分：
     *     - 维度1：{@link ChangeFlag}（增删改）
     *     - 维度2：batch（是否批量）
     *     格式如下：
     *      {
     *          "components": {
     *              "ADD.true": {},
     *              "ADD.false": {},
     *              "UPDATE.true": {},
     *              "UPDATE.false": {},
     *              "DELETE.true": {},
     *              "DELETE.false": {}
     *          }
     *      }
     *     上述属性含义：
     *     - ADD.true：批量记录添加
     *     - ADD.false：单记录添加
     *     - UPDATE.true：批量记录更新
     *     - UPDATE.false：单记录更新
     *     - DELETE.true：批量记录删除
     *     - DELETE.false：单记录删除
     * </code></pre>
     *
     * @return {@link JSix}
     */
    public static JSix six() {
        return HEX;
    }

    /**
     * 读取核心配置，使用双维度从核心配置中提取配置信息。
     *
     * @param type  {@link ChangeFlag}
     * @param batch {@link Boolean}
     *
     * @return {@link JsonObject}
     */
    public static JsonObject six(final ChangeFlag type, final Boolean batch) {
        return HEX.data(type, batch);
    }
}
