/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;

public final class Instrument
extends Enum<Instrument>
implements StringIdentifiable {
    public static final /* enum */ Instrument HARP = new Instrument("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP, Type.BASE_BLOCK);
    public static final /* enum */ Instrument BASEDRUM = new Instrument("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, Type.BASE_BLOCK);
    public static final /* enum */ Instrument SNARE = new Instrument("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE, Type.BASE_BLOCK);
    public static final /* enum */ Instrument HAT = new Instrument("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT, Type.BASE_BLOCK);
    public static final /* enum */ Instrument BASS = new Instrument("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS, Type.BASE_BLOCK);
    public static final /* enum */ Instrument FLUTE = new Instrument("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, Type.BASE_BLOCK);
    public static final /* enum */ Instrument BELL = new Instrument("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL, Type.BASE_BLOCK);
    public static final /* enum */ Instrument GUITAR = new Instrument("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, Type.BASE_BLOCK);
    public static final /* enum */ Instrument CHIME = new Instrument("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME, Type.BASE_BLOCK);
    public static final /* enum */ Instrument XYLOPHONE = new Instrument("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ Instrument IRON_XYLOPHONE = new Instrument("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ Instrument COW_BELL = new Instrument("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, Type.BASE_BLOCK);
    public static final /* enum */ Instrument DIDGERIDOO = new Instrument("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, Type.BASE_BLOCK);
    public static final /* enum */ Instrument BIT = new Instrument("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT, Type.BASE_BLOCK);
    public static final /* enum */ Instrument BANJO = new Instrument("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO, Type.BASE_BLOCK);
    public static final /* enum */ Instrument PLING = new Instrument("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING, Type.BASE_BLOCK);
    public static final /* enum */ Instrument ZOMBIE = new Instrument("zombie", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE, Type.MOB_HEAD);
    public static final /* enum */ Instrument SKELETON = new Instrument("skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ Instrument CREEPER = new Instrument("creeper", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_CREEPER, Type.MOB_HEAD);
    public static final /* enum */ Instrument DRAGON = new Instrument("dragon", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Type.MOB_HEAD);
    public static final /* enum */ Instrument WITHER_SKELETON = new Instrument("wither_skeleton", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ Instrument PIGLIN = new Instrument("piglin", SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_PIGLIN, Type.MOB_HEAD);
    public static final /* enum */ Instrument CUSTOM_HEAD = new Instrument("custom_head", SoundEvents.UI_BUTTON_CLICK, Type.CUSTOM);
    private final String name;
    private final RegistryEntry<SoundEvent> sound;
    private final Type type;
    private static final /* synthetic */ Instrument[] field_12652;

    public static Instrument[] values() {
        return (Instrument[])field_12652.clone();
    }

    public static Instrument valueOf(String string) {
        return Enum.valueOf(Instrument.class, string);
    }

    private Instrument(String name, RegistryEntry<SoundEvent> sound, Type type) {
        this.name = name;
        this.sound = sound;
        this.type = type;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public boolean shouldSpawnNoteParticles() {
        return this.type == Type.BASE_BLOCK;
    }

    public boolean hasCustomSound() {
        return this.type == Type.CUSTOM;
    }

    public boolean shouldRequireAirAbove() {
        return this.type == Type.BASE_BLOCK;
    }

    public static Optional<Instrument> fromAboveState(BlockState state) {
        if (state.isOf(Blocks.ZOMBIE_HEAD)) {
            return Optional.of(ZOMBIE);
        }
        if (state.isOf(Blocks.SKELETON_SKULL)) {
            return Optional.of(SKELETON);
        }
        if (state.isOf(Blocks.CREEPER_HEAD)) {
            return Optional.of(CREEPER);
        }
        if (state.isOf(Blocks.DRAGON_HEAD)) {
            return Optional.of(DRAGON);
        }
        if (state.isOf(Blocks.WITHER_SKELETON_SKULL)) {
            return Optional.of(WITHER_SKELETON);
        }
        if (state.isOf(Blocks.PIGLIN_HEAD)) {
            return Optional.of(PIGLIN);
        }
        if (state.isOf(Blocks.PLAYER_HEAD)) {
            return Optional.of(CUSTOM_HEAD);
        }
        return Optional.empty();
    }

    public static Instrument fromBelowState(BlockState state) {
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
        return new Instrument[]{HARP, BASEDRUM, SNARE, HAT, BASS, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE, IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING, ZOMBIE, SKELETON, CREEPER, DRAGON, WITHER_SKELETON, PIGLIN, CUSTOM_HEAD};
    }

    static {
        field_12652 = Instrument.method_36730();
    }

    static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type BASE_BLOCK = new Type();
        public static final /* enum */ Type MOB_HEAD = new Type();
        public static final /* enum */ Type CUSTOM = new Type();
        private static final /* synthetic */ Type[] field_41609;

        public static Type[] values() {
            return (Type[])field_41609.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static /* synthetic */ Type[] method_47892() {
            return new Type[]{BASE_BLOCK, MOB_HEAD, CUSTOM};
        }

        static {
            field_41609 = Type.method_47892();
        }
    }
}

