/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TropicalFishEntity
extends SchoolingFishEntity
implements VariantHolder<Variety> {
    public static final String BUCKET_VARIANT_TAG_KEY = "BucketVariantTag";
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final List<Variant> COMMON_VARIANTS = List.of(new Variant(Variety.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(Variety.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(Variety.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(Variety.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(Variety.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(Variety.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(Variety.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(Variety.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(Variety.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(Variety.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(Variety.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(Variety.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(Variety.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(Variety.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(Variety.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(Variety.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(Variety.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
    private boolean commonSpawn = true;

    public TropicalFishEntity(EntityType<? extends TropicalFishEntity> entityType, World world) {
        super((EntityType<? extends SchoolingFishEntity>)entityType, world);
    }

    public static String getToolTipForVariant(int variant) {
        return "entity.minecraft.tropical_fish.predefined." + variant;
    }

    static int getVariantId(Variety variety, DyeColor baseColor, DyeColor patternColor) {
        return variety.getId() & 0xFFFF | (baseColor.getId() & 0xFF) << 16 | (patternColor.getId() & 0xFF) << 24;
    }

    public static DyeColor getBaseDyeColor(int variant) {
        return DyeColor.byId(variant >> 16 & 0xFF);
    }

    public static DyeColor getPatternDyeColor(int variant) {
        return DyeColor.byId(variant >> 24 & 0xFF);
    }

    public static Variety getVariety(int variant) {
        return Variety.fromId(variant & 0xFFFF);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTropicalFishVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setTropicalFishVariant(nbt.getInt("Variant"));
    }

    private void setTropicalFishVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    @Override
    public boolean spawnsTooManyForEachTry(int count) {
        return !this.commonSpawn;
    }

    private int getTropicalFishVariant() {
        return this.dataTracker.get(VARIANT);
    }

    public DyeColor getBaseColorComponents() {
        return TropicalFishEntity.getBaseDyeColor(this.getTropicalFishVariant());
    }

    public DyeColor getPatternColorComponents() {
        return TropicalFishEntity.getPatternDyeColor(this.getTropicalFishVariant());
    }

    @Override
    public Variety getVariant() {
        return TropicalFishEntity.getVariety(this.getTropicalFishVariant());
    }

    @Override
    public void setVariant(Variety variety) {
        int i = this.getTropicalFishVariant();
        DyeColor dyeColor = TropicalFishEntity.getBaseDyeColor(i);
        DyeColor dyeColor2 = TropicalFishEntity.getPatternDyeColor(i);
        this.setTropicalFishVariant(TropicalFishEntity.getVariantId(variety, dyeColor, dyeColor2));
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        super.copyDataToStack(stack);
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt(BUCKET_VARIANT_TAG_KEY, this.getTropicalFishVariant());
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(Items.TROPICAL_FISH_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_TROPICAL_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        Variant variant;
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        if (spawnReason == SpawnReason.BUCKET && entityNbt != null && entityNbt.contains(BUCKET_VARIANT_TAG_KEY, 3)) {
            this.setTropicalFishVariant(entityNbt.getInt(BUCKET_VARIANT_TAG_KEY));
            return entityData;
        }
        Random random = world.getRandom();
        if (entityData instanceof TropicalFishData) {
            TropicalFishData tropicalFishData = (TropicalFishData)entityData;
            variant = tropicalFishData.variant;
        } else if ((double)random.nextFloat() < 0.9) {
            variant = Util.getRandom(COMMON_VARIANTS, random);
            entityData = new TropicalFishData(this, variant);
        } else {
            this.commonSpawn = false;
            Variety[] varietys = Variety.values();
            DyeColor[] dyeColors = DyeColor.values();
            Variety variety = Util.getRandom(varietys, random);
            DyeColor dyeColor = Util.getRandom(dyeColors, random);
            DyeColor dyeColor2 = Util.getRandom(dyeColors, random);
            variant = new Variant(variety, dyeColor, dyeColor2);
        }
        this.setTropicalFishVariant(variant.getId());
        return entityData;
    }

    public static boolean canTropicalFishSpawn(EntityType<TropicalFishEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER) && (world.getBiome(pos).isIn(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterCreatureEntity.canSpawn(type, world, reason, pos, random));
    }

    @Override
    public /* synthetic */ Object getVariant() {
        return this.getVariant();
    }

    public static final class Variety
    extends Enum<Variety>
    implements StringIdentifiable {
        public static final /* enum */ Variety KOB = new Variety("kob", Size.SMALL, 0);
        public static final /* enum */ Variety SUNSTREAK = new Variety("sunstreak", Size.SMALL, 1);
        public static final /* enum */ Variety SNOOPER = new Variety("snooper", Size.SMALL, 2);
        public static final /* enum */ Variety DASHER = new Variety("dasher", Size.SMALL, 3);
        public static final /* enum */ Variety BRINELY = new Variety("brinely", Size.SMALL, 4);
        public static final /* enum */ Variety SPOTTY = new Variety("spotty", Size.SMALL, 5);
        public static final /* enum */ Variety FLOPPER = new Variety("flopper", Size.LARGE, 0);
        public static final /* enum */ Variety STRIPEY = new Variety("stripey", Size.LARGE, 1);
        public static final /* enum */ Variety GLITTER = new Variety("glitter", Size.LARGE, 2);
        public static final /* enum */ Variety BLOCKFISH = new Variety("blockfish", Size.LARGE, 3);
        public static final /* enum */ Variety BETTY = new Variety("betty", Size.LARGE, 4);
        public static final /* enum */ Variety CLAYFISH = new Variety("clayfish", Size.LARGE, 5);
        public static final Codec<Variety> CODEC;
        private static final IntFunction<Variety> BY_ID;
        private final String name;
        private final Text text;
        private final Size size;
        private final int id;
        private static final /* synthetic */ Variety[] field_6886;

        public static Variety[] values() {
            return (Variety[])field_6886.clone();
        }

        public static Variety valueOf(String string) {
            return Enum.valueOf(Variety.class, string);
        }

        private Variety(String name, Size size, int id) {
            this.name = name;
            this.size = size;
            this.id = size.id | id << 8;
            this.text = Text.translatable("entity.minecraft.tropical_fish.type." + this.name);
        }

        public static Variety fromId(int id) {
            return BY_ID.apply(id);
        }

        public Size getSize() {
            return this.size;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public Text getText() {
            return this.text;
        }

        private static /* synthetic */ Variety[] method_36643() {
            return new Variety[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
        }

        static {
            field_6886 = Variety.method_36643();
            CODEC = StringIdentifiable.createCodec(Variety::values);
            BY_ID = ValueLists.createIdToValueFunction(Variety::getId, Variety.values(), KOB);
        }
    }

    static class TropicalFishData
    extends SchoolingFishEntity.FishData {
        final Variant variant;

        TropicalFishData(TropicalFishEntity leader, Variant variant) {
            super(leader);
            this.variant = variant;
        }
    }

    public record Variant(Variety variety, DyeColor baseColor, DyeColor patternColor) {
        public int getId() {
            return TropicalFishEntity.getVariantId(this.variety, this.baseColor, this.patternColor);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Variant.class, "pattern;baseColor;patternColor", "variety", "baseColor", "patternColor"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Variant.class, "pattern;baseColor;patternColor", "variety", "baseColor", "patternColor"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Variant.class, "pattern;baseColor;patternColor", "variety", "baseColor", "patternColor"}, this, object);
        }
    }

    public static final class Size
    extends Enum<Size> {
        public static final /* enum */ Size SMALL = new Size(0);
        public static final /* enum */ Size LARGE = new Size(1);
        final int id;
        private static final /* synthetic */ Size[] field_41577;

        public static Size[] values() {
            return (Size[])field_41577.clone();
        }

        public static Size valueOf(String string) {
            return Enum.valueOf(Size.class, string);
        }

        private Size(int id) {
            this.id = id;
        }

        private static /* synthetic */ Size[] method_47866() {
            return new Size[]{SMALL, LARGE};
        }

        static {
            field_41577 = Size.method_47866();
        }
    }
}

