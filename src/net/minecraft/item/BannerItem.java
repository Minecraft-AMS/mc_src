/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class BannerItem
extends WallStandingBlockItem {
    public BannerItem(Block block, Block block2, Item.Settings settings) {
        super(block, block2, settings);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)block);
        Validate.isInstanceOf(AbstractBannerBlock.class, (Object)block2);
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendBannerTooltip(ItemStack stack, List<Text> tooltip) {
        NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
        if (nbtCompound == null || !nbtCompound.contains("Patterns")) {
            return;
        }
        NbtList nbtList = nbtCompound.getList("Patterns", 10);
        for (int i = 0; i < nbtList.size() && i < 6; ++i) {
            NbtCompound nbtCompound2 = nbtList.getCompound(i);
            DyeColor dyeColor = DyeColor.byId(nbtCompound2.getInt("Color"));
            BannerPattern bannerPattern = BannerPattern.byId(nbtCompound2.getString("Pattern"));
            if (bannerPattern == null) continue;
            tooltip.add(new TranslatableText("block.minecraft.banner." + bannerPattern.getName() + '.' + dyeColor.getName()).formatted(Formatting.GRAY));
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        BannerItem.appendBannerTooltip(stack, tooltip);
    }
}

