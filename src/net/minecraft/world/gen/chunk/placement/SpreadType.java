/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.gen.random.AbstractRandom;

public final class SpreadType
extends Enum<SpreadType>
implements StringIdentifiable {
    public static final /* enum */ SpreadType LINEAR = new SpreadType("linear");
    public static final /* enum */ SpreadType TRIANGULAR = new SpreadType("triangular");
    private static final SpreadType[] VALUES;
    public static final Codec<SpreadType> CODEC;
    private final String name;
    private static final /* synthetic */ SpreadType[] field_36426;

    public static SpreadType[] values() {
        return (SpreadType[])field_36426.clone();
    }

    public static SpreadType valueOf(String string) {
        return Enum.valueOf(SpreadType.class, string);
    }

    private SpreadType(String name) {
        this.name = name;
    }

    public static SpreadType byName(String name) {
        for (SpreadType spreadType : VALUES) {
            if (!spreadType.asString().equals(name)) continue;
            return spreadType;
        }
        throw new IllegalArgumentException("Unknown Random Spread type: " + name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    public int get(AbstractRandom random, int bound) {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case LINEAR -> random.nextInt(bound);
            case TRIANGULAR -> (random.nextInt(bound) + random.nextInt(bound)) / 2;
        };
    }

    private static /* synthetic */ SpreadType[] method_40175() {
        return new SpreadType[]{LINEAR, TRIANGULAR};
    }

    static {
        field_36426 = SpreadType.method_40175();
        VALUES = SpreadType.values();
        CODEC = StringIdentifiable.createCodec(() -> VALUES, SpreadType::byName);
    }
}

