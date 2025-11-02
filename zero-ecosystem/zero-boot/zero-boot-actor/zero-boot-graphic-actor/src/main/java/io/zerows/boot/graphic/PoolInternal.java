package io.zerows.boot.graphic;

import io.zerows.platform.enums.typed.ChangeFlag;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

interface PoolInternal {

    ConcurrentMap<ChangeFlag, ConcurrentHashMap<String, Pixel>> POOL_NODE = new ConcurrentHashMap<ChangeFlag, ConcurrentHashMap<String, Pixel>>() {
        {
            this.put(ChangeFlag.ADD, new ConcurrentHashMap<>());
            this.put(ChangeFlag.UPDATE, new ConcurrentHashMap<>());
            this.put(ChangeFlag.DELETE, new ConcurrentHashMap<>());
        }
    };

    ConcurrentMap<ChangeFlag, Function<String, Pixel>> POOL_NODE_SUPPLIER = new ConcurrentHashMap<ChangeFlag, Function<String, Pixel>>() {
        {
            this.put(ChangeFlag.ADD, PixelNodeAdd::new);
            this.put(ChangeFlag.UPDATE, PixelNodeUpdate::new);
            this.put(ChangeFlag.DELETE, PixelNodeDelete::new);
        }
    };
    ConcurrentMap<ChangeFlag, ConcurrentHashMap<String, Pixel>> POOL_EDGE = new ConcurrentHashMap<ChangeFlag, ConcurrentHashMap<String, Pixel>>() {
        {
            this.put(ChangeFlag.ADD, new ConcurrentHashMap<>());
            this.put(ChangeFlag.UPDATE, new ConcurrentHashMap<>());
            this.put(ChangeFlag.DELETE, new ConcurrentHashMap<>());
        }
    };
    ConcurrentMap<ChangeFlag, Function<String, Pixel>> POOL_EDGE_SUPPLIER = new ConcurrentHashMap<ChangeFlag, Function<String, Pixel>>() {
        {
            this.put(ChangeFlag.ADD, PixelEdgeAdd::new);
            this.put(ChangeFlag.UPDATE, PixelEdgeUpdate::new);
            this.put(ChangeFlag.DELETE, PixelEdgeDelete::new);
        }
    };
}
