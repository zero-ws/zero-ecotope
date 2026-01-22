package io.zerows.extension.crud.uca.input;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.metadata.WebRule;
import io.zerows.cosmic.plugins.validation.Rigor;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.crud.common.Ix;
import io.zerows.extension.crud.common.IxConstant;
import io.zerows.extension.crud.uca.IxMod;
import io.zerows.mbse.metadata.KModule;
import io.zerows.platform.constant.VString;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class PreCodex implements Pre {
    @Override
    public Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        /* 1.method, uri */
        final String key = this.getKey(data, in);

        log.info("{} ---> 验证规则 Key：{}", IxConstant.K_PREFIX, key);

        final ConcurrentMap<String, List<WebRule>> rules = Ix.getRules(key);
        if (!rules.isEmpty()) {
            /*
             * 2. Validate JsonObject
             */
            final Rigor rigor = Rigor.get(JsonObject.class);
            final WebException error = rigor.verify(rules, data);
            if (null != error) {
                log.info("{} ---> 错误代码：{}", IxConstant.K_PREFIX, error.getCode());
                return Future.failedFuture(error);
            }
        }
        return Ux.future(data);
    }

    private String getKey(final JsonObject data, final IxMod in) {
        final Envelop envelop = in.envelop();
        final KModule module = in.module();
        /* 1.method, uri */
        String uri = envelop.uri();
        if (uri.contains("?")) {
            uri = uri.split("\\?")[0];
        }
        final String method = envelop.method().name();
        /* 2.uri 中处理 key 相关的情况 */
        final String keyField = module.getField().getKey();
        final String keyValue = data.getString(keyField);
        if (Ut.isNotNil(keyValue)) {
            uri = uri.replace(keyValue, "$" + keyField);
        }
        /* 3.Final Rule */
        return uri.toLowerCase(Locale.getDefault()).replace('/', '.')
            .substring(1) + VString.DOT
            + method.toLowerCase(Locale.getDefault());
    }
}
