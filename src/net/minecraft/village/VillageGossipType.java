/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public final class VillageGossipType
extends Enum<VillageGossipType> {
    public static final /* enum */ VillageGossipType MAJOR_NEGATIVE = new VillageGossipType("major_negative", -5, 100, 10, 10);
    public static final /* enum */ VillageGossipType MINOR_NEGATIVE = new VillageGossipType("minor_negative", -1, 200, 20, 20);
    public static final /* enum */ VillageGossipType MINOR_POSITIVE = new VillageGossipType("minor_positive", 1, 200, 1, 5);
    public static final /* enum */ VillageGossipType MAJOR_POSITIVE = new VillageGossipType("major_positive", 5, 100, 0, 100);
    public static final /* enum */ VillageGossipType TRADING = new VillageGossipType("trading", 1, 25, 2, 20);
    public static final int field_30240 = 25;
    public static final int field_30241 = 20;
    public static final int field_30242 = 2;
    public final String key;
    public final int multiplier;
    public final int maxValue;
    public final int decay;
    public final int shareDecrement;
    private static final Map<String, VillageGossipType> BY_KEY;
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

    @Nullable
    public static VillageGossipType byKey(String key) {
        return BY_KEY.get(key);
    }

    private static /* synthetic */ VillageGossipType[] method_36623() {
        return new VillageGossipType[]{MAJOR_NEGATIVE, MINOR_NEGATIVE, MINOR_POSITIVE, MAJOR_POSITIVE, TRADING};
    }

    static {
        field_18436 = VillageGossipType.method_36623();
        BY_KEY = (Map)Stream.of(VillageGossipType.values()).collect(ImmutableMap.toImmutableMap(villageGossipType -> villageGossipType.key, Function.identity()));
    }
}

