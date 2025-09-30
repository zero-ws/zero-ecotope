package io.mature.extension.uca.console;

import io.mature.extension.refine.Ox;
import io.mature.extension.scaffold.console.AbstractInstruction;
import io.mature.stellar.vendor.OkB;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.common.program.KRef;
import io.zerows.core.constant.KName;
import io.zerows.extension.mbse.basement.atom.builtin.DataAtom;
import io.zerows.extension.mbse.basement.util.Ao;
import io.zerows.plugins.common.shell.atom.CommandInput;
import io.zerows.plugins.common.shell.eon.EmCommand;
import io.zerows.plugins.common.shell.refine.Sl;
import io.zerows.plugins.store.elasticsearch.ElasticSearchClient;
import io.zerows.plugins.store.elasticsearch.ElasticSearchInfix;
import io.zerows.specification.access.app.HApp;
import io.zerows.specification.access.app.HArk;
import io.zerows.specification.modeling.HRecord;
import io.zerows.specification.modeling.operation.HDao;
import io.zerows.unity.Ux;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class IndexInstruction extends AbstractInstruction {

    @Override
    public Future<EmCommand.TermStatus> executeAsync(final CommandInput args) {
        final String appName = this.inString(args, "a");
        return this.runEach(appName, identifier -> this.executeAsync(identifier, appName)).compose(done -> {
            Sl.output("全文检索索引全部创建完成，创建模型数量：{0}", done.size());
            return Ux.future(EmCommand.TermStatus.SUCCESS);
        });
    }

    private Future<Boolean> executeAsync(final String identifier, final String appName) {
        return this.partyA().compose(okA -> {
            final HArk ark = okA.configArk();
            final HApp app = ark.app();

            final OkB partyB = okA.partyB(appName);
            final DataAtom atom = Ao.toAtom(app.name(), identifier);
            if (Objects.isNull(atom)) {
                /*
                 * Atom Modification
                 */
                return Ux.future(Boolean.FALSE);
            } else {
                final HDao dao = Ox.toDao(app.option(KName.APP_ID), identifier);

                final ElasticSearchClient client = ElasticSearchInfix.getClient();
                final KRef recordRef = new KRef();
                /* 客户端 */
                return dao.fetchAllAsync()
                    /* 跳过索引创建流程 */
                    .compose(recordRef::future)

                    /* 翻译环节 */
                    .compose(nil -> partyB.fabric(atom))
                    .compose(fabric -> {

                        /* 引用提取 */
                        final HRecord[] records = recordRef.get();

                        /* 创建Es索引信息 */
                        Sl.output("准备创建索引：identifier = {0}, size = {1}",
                            identifier, String.valueOf(records.length));
                        final JsonArray documents = new JsonArray();
                        Arrays.stream(records).map(record -> fabric.inTo(record.toJson()).result())
                            .forEach(documents::add);
                        return Ux.future(client.createDocuments(identifier, documents));
                    });
            }
        });

    }
}
