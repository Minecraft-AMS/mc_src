/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryListener
implements ContainerListener {
    private final MinecraftClient client;

    public CreativeInventoryListener(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void onContainerRegistered(Container container, DefaultedList<ItemStack> defaultedList) {
    }

    @Override
    public void onContainerSlotUpdate(Container container, int slotId, ItemStack itemStack) {
        this.client.interactionManager.clickCreativeStack(itemStack, slotId);
    }

    @Override
    public void onContainerPropertyUpdate(Container container, int propertyId, int i) {
    }
}
