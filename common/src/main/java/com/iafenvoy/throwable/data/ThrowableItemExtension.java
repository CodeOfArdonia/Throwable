package com.iafenvoy.throwable.data;

import com.iafenvoy.throwable.Throwable;
import net.minecraft.item.Item;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ThrowableItemExtension {
    TagKey<Item> THROWABLE = TagKey.of(RegistryKeys.ITEM, Identifier.of(Throwable.MOD_ID, "throwable"));
    TagKey<Item> DISABLE_WHEN_OFFHAND = TagKey.of(RegistryKeys.ITEM, Identifier.of(Throwable.MOD_ID, "disable_when_offhand"));

    ThrowableData throwable$getData(DynamicRegistryManager registries);

    boolean throwable$canThrow();
}
