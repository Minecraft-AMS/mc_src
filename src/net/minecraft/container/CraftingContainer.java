/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class CraftingContainer<C extends Inventory>
extends Container {
    public CraftingContainer(ContainerType<?> containerType, int i) {
        super(containerType, i);
    }

    public void fillInputSlots(boolean bl, Recipe<?> recipe, ServerPlayerEntity serverPlayerEntity) {
        new InputSlotFiller(this).fillInputSlots(serverPlayerEntity, recipe, bl);
    }

    public abstract void populateRecipeFinder(RecipeFinder var1);

    public abstract void clearCraftingSlots();

    public abstract boolean matches(Recipe<? super C> var1);

    public abstract int getCraftingResultSlotIndex();

    public abstract int getCraftingWidth();

    public abstract int getCraftingHeight();

    @Environment(value=EnvType.CLIENT)
    public abstract int getCraftingSlotCount();
}

