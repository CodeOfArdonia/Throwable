package com.iafenvoy.throwable.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class EnchantmentUtil {
    public static int getEnchantmentLevel(DynamicRegistryManager registry, RegistryKey<Enchantment> key, ItemStack stack) {
        return registry.getOptional(RegistryKeys.ENCHANTMENT).map(x -> x.getEntry(key)).flatMap(x -> x).map(x -> EnchantmentHelper.getLevel(x, stack)).orElse(0);
    }
}
