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
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.AccessibilityScreen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.SkinOptionsScreen;
import net.minecraft.client.gui.screen.options.SoundOptionsScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.Difficulty;

@Environment(value=EnvType.CLIENT)
public class SettingsScreen
extends Screen {
    private static final Option[] OPTIONS = new Option[]{Option.FOV};
    private final Screen parent;
    private final GameOptions settings;
    private ButtonWidget difficultyButton;
    private LockButtonWidget lockDifficultyButton;
    private Difficulty difficulty;

    public SettingsScreen(Screen parent, GameOptions gameOptions) {
        super(new TranslatableText("options.title", new Object[0]));
        this.parent = parent;
        this.settings = gameOptions;
    }

    @Override
    protected void init() {
        int i = 0;
        for (Option option : OPTIONS) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 - 12 + 24 * (i >> 1);
            this.addButton(option.createButton(this.minecraft.options, j, k, 150));
            ++i;
        }
        if (this.minecraft.world != null) {
            this.difficulty = this.minecraft.world.getDifficulty();
            this.difficultyButton = this.addButton(new ButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyButtonText(this.difficulty), buttonWidget -> {
                this.difficulty = Difficulty.byOrdinal(this.difficulty.getId() + 1);
                this.minecraft.getNetworkHandler().sendPacket(new UpdateDifficultyC2SPacket(this.difficulty));
                this.difficultyButton.setMessage(this.getDifficultyButtonText(this.difficulty));
            }));
            if (this.minecraft.isIntegratedServerRunning() && !this.minecraft.world.getLevelProperties().isHardcore()) {
                this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
                this.lockDifficultyButton = this.addButton(new LockButtonWidget(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, buttonWidget -> this.minecraft.openScreen(new ConfirmScreen(this::lockDifficulty, new TranslatableText("difficulty.lock.title", new Object[0]), new TranslatableText("difficulty.lock.question", new TranslatableText("options.difficulty." + this.minecraft.world.getLevelProperties().getDifficulty().getName(), new Object[0]))))));
                this.lockDifficultyButton.setLocked(this.minecraft.world.getLevelProperties().isDifficultyLocked());
                this.lockDifficultyButton.active = !this.lockDifficultyButton.isLocked();
                this.difficultyButton.active = !this.lockDifficultyButton.isLocked();
            } else {
                this.difficultyButton.active = false;
            }
        } else {
            this.addButton(new OptionButtonWidget(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, Option.REALMS_NOTIFICATIONS, Option.REALMS_NOTIFICATIONS.getDisplayString(this.settings), buttonWidget -> {
                Option.REALMS_NOTIFICATIONS.set(this.settings);
                this.settings.write();
                buttonWidget.setMessage(Option.REALMS_NOTIFICATIONS.getDisplayString(this.settings));
            }));
        }
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.skinCustomisation", new Object[0]), buttonWidget -> this.minecraft.openScreen(new SkinOptionsScreen(this))));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.translate("options.sounds", new Object[0]), buttonWidget -> this.minecraft.openScreen(new SoundOptionsScreen(this, this.settings))));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.video", new Object[0]), buttonWidget -> this.minecraft.openScreen(new VideoOptionsScreen(this, this.settings))));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.translate("options.controls", new Object[0]), buttonWidget -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.settings))));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.language", new Object[0]), buttonWidget -> this.minecraft.openScreen(new LanguageOptionsScreen(this, this.settings, this.minecraft.getLanguageManager()))));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.translate("options.chat.title", new Object[0]), buttonWidget -> this.minecraft.openScreen(new ChatOptionsScreen(this, this.settings))));
        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.resourcepack", new Object[0]), buttonWidget -> this.minecraft.openScreen(new ResourcePackOptionsScreen(this))));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.translate("options.accessibility.title", new Object[0]), buttonWidget -> this.minecraft.openScreen(new AccessibilityScreen(this, this.settings))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(this.parent)));
    }

    public String getDifficultyButtonText(Difficulty difficulty) {
        return new TranslatableText("options.difficulty", new Object[0]).append(": ").append(difficulty.getTranslatableName()).asFormattedString();
    }

    private void lockDifficulty(boolean difficultyLocked) {
        this.minecraft.openScreen(this);
        if (difficultyLocked && this.minecraft.world != null) {
            this.minecraft.getNetworkHandler().sendPacket(new UpdateDifficultyLockC2SPacket(true));
            this.lockDifficultyButton.setLocked(true);
            this.lockDifficultyButton.active = false;
            this.difficultyButton.active = false;
        }
    }

    @Override
    public void removed() {
        this.settings.write();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 15, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}
