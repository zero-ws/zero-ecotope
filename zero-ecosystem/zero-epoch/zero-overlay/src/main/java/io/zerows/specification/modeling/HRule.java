package io.zerows.specification.modeling;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KRule;
import io.zerows.support.base.UtBase;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 规则接口，对应系统中的标识规则部分
 *
 * @author lang : 2023-05-09
 */
public interface HRule extends Serializable {
//
//    static HRule of(final String content) {
//        return UtBase.deserialize(content, KRule.class);
//    }

    static HRule of(final JsonObject content) {
        return UtBase.deserialize(content, KRule.class);
    }

    /**
     * 判断当前标识规则是否有效（合法）
     *
     * @return true 有效，false 无效
     */
    boolean valid();

    /**
     * 使用模型标识符提取该模型下的子标识规则
     *
     * @param identifier 模型标识符
     * @return 子标识规则
     */
    HRule child(String identifier);
    // ------------------ 标识规则集 --------------------

    /**
     * 提取集成规则，集成规则无优先级
     *
     * @return 集成规则集
     */
    Set<KRule.Item> rulePull();

    /**
     * 提取带有优先级的标识规则（标准规则）
     *
     * @return 优先级规则集
     */
    List<KRule.Item> rulePure();

    /**
     * 提取推送规则、落库规则
     *
     * @return 推送规则集
     */
    List<KRule.Item> rulePush();

    /**
     * （标识规则更新）提取内部强连接规则
     *
     * @return 强连接规则集
     */
    Set<KRule.Item> ruleStrong();

    /**
     * （标识规则更新）提取内部弱连接规则
     *
     * @return 强连接规则集
     */
    Set<KRule.Item> ruleWeak();

    // ------------------ 其他约束 --------------------
}
