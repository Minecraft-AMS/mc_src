/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.resourcepack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SelectedResourcePackListWidget
extends ResourcePackListWidget {
    public SelectedResourcePackListWidget(MinecraftClient client, int width, int height) {
        super(client, width, height, new TranslatableText("resourcePack.selected.title", new Object[0]));
    }
}

