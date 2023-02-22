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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WrittenBookItem
extends Item {
    public WrittenBookItem(Item.Settings settings) {
        super(settings);
    }

    public static boolean isValid(@Nullable NbtCompound nbt) {
        if (!WritableBookItem.isValid(nbt)) {
            return false;
        }
        if (!nbt.contains("title", 8)) {
            return false;
        }
        String string = nbt.getString("title");
        if (string.length() > 32) {
            return false;
        }
        return nbt.contains("author", 8);
    }

    public static int getGeneration(ItemStack stack) {
        return stack.getTag().getInt("generation");
    }

    public static int getPageCount(ItemStack stack) {
        NbtCompound nbtCompound = stack.getTag();
        return nbtCompound != null ? nbtCompound.getList("pages", 8).size() : 0;
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtCompound nbtCompound;
        String string;
        if (stack.hasTag() && !ChatUtil.isEmpty(string = (nbtCompound = stack.getTag()).getString("title"))) {
            return new LiteralText(string);
        }
        return super.getName(stack);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasTag()) {
            NbtCompound nbtCompound = stack.getTag();
            String string = nbtCompound.getString("author");
            if (!ChatUtil.isEmpty(string)) {
                tooltip.add(new TranslatableText("book.byAuthor", string).formatted(Formatting.GRAY));
            }
            tooltip.add(new TranslatableText("book.generation." + nbtCompound.getInt("generation")).formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (blockState.isOf(Blocks.LECTERN)) {
            return LecternBlock.putBookIfAbsent(world, blockPos, blockState, context.getStack()) ? ActionResult.success(world.isClient) : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.useBook(itemStack, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    public static boolean resolve(ItemStack book, @Nullable ServerCommandSource commandSource, @Nullable PlayerEntity player) {
        NbtCompound nbtCompound = book.getTag();
        if (nbtCompound == null || nbtCompound.getBoolean("resolved")) {
            return false;
        }
        nbtCompound.putBoolean("resolved", true);
        if (!WrittenBookItem.isValid(nbtCompound)) {
            return false;
        }
        NbtList nbtList = nbtCompound.getList("pages", 8);
        for (int i = 0; i < nbtList.size(); ++i) {
            MutableText text;
            String string = nbtList.getString(i);
            try {
                text = Text.Serializer.fromLenientJson(string);
                text = Texts.parse(commandSource, text, player, 0);
            }
            catch (Exception exception) {
                text = new LiteralText(string);
            }
            nbtList.set(i, NbtString.of(Text.Serializer.toJson(text)));
        }
        nbtCompound.put("pages", nbtList);
        return true;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}

