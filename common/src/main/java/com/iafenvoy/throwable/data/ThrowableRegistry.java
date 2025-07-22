package com.iafenvoy.throwable.data;

import com.iafenvoy.throwable.Throwable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ThrowableRegistry {
    public static final RegistryKey<Registry<ThrowableData>> KEY = RegistryKey.ofRegistry(Identifier.of(Throwable.MOD_ID, Throwable.MOD_ID));
}
