package com.iafenvoy.throwable.fabric;

import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import com.iafenvoy.throwable.render.ThrownWeaponEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;

public final class ThrowableFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ThrownWeaponEntity.TYPE.get(), ThrownWeaponEntityRenderer::new);
        ThrowableRegistry.DYNAMIC_REGISTRY_GETTER = () -> MinecraftClient.getInstance().world == null ? null : MinecraftClient.getInstance().world.getRegistryManager();
    }
}
