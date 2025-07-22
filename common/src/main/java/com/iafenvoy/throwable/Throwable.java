package com.iafenvoy.throwable;

import com.iafenvoy.throwable.registry.ThrowableEntities;
import com.mojang.logging.LogUtils;
import net.minecraft.registry.DynamicRegistryManager;
import org.slf4j.Logger;

public final class Throwable {
    public static final String MOD_ID = "throwable";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        ThrowableEntities.REGISTRY.register();
    }

    public static void process() {

    }
}
