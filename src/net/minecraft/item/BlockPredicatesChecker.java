/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BlockPredicatesChecker {
    public static final BlockPredicateArgumentType BLOCK_PREDICATE = BlockPredicateArgumentType.blockPredicate();
    private final String key;
    @Nullable
    private CachedBlockPosition cachedPos;
    private boolean lastResult;
    private boolean nbtAware;

    public BlockPredicatesChecker(String key) {
        this.key = key;
    }

    private static boolean canUseCache(CachedBlockPosition pos, @Nullable CachedBlockPosition cachedPos, boolean nbtAware) {
        if (cachedPos == null || pos.getBlockState() != cachedPos.getBlockState()) {
            return false;
        }
        if (!nbtAware) {
            return true;
        }
        if (pos.getBlockEntity() == null && cachedPos.getBlockEntity() == null) {
            return true;
        }
        if (pos.getBlockEntity() == null || cachedPos.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(pos.getBlockEntity().createNbtWithId(), cachedPos.getBlockEntity().createNbtWithId());
    }

    public boolean check(ItemStack stack, Registry<Block> blockRegistry, CachedBlockPosition pos) {
        if (BlockPredicatesChecker.canUseCache(pos, this.cachedPos, this.nbtAware)) {
            return this.lastResult;
        }
        this.cachedPos = pos;
        this.nbtAware = false;
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains(this.key, 9)) {
            NbtList nbtList = nbtCompound.getList(this.key, 8);
            for (int i = 0; i < nbtList.size(); ++i) {
                String string = nbtList.getString(i);
                try {
                    BlockPredicateArgumentType.BlockPredicate blockPredicate = BLOCK_PREDICATE.parse(new StringReader(string));
                    this.nbtAware |= blockPredicate.hasNbt();
                    Predicate<CachedBlockPosition> predicate = blockPredicate.create(blockRegistry);
                    if (predicate.test(pos)) {
                        this.lastResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastResult = false;
        return false;
    }
}

