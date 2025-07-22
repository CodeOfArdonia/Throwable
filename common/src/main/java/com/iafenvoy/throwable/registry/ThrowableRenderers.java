package com.iafenvoy.throwable.registry;

import com.iafenvoy.throwable.render.ThrownWeaponEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public final class ThrowableRenderers {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(ThrowableEntities.THROWN_WEAPON, ThrownWeaponEntityRenderer::new);
    }
}
