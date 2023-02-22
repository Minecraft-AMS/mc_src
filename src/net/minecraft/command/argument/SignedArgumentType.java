/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import net.minecraft.command.argument.DecoratableArgumentType;

public interface SignedArgumentType<T>
extends DecoratableArgumentType<T> {
    public String toSignedString(T var1);
}

