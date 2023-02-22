/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenu {
    private static final SpectatorMenuCommand CLOSE_COMMAND = new CloseSpectatorMenuCommand();
    private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
    private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
    private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
    private static final int field_32443 = 8;
    static final Text CLOSE_TEXT = new TranslatableText("spectatorMenu.close");
    static final Text PREVIOUS_PAGE_TEXT = new TranslatableText("spectatorMenu.previous_page");
    static final Text NEXT_PAGE_TEXT = new TranslatableText("spectatorMenu.next_page");
    public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand(){

        @Override
        public void use(SpectatorMenu menu) {
        }

        @Override
        public Text getName() {
            return LiteralText.EMPTY;
        }

        @Override
        public void renderIcon(MatrixStack matrices, float f, int i) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    private final SpectatorMenuCloseCallback closeCallback;
    private SpectatorMenuCommandGroup currentGroup = new RootSpectatorCommandGroup();
    private int selectedSlot = -1;
    int page;

    public SpectatorMenu(SpectatorMenuCloseCallback closeCallback) {
        this.closeCallback = closeCallback;
    }

    public SpectatorMenuCommand getCommand(int slot) {
        int i = slot + this.page * 6;
        if (this.page > 0 && slot == 0) {
            return PREVIOUS_PAGE_COMMAND;
        }
        if (slot == 7) {
            if (i < this.currentGroup.getCommands().size()) {
                return NEXT_PAGE_COMMAND;
            }
            return DISABLED_NEXT_PAGE_COMMAND;
        }
        if (slot == 8) {
            return CLOSE_COMMAND;
        }
        if (i < 0 || i >= this.currentGroup.getCommands().size()) {
            return BLANK_COMMAND;
        }
        return (SpectatorMenuCommand)MoreObjects.firstNonNull((Object)this.currentGroup.getCommands().get(i), (Object)BLANK_COMMAND);
    }

    public List<SpectatorMenuCommand> getCommands() {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getCommand(i));
        }
        return list;
    }

    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }

    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }

    public void useCommand(int slot) {
        SpectatorMenuCommand spectatorMenuCommand = this.getCommand(slot);
        if (spectatorMenuCommand != BLANK_COMMAND) {
            if (this.selectedSlot == slot && spectatorMenuCommand.isEnabled()) {
                spectatorMenuCommand.use(this);
            } else {
                this.selectedSlot = slot;
            }
        }
    }

    public void close() {
        this.closeCallback.close(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectElement(SpectatorMenuCommandGroup group) {
        this.currentGroup = group;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.getCommands(), this.selectedSlot);
    }

    @Environment(value=EnvType.CLIENT)
    static class CloseSpectatorMenuCommand
    implements SpectatorMenuCommand {
        CloseSpectatorMenuCommand() {
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.close();
        }

        @Override
        public Text getName() {
            return CLOSE_TEXT;
        }

        @Override
        public void renderIcon(MatrixStack matrices, float f, int i) {
            RenderSystem.setShaderTexture(0, SpectatorHud.SPECTATOR_TEXTURE);
            DrawableHelper.drawTexture(matrices, 0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ChangePageSpectatorMenuCommand
    implements SpectatorMenuCommand {
        private final int direction;
        private final boolean enabled;

        public ChangePageSpectatorMenuCommand(int direction, boolean enabled) {
            this.direction = direction;
            this.enabled = enabled;
        }

        @Override
        public void use(SpectatorMenu menu) {
            menu.page += this.direction;
        }

        @Override
        public Text getName() {
            return this.direction < 0 ? PREVIOUS_PAGE_TEXT : NEXT_PAGE_TEXT;
        }

        @Override
        public void renderIcon(MatrixStack matrices, float f, int i) {
            RenderSystem.setShaderTexture(0, SpectatorHud.SPECTATOR_TEXTURE);
            if (this.direction < 0) {
                DrawableHelper.drawTexture(matrices, 0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            } else {
                DrawableHelper.drawTexture(matrices, 0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }
}

