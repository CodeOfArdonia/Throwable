package com.iafenvoy.throwable.registry;

import com.iafenvoy.throwable.Throwable;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ThrowableTags {
    public static final TagKey<Item> THROWABLE = TagKey.of(RegistryKeys.ITEM, Identifier.of(Throwable.MOD_ID, Throwable.MOD_ID));
}
