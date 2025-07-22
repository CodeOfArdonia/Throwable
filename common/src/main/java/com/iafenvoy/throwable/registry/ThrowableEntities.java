package com.iafenvoy.throwable.registry;

import com.iafenvoy.throwable.Throwable;
import com.iafenvoy.throwable.object.ThrownWeaponEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKeys;

public final class ThrowableEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Throwable.MOD_ID, RegistryKeys.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<ThrownWeaponEntity>> THROWN_WEAPON = build("thrown_weapon", ThrownWeaponEntity::new, SpawnGroup.MISC, 64, 1, false, new EntityBuildHelper.Dimension(0.5F, 0.5F));

    private static <T extends Entity> RegistrySupplier<EntityType<T>> build(String name, EntityType.EntityFactory<T> constructor, SpawnGroup category, int trackingRange, int updateInterval, boolean fireImmune, EntityBuildHelper.Dimension dimension) {
        return REGISTRY.register(name, EntityBuildHelper.build(name, constructor, category, trackingRange, updateInterval, fireImmune, dimension));
    }
}
