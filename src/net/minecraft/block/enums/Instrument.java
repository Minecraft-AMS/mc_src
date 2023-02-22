/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.StringIdentifiable;

public final class Instrument
extends Enum<Instrument>
implements StringIdentifiable {
    public static final /* enum */ Instrument HARP = new Instrument("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP);
    public static final /* enum */ Instrument BASEDRUM = new Instrument("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM);
    public static final /* enum */ Instrument SNARE = new Instrument("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE);
    public static final /* enum */ Instrument HAT = new Instrument("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT);
    public static final /* enum */ Instrument BASS = new Instrument("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS);
    public static final /* enum */ Instrument FLUTE = new Instrument("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE);
    public static final /* enum */ Instrument BELL = new Instrument("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL);
    public static final /* enum */ Instrument GUITAR = new Instrument("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR);
    public static final /* enum */ Instrument CHIME = new Instrument("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME);
    public static final /* enum */ Instrument XYLOPHONE = new Instrument("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE);
    public static final /* enum */ Instrument IRON_XYLOPHONE = new Instrument("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE);
    public static final /* enum */ Instrument COW_BELL = new Instrument("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL);
    public static final /* enum */ Instrument DIDGERIDOO = new Instrument("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO);
    public static final /* enum */ Instrument BIT = new Instrument("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT);
    public static final /* enum */ Instrument BANJO = new Instrument("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO);
    public static final /* enum */ Instrument PLING = new Instrument("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING);
    private final String name;
    private final SoundEvent sound;
    private static final /* synthetic */ Instrument[] field_12652;

    public static Instrument[] values() {
        return (Instrument[])field_12652.clone();
    }

    public static Instrument valueOf(String string) {
        return Enum.valueOf(Instrument.class, string);
    }

    private Instrument(String name, SoundEvent sound) {
        this.name = name;
        this.sound = sound;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public SoundEvent getSound() {
        return this.sound;
    }

    public static Instrument fromBlockState(BlockState state) {
        if (state.isOf(Blocks.CLAY)) {
            return FLUTE;
        }
        if (state.isOf(Blocks.GOLD_BLOCK)) {
            return BELL;
        }
        if (state.isIn(BlockTags.WOOL)) {
            return GUITAR;
        }
        if (state.isOf(Blocks.PACKED_ICE)) {
            return CHIME;
        }
        if (state.isOf(Blocks.BONE_BLOCK)) {
            return XYLOPHONE;
        }
        if (state.isOf(Blocks.IRON_BLOCK)) {
            return IRON_XYLOPHONE;
        }
        if (state.isOf(Blocks.SOUL_SAND)) {
            return COW_BELL;
        }
        if (state.isOf(Blocks.PUMPKIN)) {
            return DIDGERIDOO;
        }
        if (state.isOf(Blocks.EMERALD_BLOCK)) {
            return BIT;
        }
        if (state.isOf(Blocks.HAY_BLOCK)) {
            return BANJO;
        }
        if (state.isOf(Blocks.GLOWSTONE)) {
            return PLING;
        }
        Material material = state.getMaterial();
        if (material == Material.STONE) {
            return BASEDRUM;
        }
        if (material == Material.AGGREGATE) {
            return SNARE;
        }
        if (material == Material.GLASS) {
            return HAT;
        }
        if (material == Material.WOOD || material == Material.NETHER_WOOD) {
            return BASS;
        }
        return HARP;
    }

    private static /* synthetic */ Instrument[] method_36730() {
        return new Instrument[]{HARP, BASEDRUM, SNARE, HAT, BASS, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE, IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING};
    }

    static {
        field_12652 = Instrument.method_36730();
    }
}

