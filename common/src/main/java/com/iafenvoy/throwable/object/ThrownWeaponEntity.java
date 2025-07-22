package com.iafenvoy.throwable.object;

import com.iafenvoy.throwable.registry.ThrowableEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ThrownWeaponEntity extends PersistentProjectileEntity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private boolean hitEntity;

    public ThrownWeaponEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ThrowableEntities.THROWN_WEAPON.get(), owner, world);
        this.pickupType = PickupPermission.DISALLOWED;
        this.setStack(stack);
    }

    public ThrownWeaponEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
        this.dataTracker.startTracking(SCALE, 1f);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setStack(ItemStack.fromNbt(nbt.getCompound("stack")));
        this.setScale(nbt.getFloat("scale"));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("stack", this.asItemStack().writeNbt(new NbtCompound()));
        nbt.putFloat("scale", this.getScale());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isEmpty()) {
            player.setStackInHand(hand, this.asItemStack());
            this.discard();
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    protected boolean canHit(Entity entity) {
        return !(entity instanceof ThrownWeaponEntity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.hitEntity) return;
        Entity entity = entityHitResult.getEntity();
        float f = (float) this.getVelocity().length();
        int i = MathHelper.ceil(MathHelper.clamp((double) f * this.getDamage(), 0.0F, Integer.MAX_VALUE));

        if (this.isCritical()) {
            long l = this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(l + (long) i, 2147483647L);
        }

        Entity entity2 = this.getOwner();
        DamageSource damageSource;
        if (entity2 == null)
            damageSource = this.getDamageSources().arrow(this, this);
        else {
            damageSource = this.getDamageSources().arrow(this, entity2);
            if (entity2 instanceof LivingEntity living) living.onAttacking(entity);
        }

        boolean bl = entity.getType() == EntityType.ENDERMAN;
        int j = entity.getFireTicks();
        if (this.isOnFire() && !bl) entity.setOnFireFor(5);
        if (entity.damage(damageSource, (float) i)) {
            if (bl) return;
            if (entity instanceof LivingEntity livingEntity) {
                if (this.getPunch() > 0) {
                    double d = Math.max(0.0F, (double) 1.0F - livingEntity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                    Vec3d vec3d = this.getVelocity().multiply(1.0F, 0.0F, 1.0F).normalize().multiply((double) this.getPunch() * 0.6 * d);
                    if (vec3d.lengthSquared() > (double) 0.0F)
                        livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
                }
                if (!this.getWorld().isClient && entity2 instanceof LivingEntity living) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged(living, livingEntity);
                }
                this.onHit(livingEntity);
                if (livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity serverPlayer && !this.isSilent())
                    serverPlayer.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
            }

            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0)
                this.hitEntity = true;
        } else {
            entity.setFireTicks(j);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0F);
            this.prevYaw += 180.0F;
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupPermission.ALLOWED)
                    this.dropStack(this.asItemStack(), 0.1F);
                this.hitEntity = true;
            }
        }
        this.setVelocity(0, 0, 0);
    }

    @Override
    protected Text getDefaultName() {
        return this.asItemStack().getName();
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public ItemStack asItemStack() {
        return this.dataTracker.get(STACK).copy();
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack);
        if (stack.getItem() instanceof SwordItem sword)
            this.setDamage(sword.getAttackDamage() / 2);
    }

    public float getScale() {
        return this.dataTracker.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataTracker.set(SCALE, scale);
    }
}
