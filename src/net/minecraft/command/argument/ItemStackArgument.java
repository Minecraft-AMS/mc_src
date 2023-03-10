/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemStackArgument
implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType((item, maxCount) -> new TranslatableText("arguments.item.overstacked", item, maxCount));
    private final Item item;
    @Nullable
    private final NbtCompound nbt;

    public ItemStackArgument(Item item, @Nullable NbtCompound nbt) {
        this.item = item;
        this.nbt = nbt;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.isOf(this.item) && NbtHelper.matches(this.nbt, itemStack.getNbt(), true);
    }

    public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
        ItemStack itemStack = new ItemStack(this.item, amount);
        if (this.nbt != null) {
            itemStack.setNbt(this.nbt);
        }
        if (checkOverstack && amount > itemStack.getMaxCount()) {
            throw OVERSTACKED_EXCEPTION.create((Object)Registry.ITEM.getId(this.item), (Object)itemStack.getMaxCount());
        }
        return itemStack;
    }

    public String asString() {
        StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.item));
        if (this.nbt != null) {
            stringBuilder.append(this.nbt);
        }
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ boolean test(Object stack) {
        return this.test((ItemStack)stack);
    }
}

