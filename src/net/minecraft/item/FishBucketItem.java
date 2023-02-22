/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FishBucketItem
extends BucketItem {
    private final EntityType<?> fishType;

    public FishBucketItem(EntityType<?> type, Fluid fluid, Item.Settings settings) {
        super(fluid, settings);
        this.fishType = type;
    }

    @Override
    public void onEmptied(World world, ItemStack stack, BlockPos pos) {
        if (!world.isClient) {
            this.spawnFish(world, stack, pos);
        }
    }

    @Override
    protected void playEmptyingSound(@Nullable PlayerEntity player, IWorld world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }

    private void spawnFish(World world, ItemStack stack, BlockPos pos) {
        Entity entity = this.fishType.spawnFromItemStack(world, stack, null, pos, SpawnType.BUCKET, true, false);
        if (entity != null) {
            ((FishEntity)entity).setFromBucket(true);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag compoundTag;
        if (this.fishType == EntityType.TROPICAL_FISH && (compoundTag = stack.getTag()) != null && compoundTag.contains("BucketVariantTag", 3)) {
            int i = compoundTag.getInt("BucketVariantTag");
            Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};
            String string = "color.minecraft." + TropicalFishEntity.getBaseDyeColor(i);
            String string2 = "color.minecraft." + TropicalFishEntity.getPatternDyeColor(i);
            for (int j = 0; j < TropicalFishEntity.COMMON_VARIANTS.length; ++j) {
                if (i != TropicalFishEntity.COMMON_VARIANTS[j]) continue;
                tooltip.add(new TranslatableText(TropicalFishEntity.getToolTipForVariant(j), new Object[0]).formatted(formattings));
                return;
            }
            tooltip.add(new TranslatableText(TropicalFishEntity.getTranslationKey(i), new Object[0]).formatted(formattings));
            TranslatableText text = new TranslatableText(string, new Object[0]);
            if (!string.equals(string2)) {
                text.append(", ").append(new TranslatableText(string2, new Object[0]));
            }
            text.formatted(formattings);
            tooltip.add(text);
        }
    }
}

