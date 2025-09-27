package io.zerows.plugins.office.excel.uca.ranger;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.office.excel.atom.ExRecord;

/**
 * 新版语法解析器，针对 ExRecord 执行二次遍历，做最终数据的处理流程，如果最终数据处理流程中包含了语法解析，则执行语法解析相关的替换操作
 * <pre><code>
 *     语法格式：
 *     JSON:????            加载 JSON 文件
 *     {UUID}               自动生成 UUID
 *     CODE:class           根据 CODE 加载 {CURRENT}/{code}/{field}.json -> [value]  -> String Java 类名
 *     NAME:config          根据 NAME 加载 {CURRENT}/{name}/{field}.json
 *     CODE:config          根据 CODE 加载 {CURRENT}/{code}/{field}.json -> {} -> InJson 格式的配置数据
 *     CODE:NAME:config     根据 CODE 加载 {CURRENT}/{code}/{name}/{field}.json
 *     PWD                  当前目录       {CURRENT}/{field}.json
 *     外联文件加载
 *     FILE:????            加载文件中的数据
 *          --> {
 *              "__type__": "FILE",
 *              "__content__": {
 *                  "path": "????"
 *              }
 *          }
 *     PAGE:????
 *          --> {
 *              "__type__": "PAGE",
 *              "__context__": {
 *                  "path": "????"
 *              }
 *          }
 * </code></pre>
 * 所以设置值的时候绕开上述的语法保留字，以防止冲突
 *
 * @author lang : 2024-06-13
 */
public interface ExExpr {

    Cc<String, ExExpr> CCT_EXPR = Cc.openThread();

    static ExExpr of() {
        return CCT_EXPR.pick(ExExprRecord::new, ExExprRecord.class.getName());
    }

    JsonObject parse(ExRecord record);
}
