/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SleepingChatScreen
extends ChatScreen {
    public SleepingChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableText("multiplayer.stopSleeping"), button -> this.stopSleeping()));
    }

    @Override
    public void close() {
        this.stopSleeping();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.stopSleeping();
        } else if (keyCode == 257 || keyCode == 335) {
            String string = this.chatField.getText().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }
            this.chatField.setText("");
            this.client.inGameHud.getChatHud().resetScroll();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void stopSleeping() {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        clientPlayNetworkHandler.sendPacket(new ClientCommandC2SPacket(this.client.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
    }

    public void closeChatIfEmpty() {
        if (this.chatField.getText().isEmpty()) {
            this.client.setScreen(null);
        } else {
            this.client.setScreen(new ChatScreen(this.chatField.getText()));
        }
    }
}

