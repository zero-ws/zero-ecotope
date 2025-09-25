package io.mature.stellar.owner;

import io.zerows.ams.constant.em.Environment;
import io.zerows.ams.constant.spec.VWeb;
import io.mature.stellar.Party;
import io.mature.stellar.vendor.OkB;
import io.zerows.common.app.KGlobal;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface OkA extends Party {
    /**
     * 当前甲方环境已经初始化完成
     *
     * @return 是否初始化完成
     */
    default boolean initialized() {
        return false;
    }

    /**
     * 对应文件目录 {@link VWeb.runtime#CONFIGURATION_JSON}
     *
     * @return {@link KGlobal}
     */
    KGlobal partyA();

    /**
     * 和当前甲方环境相关的所有乙方环境加载
     *
     * @param name 乙方环境名称
     *
     * @return {@link OkB}
     */
    OkB partyB(String name);

    /**
     * 返回当前甲方环境信息
     *
     * @return {@link Environment}
     */
    Environment environment();
}
