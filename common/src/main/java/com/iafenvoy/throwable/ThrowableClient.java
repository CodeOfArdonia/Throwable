package com.iafenvoy.throwable;

import com.iafenvoy.throwable.registry.ThrowableRenderers;

public final class ThrowableClient {
    public static void init() {
        ThrowableRenderers.registerEntityRenderers();
    }

    public static void process() {
    }
}
