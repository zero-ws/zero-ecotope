#!/usr/bin/env python3
"""Audit MXT owner-document coverage and retrieval hygiene.

This script is intentionally local and dependency-free so agents can run it
after framework module changes or MCP documentation edits.
"""

from __future__ import annotations

from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parents[2]
MXT = ROOT / "mxt"


TASK_OWNER_DOCS = [
    "zero-version-guide.md",
    "zero-epoch-runtime-guide.md",
    "spi-core-plugin-guide.md",
    "config-center-local-nacos.md",
    "dbe-query-rules.md",
    "dbs-multi-datasource.md",
    "zero-overlay-bridge.md",
    "abstraction-rules.md",
    "job-model-guide.md",
    "actor-startup-matrix.md",
    "cache-redis-guide.md",
    "excel-import-export-guide.md",
    "flyway-loading-flow.md",
    "monitor-center-guide.md",
    "security-plugin-flow.md",
    "neo4j-guide.md",
    "email-capability-guide.md",
    "sms-capability-guide.md",
    "weco-capability-guide.md",
    "websocket-guide.md",
    "crud-engine-guide.md",
    "extension-skeleton-guide.md",
    "extension-api-guide.md",
    "exmodule-ambient-guide.md",
    "exmodule-erp-guide.md",
    "exmodule-finance-guide.md",
    "exmodule-graphic-guide.md",
    "exmodule-integration-guide.md",
    "exmodule-lbs-guide.md",
    "exmodule-mbseapi-guide.md",
    "exmodule-mbsecore-guide.md",
    "exmodule-modulat-guide.md",
    "exmodule-rbac-guide.md",
    "exmodule-report-guide.md",
    "exmodule-tpl-guide.md",
    "exmodule-ui-guide.md",
    "exmodule-workflow-guide.md",
    "attachment-storage-integration-guide.md",
    "static-modeling-guide.md",
    "activity-log-guide.md",
    "report-center-guide.md",
    "acl-authorization-guide.md",
    "modulat-ui-unified-guide.md",
]


GRAPH_GAP_OWNER_DOCS = [
    "zero-boot-wiring-guide.md",
    "swagger-openapi-guide.md",
    "elasticsearch-guide.md",
    "session-guide.md",
    "oauth2-capability-guide.md",
    "trash-capability-guide.md",
]


GOVERNANCE_DOCS = [
    "mcp-integration-map.md",
    "mcp-fast-retrieval-rules.md",
    "distillation-rules.md",
    "purification-rules.md",
    "graph-usage-rules.md",
    "graph-noise-rules.md",
    "mcp-code-review-graph-rules.md",
]


REQUIRED_PHRASES = {
    "mcp-fast-retrieval-rules.md": [
        "Route once, query once, open only the owner and proof files.",
        "Graph expansion is the last step, not the entry point.",
    ],
    "distillation-rules.md": [
        "Keep the decision path, the owner, and the proof anchor; remove everything else.",
    ],
    "purification-rules.md": [
        "One rule, one owner document, one shortest route.",
    ],
    "graph-noise-rules.md": [
        "Filter graph output by framework ownership before trusting rank or community size.",
        "Graph rank is not ownership. Path ownership is stronger than score.",
    ],
}


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def fail(message: str, failures: list[str]) -> None:
    failures.append(message)


def check_exists(doc: str, failures: list[str]) -> None:
    if not (MXT / doc).exists():
        fail(f"missing owner doc: {doc}", failures)


def check_internal_links(failures: list[str]) -> None:
    ignored = {"CLAUDE.md", "AGENTS.md", "CODEX.md"}
    pattern = re.compile(r"\[[^\]]+\]\(([^)]+\.md)\)|`([^`/]+\.md)`")
    for md in MXT.glob("*.md"):
        for match in pattern.finditer(read(md)):
            target = match.group(1) or match.group(2)
            if target in ignored or "/" in target:
                continue
            if not (MXT / target).exists():
                fail(f"{md.name} references missing {target}", failures)


def check_readme_and_route(doc: str, failures: list[str]) -> None:
    readme = read(MXT / "README.md")
    route = read(MXT / "mcp-integration-map.md")
    audit = read(MXT / "document-boundary-audit.md")
    if doc not in readme:
        fail(f"README missing {doc}", failures)
    if doc not in route and doc not in GOVERNANCE_DOCS:
        fail(f"mcp-integration-map missing {doc}", failures)
    if f"`{doc}`" not in audit:
        fail(f"document-boundary-audit missing {doc}", failures)


def check_english(doc: str, failures: list[str]) -> None:
    if re.search(r"[\u4e00-\u9fff]", read(MXT / doc)):
        fail(f"non-English content in {doc}", failures)


def check_required_phrases(failures: list[str]) -> None:
    for doc, phrases in REQUIRED_PHRASES.items():
        text = read(MXT / doc)
        for phrase in phrases:
            if phrase not in text:
                fail(f"{doc} missing phrase: {phrase}", failures)


def main() -> int:
    failures: list[str] = []
    docs = TASK_OWNER_DOCS + GRAPH_GAP_OWNER_DOCS + GOVERNANCE_DOCS

    for doc in docs:
        check_exists(doc, failures)
        if (MXT / doc).exists():
            check_english(doc, failures)
            check_readme_and_route(doc, failures)

    check_internal_links(failures)
    check_required_phrases(failures)

    if failures:
        for item in failures:
            print(f"FAIL: {item}")
        print(f"MXT coverage audit failed: {len(failures)} issue(s)")
        return 1

    print(
        "MXT coverage audit passed: "
        f"{len(TASK_OWNER_DOCS)} task owners, "
        f"{len(GRAPH_GAP_OWNER_DOCS)} graph-gap owners, "
        f"{len(GOVERNANCE_DOCS)} governance docs"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
