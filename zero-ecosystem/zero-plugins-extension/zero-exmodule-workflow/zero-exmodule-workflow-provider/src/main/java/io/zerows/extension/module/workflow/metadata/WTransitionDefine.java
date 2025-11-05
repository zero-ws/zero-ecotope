package io.zerows.extension.module.workflow.metadata;

import io.zerows.epoch.metadata.KFlow;
import io.zerows.extension.module.workflow.component.camunda.Io;
import org.camunda.bpm.engine.repository.ProcessDefinition;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-05
 */
public class WTransitionDefine {
    private final ProcessDefinition definition;

    private final KFlow workflow;

    private final ConcurrentMap<String, WMove> move = new ConcurrentHashMap<>();

    public WTransitionDefine(final KFlow workflow, final ConcurrentMap<String, WMove> move) {
        this.workflow = workflow;
        // Io<Void> io when create the new Transaction
        final Io<Void> io = Io.io();
        /*
         * ProcessDefinition
         * ProcessInstance
         */
        this.definition = io.inProcess(workflow.definitionId());
        if (Objects.nonNull(move)) {
            this.move.clear();
            this.move.putAll(move);
        }
    }

    public ProcessDefinition definition() {
        return this.definition;
    }

    public KFlow workflow() {
        return this.workflow;
    }

    public WMove rule(final String node) {
        return this.move.getOrDefault(node, WMove.empty());
    }
}
