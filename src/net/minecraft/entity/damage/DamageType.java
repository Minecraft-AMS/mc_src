/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.damage;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DeathMessageType;

public record DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {
    public static final Codec<DamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("message_id").forGetter(DamageType::msgId), (App)DamageScaling.CODEC.fieldOf("scaling").forGetter(DamageType::scaling), (App)Codec.FLOAT.fieldOf("exhaustion").forGetter(DamageType::exhaustion), (App)DamageEffects.CODEC.optionalFieldOf("effects", (Object)DamageEffects.HURT).forGetter(DamageType::effects), (App)DeathMessageType.CODEC.optionalFieldOf("death_message_type", (Object)DeathMessageType.DEFAULT).forGetter(DamageType::deathMessageType)).apply((Applicative)instance, DamageType::new));

    public DamageType(String msgId, DamageScaling scaling, float exhaustion) {
        this(msgId, scaling, exhaustion, DamageEffects.HURT, DeathMessageType.DEFAULT);
    }

    public DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects) {
        this(msgId, scaling, exhaustion, effects, DeathMessageType.DEFAULT);
    }

    public DamageType(String msgId, float exhaustion, DamageEffects effects) {
        this(msgId, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion, effects);
    }

    public DamageType(String msgId, float exhaustion) {
        this(msgId, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, exhaustion);
    }
}

