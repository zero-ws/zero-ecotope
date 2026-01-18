package io.zerows.plugins.email;

import io.r2mo.base.web.ForTpl;
import io.r2mo.typed.json.JObject;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class EmailTpl implements ForTpl {

    private final TemplateEngine templateEngine;

    public EmailTpl() {
        // 1. 初始化模板解析器 (Resolver)
        // ClassLoaderTemplateResolver 用于从 classpath (即 src/main/resources) 加载文件
        final ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        // 2. 配置解析规则
        resolver.setPrefix("templates/");               // 前缀：去 classpath 下的 templates 目录找
        resolver.setSuffix(".html");                    // 后缀：补全文件名
        resolver.setTemplateMode(TemplateMode.HTML);    // 模式：HTML
        resolver.setCharacterEncoding("UTF-8");         // 编码
        resolver.setCacheable(true);                    // 缓存：生产环境建议开启，开发环境可设为 false

        // 3. 初始化引擎并挂载解析器
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    /**
     * 根据模板名称和参数渲染 Thymeleaf 模板，返回生成的 HTML 字符串。
     *
     * @param template 模板名称（相对于 src/main/resources/templates/ 目录，
     *                 不包括 .html 后缀。例如 'email-template' 对应
     *                 'src/main/resources/templates/email-template.html'）
     * @param params   包含模板变量的 Map (key-value pairs)
     * @return 渲染后的 HTML 内容字符串
     */
    @Override
    public String process(final String template, final JObject params) {
        // 1. 创建 Thymeleaf 上下文对象
        final Context context = new Context();

        // 2. 将参数 params 中的所有内容放到上下文中
        params.fieldNames().forEach(field -> {
            final Object value = params.get(field);
            context.setVariable(field, value);
        });

        // 3. 使用 TemplateEngine 处理模板并返回结果字符串
        return this.templateEngine.process(template, context);
    }
}
