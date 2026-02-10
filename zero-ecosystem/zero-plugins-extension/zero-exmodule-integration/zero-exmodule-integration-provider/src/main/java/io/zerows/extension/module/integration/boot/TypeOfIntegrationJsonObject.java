package io.zerows.extension.module.integration.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.integration.domain.tables.IIntegration;
import io.zerows.extension.module.integration.domain.tables.IPortfolio;

import java.util.List;
import java.util.Map;

public class TypeOfIntegrationJsonObject extends TypeOfJsonObject {

    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // IIntegration
            Map.of(
                IIntegration.I_INTEGRATION.OPTIONS.getName(), IIntegration.I_INTEGRATION.getName()
            ),
            // IPortfolio
            Map.of(
                IPortfolio.I_PORTFOLIO.DATA_CONFIG.getName(), IPortfolio.I_PORTFOLIO.getName(),
                IPortfolio.I_PORTFOLIO.DATA_INTEGRATION.getName(), IPortfolio.I_PORTFOLIO.getName(),
                IPortfolio.I_PORTFOLIO.DATA_SECURE.getName(), IPortfolio.I_PORTFOLIO.getName(),
                IPortfolio.I_PORTFOLIO.RUN_CONFIG.getName(), IPortfolio.I_PORTFOLIO.getName()

            )
        );
    }
}
