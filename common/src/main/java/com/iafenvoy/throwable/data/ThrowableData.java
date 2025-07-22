package com.iafenvoy.throwable.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record ThrowableData(int maxUseTime, float rotateOffset, SoundEvent throwSound, SoundEvent hitGroundSound) {
    public static final Codec<ThrowableData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("maxUseTime", 72000).forGetter(ThrowableData::maxUseTime),
            Codec.FLOAT.optionalFieldOf("rotateOffset", 0f).forGetter(ThrowableData::rotateOffset),
            Registries.SOUND_EVENT.getCodec().optionalFieldOf("throwSound", SoundEvents.ITEM_TRIDENT_THROW).forGetter(ThrowableData::throwSound),
            Registries.SOUND_EVENT.getCodec().optionalFieldOf("hitGroundSound", SoundEvents.ITEM_TRIDENT_HIT_GROUND).forGetter(ThrowableData::hitGroundSound)
    ).apply(i, ThrowableData::new));
}
