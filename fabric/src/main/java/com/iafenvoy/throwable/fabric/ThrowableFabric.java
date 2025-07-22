package com.iafenvoy.throwable.fabric;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public final class ThrowableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Throwable.init();
        DynamicRegistries.registerSynced(ThrowableRegistry.KEY, ThrowableData.CODEC, ThrowableData.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
    }
}
