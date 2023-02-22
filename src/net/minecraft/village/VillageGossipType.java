/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.village;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public final class VillageGossipType
extends Enum<VillageGossipType>
implements StringIdentifiable {
    public static final /* enum */ VillageGossipType MAJOR_NEGATIVE = new VillageGossipType("major_negative", -5, 100, 10, 10);
    public static final /* enum */ VillageGossipType MINOR_NEGATIVE = new VillageGossipType("minor_negative", -1, 200, 20, 20);
    public static final /* enum */ VillageGossipType MINOR_POSITIVE = new VillageGossipType("minor_positive", 1, 200, 1, 5);
    public static final /* enum */ VillageGossipType MAJOR_POSITIVE = new VillageGossipType("major_positive", 5, 100, 0, 100);
    public static final /* enum */ VillageGossipType TRADING = new VillageGossipType("trading", 1, 25, 2, 20);
    public static final int MAX_TRADING_REPUTATION = 25;
    public static final int TRADING_GOSSIP_SHARE_DECREMENT = 20;
    public static final int TRADING_GOSSIP_DECAY = 2;
    public final String key;
    public final int multiplier;
    public final int maxValue;
    public final int decay;
    public final int shareDecrement;
    public static final Codec<VillageGossipType> CODEC;
    private static final /* synthetic */ VillageGossipType[] field_18436;

    public static VillageGossipType[] values() {
        return (VillageGossipType[])field_18436.clone();
    }

    public static VillageGossipType valueOf(String string) {
        return Enum.valueOf(VillageGossipType.class, string);
    }

    private VillageGossipType(String key, int multiplier, int maxReputation, int decay, int shareDecrement) {
        this.key = key;
        this.multiplier = multiplier;
        this.maxValue = maxReputation;
        this.decay = decay;
        this.shareDecrement = shareDecrement;
    }

    @Override
    public String asString() {
        return this.key;
    }

    private static /* synthetic */ VillageGossipType[] method_36623() {
        return new VillageGossipType[]{MAJOR_NEGATIVE, MINOR_NEGATIVE, MINOR_POSITIVE, MAJOR_POSITIVE, TRADING};
    }

    static {
        field_18436 = VillageGossipType.method_36623();
        CODEC = StringIdentifiable.createCodec(VillageGossipType::values);
    }
}

