package com.iafenvoy.throwable.render;

import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableItemExtension;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ThrownWeaponEntityRenderer extends EntityRenderer<ThrownWeaponEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownWeaponEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ThrownWeaponEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        assert MinecraftClient.getInstance().world != null;
        DynamicRegistryManager registries = MinecraftClient.getInstance().world.getRegistryManager();
        ItemStack stack = entity.asItemStack();
        ThrowableData data = stack.getItem() instanceof ThrowableItemExtension item ? item.throwable$getData(registries) : null;
        matrices.push();
        if (data != null) {
            float scale = data.scale();
            matrices.scale(scale, scale, scale);
        }
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch()) + (data != null ? data.rotateOffset() : 0) - 135));
        matrices.translate(-0.125, 0, 0);
        this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getEntityWorld(), 0);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(ThrownWeaponEntity entity) {
        return Identifier.of("", "");
    }

    public static void applyThrowTransform(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.translate(0, 0.25, 0);
    }
}
