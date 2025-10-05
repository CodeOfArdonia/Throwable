package com.iafenvoy.throwable.forge;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(Throwable.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class ThrowableForge {
    public ThrowableForge(IEventBus bus, ModContainer container) {
        DeferredRegister<EntityType<?>> registry = DeferredRegister.create(RegistryKeys.ENTITY_TYPE, Throwable.MOD_ID);
        registry.register(ThrownWeaponEntity.ID, ThrownWeaponEntity.TYPE);
        registry.register(bus);
    }

    @SubscribeEvent
    public static void newDynamicRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ThrowableRegistry.KEY, ThrowableData.CODEC, ThrowableData.CODEC);
    }
}
