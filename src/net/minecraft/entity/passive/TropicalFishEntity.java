/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.Locale;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BiomeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TropicalFishEntity
extends SchoolingFishEntity {
    public static final String BUCKET_VARIANT_TAG_KEY = "BucketVariantTag";
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TropicalFishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final int field_30380 = 0;
    public static final int field_30383 = 1;
    private static final int field_30379 = 2;
    private static final Identifier[] SHAPE_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_a.png"), new Identifier("textures/entity/fish/tropical_b.png")};
    private static final Identifier[] SMALL_FISH_VARIETY_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_a_pattern_1.png"), new Identifier("textures/entity/fish/tropical_a_pattern_2.png"), new Identifier("textures/entity/fish/tropical_a_pattern_3.png"), new Identifier("textures/entity/fish/tropical_a_pattern_4.png"), new Identifier("textures/entity/fish/tropical_a_pattern_5.png"), new Identifier("textures/entity/fish/tropical_a_pattern_6.png")};
    private static final Identifier[] LARGE_FISH_VARIETY_IDS = new Identifier[]{new Identifier("textures/entity/fish/tropical_b_pattern_1.png"), new Identifier("textures/entity/fish/tropical_b_pattern_2.png"), new Identifier("textures/entity/fish/tropical_b_pattern_3.png"), new Identifier("textures/entity/fish/tropical_b_pattern_4.png"), new Identifier("textures/entity/fish/tropical_b_pattern_5.png"), new Identifier("textures/entity/fish/tropical_b_pattern_6.png")};
    private static final int field_30381 = 6;
    private static final int field_30382 = 15;
    public static final int[] COMMON_VARIANTS = new int[]{TropicalFishEntity.toVariant(Variety.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.KOB, DyeColor.ORANGE, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), TropicalFishEntity.toVariant(Variety.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.RED), TropicalFishEntity.toVariant(Variety.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.GLITTER, DyeColor.WHITE, DyeColor.GRAY), TropicalFishEntity.toVariant(Variety.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), TropicalFishEntity.toVariant(Variety.DASHER, DyeColor.CYAN, DyeColor.PINK), TropicalFishEntity.toVariant(Variety.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), TropicalFishEntity.toVariant(Variety.BETTY, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SNOOPER, DyeColor.GRAY, DyeColor.RED), TropicalFishEntity.toVariant(Variety.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.KOB, DyeColor.RED, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), TropicalFishEntity.toVariant(Variety.DASHER, DyeColor.CYAN, DyeColor.YELLOW), TropicalFishEntity.toVariant(Variety.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
    private boolean commonSpawn = true;

    private static int toVariant(Variety variety, DyeColor baseColor, DyeColor patternColor) {
        return variety.getShape() & 0xFF | (variety.getPattern() & 0xFF) << 8 | (baseColor.getId() & 0xFF) << 16 | (patternColor.getId() & 0xFF) << 24;
    }

    public TropicalFishEntity(EntityType<? extends TropicalFishEntity> entityType, World world) {
        super((EntityType<? extends SchoolingFishEntity>)entityType, world);
    }

    public static String getToolTipForVariant(int variant) {
        return "entity.minecraft.tropical_fish.predefined." + variant;
    }

    public static DyeColor getBaseDyeColor(int variant) {
        return DyeColor.byId(TropicalFishEntity.getBaseDyeColorIndex(variant));
    }

    public static DyeColor getPatternDyeColor(int variant) {
        return DyeColor.byId(TropicalFishEntity.getPatternDyeColorIndex(variant));
    }

    public static String getTranslationKey(int variant) {
        int i = TropicalFishEntity.getShape(variant);
        int j = TropicalFishEntity.getPattern(variant);
        return "entity.minecraft.tropical_fish.type." + Variety.getTranslateKey(i, j);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(nbt.getInt("Variant"));
    }

    public void setVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    }

    @Override
    public boolean spawnsTooManyForEachTry(int count) {
        return !this.commonSpawn;
    }

    public int getVariant() {
        return this.dataTracker.get(VARIANT);
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        super.copyDataToStack(stack);
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt(BUCKET_VARIANT_TAG_KEY, this.getVariant());
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

    private static int getBaseDyeColorIndex(int variant) {
        return (variant & 0xFF0000) >> 16;
    }

    public float[] getBaseColorComponents() {
        return DyeColor.byId(TropicalFishEntity.getBaseDyeColorIndex(this.getVariant())).getColorComponents();
    }

    private static int getPatternDyeColorIndex(int variant) {
        return (variant & 0xFF000000) >> 24;
    }

    public float[] getPatternColorComponents() {
        return DyeColor.byId(TropicalFishEntity.getPatternDyeColorIndex(this.getVariant())).getColorComponents();
    }

    public static int getShape(int variant) {
        return Math.min(variant & 0xFF, 1);
    }

    public int getShape() {
        return TropicalFishEntity.getShape(this.getVariant());
    }

    private static int getPattern(int variant) {
        return Math.min((variant & 0xFF00) >> 8, 5);
    }

    public Identifier getVarietyId() {
        if (TropicalFishEntity.getShape(this.getVariant()) == 0) {
            return SMALL_FISH_VARIETY_IDS[TropicalFishEntity.getPattern(this.getVariant())];
        }
        return LARGE_FISH_VARIETY_IDS[TropicalFishEntity.getPattern(this.getVariant())];
    }

    public Identifier getShapeId() {
        return SHAPE_IDS[TropicalFishEntity.getShape(this.getVariant())];
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        int l;
        int k;
        int j;
        int i;
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        if (spawnReason == SpawnReason.BUCKET && entityNbt != null && entityNbt.contains(BUCKET_VARIANT_TAG_KEY, 3)) {
            this.setVariant(entityNbt.getInt(BUCKET_VARIANT_TAG_KEY));
            return entityData;
        }
        Random random = world.getRandom();
        if (entityData instanceof TropicalFishData) {
            TropicalFishData tropicalFishData = (TropicalFishData)entityData;
            i = tropicalFishData.shape;
            j = tropicalFishData.pattern;
            k = tropicalFishData.baseColor;
            l = tropicalFishData.patternColor;
        } else if ((double)random.nextFloat() < 0.9) {
            int m = Util.getRandom(COMMON_VARIANTS, random);
            i = m & 0xFF;
            j = (m & 0xFF00) >> 8;
            k = (m & 0xFF0000) >> 16;
            l = (m & 0xFF000000) >> 24;
            entityData = new TropicalFishData(this, i, j, k, l);
        } else {
            this.commonSpawn = false;
            i = random.nextInt(2);
            j = random.nextInt(6);
            k = random.nextInt(15);
            l = random.nextInt(15);
        }
        this.setVariant(i | j << 8 | k << 16 | l << 24);
        return entityData;
    }

    public static boolean canTropicalFishSpawn(EntityType<TropicalFishEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        return world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER) && (world.getBiome(pos).isIn(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterCreatureEntity.canSpawn(type, world, reason, pos, random));
    }

    static final class Variety
    extends Enum<Variety> {
        public static final /* enum */ Variety KOB = new Variety(0, 0);
        public static final /* enum */ Variety SUNSTREAK = new Variety(0, 1);
        public static final /* enum */ Variety SNOOPER = new Variety(0, 2);
        public static final /* enum */ Variety DASHER = new Variety(0, 3);
        public static final /* enum */ Variety BRINELY = new Variety(0, 4);
        public static final /* enum */ Variety SPOTTY = new Variety(0, 5);
        public static final /* enum */ Variety FLOPPER = new Variety(1, 0);
        public static final /* enum */ Variety STRIPEY = new Variety(1, 1);
        public static final /* enum */ Variety GLITTER = new Variety(1, 2);
        public static final /* enum */ Variety BLOCKFISH = new Variety(1, 3);
        public static final /* enum */ Variety BETTY = new Variety(1, 4);
        public static final /* enum */ Variety CLAYFISH = new Variety(1, 5);
        private final int shape;
        private final int pattern;
        private static final Variety[] VALUES;
        private static final /* synthetic */ Variety[] field_6886;

        public static Variety[] values() {
            return (Variety[])field_6886.clone();
        }

        public static Variety valueOf(String string) {
            return Enum.valueOf(Variety.class, string);
        }

        private Variety(int shape, int pattern) {
            this.shape = shape;
            this.pattern = pattern;
        }

        public int getShape() {
            return this.shape;
        }

        public int getPattern() {
            return this.pattern;
        }

        public static String getTranslateKey(int shape, int pattern) {
            return VALUES[pattern + 6 * shape].getTranslationKey();
        }

        public String getTranslationKey() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        private static /* synthetic */ Variety[] method_36643() {
            return new Variety[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
        }

        static {
            field_6886 = Variety.method_36643();
            VALUES = Variety.values();
        }
    }

    static class TropicalFishData
    extends SchoolingFishEntity.FishData {
        final int shape;
        final int pattern;
        final int baseColor;
        final int patternColor;

        TropicalFishData(TropicalFishEntity leader, int shape, int pattern, int baseColor, int patternColor) {
            super(leader);
            this.shape = shape;
            this.pattern = pattern;
            this.baseColor = baseColor;
            this.patternColor = patternColor;
        }
    }
}

