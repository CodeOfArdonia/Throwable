package com.iafenvoy.throwable.object;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ThrowableWeaponItem extends SwordItem {
    private final int maxUseTime, rotateOffset;
    private final Supplier<SoundEvent> throwSound, hitGroundSound;

    public ThrowableWeaponItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, ThrowSettings throwSettings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.maxUseTime = throwSettings.maxUseTime;
        this.rotateOffset = throwSettings.rotateOffset;
        this.throwSound = throwSettings.throwSound;
        this.hitGroundSound = throwSettings.hitGroundSound;
    }

    public int getRotateOffset() {
        return this.rotateOffset;
    }

    public SoundEvent getHitGroundSound() {
        return this.hitGroundSound.get();
    }

    public SoundEvent getThrowSound() {
        return this.throwSound.get();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return this.maxUseTime;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            if (i >= 10) {
                if (!world.isClient) {
                    stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(user.getActiveHand()));
                    ThrownWeaponEntity weapon = new ThrownWeaponEntity(world, playerEntity, stack);
                    weapon.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                    if (playerEntity.getAbilities().creativeMode)
                        weapon.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    else playerEntity.getInventory().removeOne(stack);
                    if (remainingUseTicks <= 0) weapon.setCritical(true);
                    weapon.setSound(this.getHitGroundSound());
                    world.spawnEntity(weapon);
                    world.playSoundFromEntity(null, weapon, this.getThrowSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1)
            return TypedActionResult.fail(itemStack);
        else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    public static class ThrowSettings {
        private int maxUseTime = 72000, rotateOffset = 0;
        private Supplier<SoundEvent> throwSound = () -> SoundEvents.ITEM_TRIDENT_THROW, hitGroundSound = () -> SoundEvents.ITEM_TRIDENT_HIT_GROUND;

        public ThrowSettings setMaxUseTime(int maxUseTime) {
            this.maxUseTime = maxUseTime;
            return this;
        }

        public ThrowSettings setRotateOffset(int rotateOffset) {
            this.rotateOffset = rotateOffset;
            return this;
        }

        public ThrowSettings setThrowSound(Supplier<SoundEvent> throwSound) {
            this.throwSound = throwSound;
            return this;
        }

        public ThrowSettings setHitGroundSound(Supplier<SoundEvent> hitGroundSound) {
            this.hitGroundSound = hitGroundSound;
            return this;
        }
    }
}
