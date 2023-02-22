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
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkChargeItem
extends Item {
    public FireworkChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag compoundTag = stack.getSubTag("Explosion");
        if (compoundTag != null) {
            FireworkChargeItem.appendFireworkTooltip(compoundTag, tooltip);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendFireworkTooltip(CompoundTag tag, List<Text> tooltip) {
        int[] js;
        FireworkItem.Type type = FireworkItem.Type.byId(tag.getByte("Type"));
        tooltip.add(new TranslatableText("item.minecraft.firework_star.shape." + type.getName(), new Object[0]).formatted(Formatting.GRAY));
        int[] is = tag.getIntArray("Colors");
        if (is.length > 0) {
            tooltip.add(FireworkChargeItem.appendColors(new LiteralText("").formatted(Formatting.GRAY), is));
        }
        if ((js = tag.getIntArray("FadeColors")).length > 0) {
            tooltip.add(FireworkChargeItem.appendColors(new TranslatableText("item.minecraft.firework_star.fade_to", new Object[0]).append(" ").formatted(Formatting.GRAY), js));
        }
        if (tag.getBoolean("Trail")) {
            tooltip.add(new TranslatableText("item.minecraft.firework_star.trail", new Object[0]).formatted(Formatting.GRAY));
        }
        if (tag.getBoolean("Flicker")) {
            tooltip.add(new TranslatableText("item.minecraft.firework_star.flicker", new Object[0]).formatted(Formatting.GRAY));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Text appendColors(Text line, int[] colors) {
        for (int i = 0; i < colors.length; ++i) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(FireworkChargeItem.getColorText(colors[i]));
        }
        return line;
    }

    @Environment(value=EnvType.CLIENT)
    private static Text getColorText(int color) {
        DyeColor dyeColor = DyeColor.byFireworkColor(color);
        if (dyeColor == null) {
            return new TranslatableText("item.minecraft.firework_star.custom_color", new Object[0]);
        }
        return new TranslatableText("item.minecraft.firework_star." + dyeColor.getName(), new Object[0]);
    }
}
