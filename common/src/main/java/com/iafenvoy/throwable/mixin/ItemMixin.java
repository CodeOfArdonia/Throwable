package com.iafenvoy.throwable.mixin;

import com.iafenvoy.throwable.data.ThrowableData;
import com.iafenvoy.throwable.data.ThrowableItemExtension;
import com.iafenvoy.throwable.data.ThrowableRegistry;
import com.iafenvoy.throwable.entity.ThrownWeaponEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements ThrowableItemExtension {
    @Shadow
    public abstract RegistryEntry.Reference<Item> getRegistryEntry();

    @Override
    public ThrowableData throwable$getData(DynamicRegistryManager registries) {
        return ThrowableRegistry.get(registries, (Item) (Object) this);
    }

    @Override
    public boolean throwable$canThrow() {
        return this.getRegistryEntry().isIn(THROWABLE);
    }

    @Inject(method = "getUseAction", at = @At("HEAD"), cancellable = true)
    private void handleUseAction(ItemStack stack, CallbackInfoReturnable<UseAction> cir) {
        if (this.throwable$canThrow()) cir.setReturnValue(UseAction.SPEAR);
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void handleMaxUseTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        DynamicRegistryManager manager = ThrowableRegistry.DYNAMIC_REGISTRY_GETTER.get();
        if (this.throwable$canThrow() && manager != null)
            cir.setReturnValue(this.throwable$getData(manager).maxUseTime());
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
    private void handleStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (this.throwable$canThrow() && user instanceof PlayerEntity player) {
            ThrowableData data = this.throwable$getData(world.getRegistryManager());
            int i = data.maxUseTime() - remainingUseTicks;
            if (i >= 10) {
                if (!world.isClient) {
                    stack.damage(1, player, LivingEntity.getSlotForHand(user.getActiveHand()));
                    ThrownWeaponEntity weapon = new ThrownWeaponEntity(world, player, stack);
                    weapon.setVelocity(player, player.getPitch(), player.getYaw(), 0, 2.5F, 1);
                    if (player.getAbilities().creativeMode)
                        weapon.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    else {
                        weapon.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        player.getInventory().removeOne(stack);
                    }
                    if (remainingUseTicks <= 0) weapon.setCritical(true);
                    weapon.setSound(data.hitGroundSound());
                    weapon.setDamage(data.damageScale());
                    world.spawnEntity(weapon);
                    world.playSoundFromEntity(null, weapon, data.throwSound(), SoundCategory.PLAYERS, 1, 1);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void handleUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (this.throwable$canThrow()) {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.getDamage() >= stack.getMaxDamage() - 1)
                cir.setReturnValue(TypedActionResult.fail(stack));
            else {
                user.setCurrentHand(hand);
                cir.setReturnValue(TypedActionResult.consume(stack));
            }
        }
    }
}
