package com.iafenvoy.throwable.entity;

import com.google.common.base.Suppliers;
import com.iafenvoy.throwable.config.ThrowableConfig;
import com.iafenvoy.throwable.mixin.PersistentProjectileEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ThrownWeaponEntity extends PersistentProjectileEntity {
    public static final String ID = "thrown_weapon";
    public static final Supplier<EntityType<ThrownWeaponEntity>> TYPE = Suppliers.memoize(() -> EntityType.Builder.<ThrownWeaponEntity>create(ThrownWeaponEntity::new, SpawnGroup.MISC).maxTrackingRange(64).trackingTickInterval(1).setDimensions(0.5F, 0.5F).build(ID));

    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Float> SCALE = DataTracker.registerData(ThrownWeaponEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private boolean hitEntity;

    public ThrownWeaponEntity(World world, LivingEntity owner, ItemStack stack) {
        super(TYPE.get(), owner, world);
        this.setStack(stack);
    }

    public ThrownWeaponEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
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
    protected void age() {
        int despawnTime = ThrowableConfig.INSTANCE.despawnDelayTicks;
        if (despawnTime <= -1) return;
        PersistentProjectileEntityAccessor accessor = (PersistentProjectileEntityAccessor) this;
        int life = accessor.getLife();
        if (this.pickupType == PickupPermission.CREATIVE_ONLY) {
            if (life >= 1200) this.discard();
        } else if (life >= despawnTime) {
            if (ThrowableConfig.INSTANCE.dropWhenDespawn)
                this.getWorld().spawnEntity(new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), this.asItemStack()));
            this.discard();
        }
        if (!this.isRemoved()) accessor.setLife(life + 1);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.pickupType == PickupPermission.DISALLOWED && (!ThrowableConfig.INSTANCE.ownerPickUpOnly || this.getOwner() == player) && player.getStackInHand(hand).isEmpty()) {
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
        int level = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, this.asItemStack());
        if ((this.isOnFire() || level > 0) && !bl) entity.setOnFireFor((this.isOnFire() ? 5 : 0) + level * 5);
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
    protected void onBlockHit(BlockHitResult blockHitResult) {
        SoundEvent soundEvent = this.getSound();
        super.onBlockHit(blockHitResult);
        this.setSound(soundEvent);
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
    public void setDamage(double scale) {
        double damage = 1;
        Item item = this.asItemStack().getItem();
        if (item instanceof SwordItem sword) damage = sword.getAttackDamage();
        if (item instanceof MiningToolItem tool) damage = tool.getAttackDamage();
        super.setDamage(damage * scale);
    }

    @Override
    public ItemStack asItemStack() {
        return this.dataTracker.get(STACK).copy();
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack);
    }

    public float getScale() {
        return this.dataTracker.get(SCALE);
    }

    public void setScale(float scale) {
        this.dataTracker.set(SCALE, scale);
    }
}
