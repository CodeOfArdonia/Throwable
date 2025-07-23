package com.iafenvoy.throwable.forge;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;

@Mod(Throwable.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ThrowableForge {
    @SuppressWarnings("removal")
    public ThrowableForge() {
        DeferredRegister<EntityType<?>> registry = DeferredRegister.create(RegistryKeys.ENTITY_TYPE, Throwable.MOD_ID);
        registry.register(ThrownWeaponEntity.ID, ThrownWeaponEntity.TYPE);
        registry.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void newDynamicRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ThrowableRegistry.KEY, ThrowableData.CODEC, ThrowableData.CODEC);
    }
}
