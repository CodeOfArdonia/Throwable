package com.iafenvoy.throwable.data;

import com.iafenvoy.throwable.Throwable;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public final class ThrowableRegistry {
    public static final RegistryKey<Registry<ThrowableData>> KEY = RegistryKey.ofRegistry(Identifier.of(Throwable.MOD_ID, Throwable.MOD_ID));
    @Nullable
    public static MinecraftServer SERVER = null;
    public static Supplier<DynamicRegistryManager> DYNAMIC_REGISTRY_GETTER = () -> SERVER == null ? null : SERVER.getRegistryManager();

    public static ThrowableData get(DynamicRegistryManager registries, Identifier id) {
        return registries.getOptional(KEY).map(x -> x.getOrEmpty(id)).flatMap(x -> x).orElse(ThrowableData.DEFAULT);
    }

    public static ThrowableData get(DynamicRegistryManager registries, Item item) {
        return get(registries, Registries.ITEM.getId(item));
    }
}
