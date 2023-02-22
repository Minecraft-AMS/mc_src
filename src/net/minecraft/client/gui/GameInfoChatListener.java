/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GameInfoChatListener
implements ClientChatListener {
    private final MinecraftClient client;

    public GameInfoChatListener(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void onChatMessage(MessageType messageType, Text message) {
        this.client.inGameHud.setOverlayMessage(message, false);
    }
}
