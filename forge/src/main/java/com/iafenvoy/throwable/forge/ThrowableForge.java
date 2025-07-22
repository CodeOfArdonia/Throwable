package com.iafenvoy.throwable.forge;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(Throwable.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ThrowableForge {
    public ThrowableForge() {
        // Run our common setup.
        Throwable.init();
    }

    @SubscribeEvent
    public static void newDynamicRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ThrowableRegistry.KEY, ThrowableData.CODEC, ThrowableData.CODEC);
    }
}
