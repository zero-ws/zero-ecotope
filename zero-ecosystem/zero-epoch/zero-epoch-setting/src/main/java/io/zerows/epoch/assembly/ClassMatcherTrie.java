package io.zerows.epoch.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassMatcherTrie 🌲
 * <p>
 * 高性能“前缀黑名单”匹配器（字符级 Trie + 前置去冗余）：
 * 1) 🧹 去重：过滤 null / 空白 / 重复前缀；
 * 2) ✂️ 去冗余：若存在更短的前缀 a 覆盖了更长的前缀 a.b.c...，仅保留 a 以降低匹配成本；
 * 3) 🌲 Trie：逐字符下降；遇到终止节点（end=true）立刻命中；中途断链则未命中；
 * 4) 💡 线程安全：构建后为只读结构；matches 为无锁读。
 * <p>
 * 说明：
 * - 本类只关心“是否命中任一前缀”，不关心具体前缀内容；
 * - 与 String.startsWith 黑名单行为一致（任一前缀匹配即命中）。
 */
final class ClassMatcherTrie {

    /**
     * Trie 根节点（构建完成后只读）
     */
    private final Node root;
    /**
     * 去冗余后的前缀数量（仅用于统计/日志）
     */
    private final int size;

    private ClassMatcherTrie(final Node root, final int size) {
        this.root = root;
        this.size = size;
    }

    /**
     * 构建前缀匹配器（去重 + 去冗余 + Trie）
     */
    static ClassMatcherTrie compile(final String[] raw) {
        if (raw == null || raw.length == 0) {
            return new ClassMatcherTrie(new Node(), 0);
        }

        // 1) 去重：过滤空/空白，放入 Set
        final Set<String> uniq = new HashSet<>(raw.length * 2);
        for (String s : raw) {
            if (s != null) {
                s = s.trim();
                if (!s.isEmpty()) {
                    uniq.add(s);
                }
            }
        }

        // 2) 排序：长度升序 + 字典序（短前缀优先，便于覆盖判定）
        final List<String> list = new ArrayList<>(uniq);
        list.sort((a, b) -> {
            final int la = a.length();
            final int lb = b.length();
            return la == lb ? a.compareTo(b) : Integer.compare(la, lb);
        });

        // 3) 去冗余：若 p 以 kept 开头，则说明 kept 覆盖了 p，丢弃 p
        final List<String> dedup = new ArrayList<>(list.size());
        outer:
        for (final String p : list) {
            for (final String kept : dedup) {
                if (p.startsWith(kept)) {
                    continue outer; // 被更短前缀覆盖
                }
            }
            dedup.add(p);
        }

        // 4) 构建 Trie：字符级下降；途中遇 end=true 可短路
        final Node root = new Node();
        for (final String p : dedup) {
            Node cur = root;
            for (int i = 0; i < p.length(); i++) {
                final char c = p.charAt(i);
                Node nxt = cur.next.get(c);
                if (nxt == null) {
                    nxt = new Node();
                    cur.next.put(c, nxt);
                }
                cur = nxt;
                if (cur.end) {
                    break; // 更短前缀已终止，后续字符可忽略
                }
            }
            cur.end = true;
        }

        return new ClassMatcherTrie(root, dedup.size());
    }

    /**
     * 是否命中任一黑名单前缀（包名或类名均可传入）。
     * 算法：沿 Trie 逐字符下降；途中遇到 end=true 立即命中；中途断链则未命中；
     * 若字符串到尾仍在终止节点则命中。
     */
    boolean matches(final String pkgOrClass) {
        if (pkgOrClass == null || pkgOrClass.isEmpty()) {
            return true; // 空值保守命中（跳过）
        }
        Node cur = this.root;
        for (int i = 0; i < pkgOrClass.length(); i++) {
            if (cur.end) {
                return true;    // 命中更短前缀
            }
            final char c = pkgOrClass.charAt(i);
            cur = cur.next.get(c);
            if (cur == null) {
                return false; // 断链：未命中
            }
        }
        return cur.end; // 末尾是否落在终止节点
    }

    /**
     * 去冗余后前缀数量（用于统计/日志）
     */
    int size() {
        return this.size;
    }

    /**
     * Trie 节点：分支较少，用普通 HashMap 即可，避免过度同步开销
     */
    private static final class Node {
        final Map<Character, Node> next = new HashMap<>(4);
        boolean end;
    }
}
