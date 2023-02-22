/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

public interface ParsableText {
    public MutableText parse(@Nullable ServerCommandSource var1, @Nullable Entity var2, int var3) throws CommandSyntaxException;
}

