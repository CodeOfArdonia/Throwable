package com.iafenvoy.throwable.forge;

import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import com.iafenvoy.throwable.render.ThrownWeaponEntityRenderer;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ThrowableForgeClient {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ThrownWeaponEntity.TYPE.get(), ThrownWeaponEntityRenderer::new);
        ThrowableRegistry.DYNAMIC_REGISTRY_GETTER = () -> MinecraftClient.getInstance().world == null ? null : MinecraftClient.getInstance().world.getRegistryManager();
    }
}
