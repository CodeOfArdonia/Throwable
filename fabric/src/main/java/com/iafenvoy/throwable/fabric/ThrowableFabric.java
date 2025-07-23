package com.iafenvoy.throwable.fabric;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ThrowableFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registries.ENTITY_TYPE, Identifier.of(Throwable.MOD_ID, ThrownWeaponEntity.ID), ThrownWeaponEntity.TYPE.get());
        DynamicRegistries.registerSynced(ThrowableRegistry.KEY, ThrowableData.CODEC, ThrowableData.CODEC, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
    }
}
