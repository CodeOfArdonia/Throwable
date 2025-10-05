package com.iafenvoy.throwable.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record ThrowableData(int maxUseTime, double damageScale, float scale, float rotateOffset, SoundEvent throwSound,
                            SoundEvent hitGroundSound) {
    public static final ThrowableData DEFAULT = new ThrowableData(72000, 0.5, 1, 0, SoundEvents.ITEM_TRIDENT_THROW.value(), SoundEvents.ITEM_TRIDENT_HIT_GROUND);
    public static final Codec<ThrowableData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("maxUseTime", DEFAULT.maxUseTime).forGetter(ThrowableData::maxUseTime),
            Codec.DOUBLE.optionalFieldOf("damageScale", DEFAULT.damageScale).forGetter(ThrowableData::damageScale),
            Codec.FLOAT.optionalFieldOf("scale", DEFAULT.scale).forGetter(ThrowableData::scale),
            Codec.FLOAT.optionalFieldOf("rotateOffset", DEFAULT.rotateOffset).forGetter(ThrowableData::rotateOffset),
            Registries.SOUND_EVENT.getCodec().optionalFieldOf("throwSound", DEFAULT.throwSound).forGetter(ThrowableData::throwSound),
            Registries.SOUND_EVENT.getCodec().optionalFieldOf("hitGroundSound", DEFAULT.hitGroundSound).forGetter(ThrowableData::hitGroundSound)
    ).apply(i, ThrowableData::new));
}
