/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package net.minecraft.data.client;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.block.Block;

public interface BlockStateSupplier
extends Supplier<JsonElement> {
    public Block getBlock();
}

