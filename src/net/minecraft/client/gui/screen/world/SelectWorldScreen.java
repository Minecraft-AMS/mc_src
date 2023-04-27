/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.PathUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelInfo;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SelectWorldScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GeneratorOptions DEBUG_GENERATOR_OPTIONS = new GeneratorOptions("test1".hashCode(), true, false);
    protected final Screen parent;
    private ButtonWidget deleteButton;
    private ButtonWidget selectButton;
    private ButtonWidget editButton;
    private ButtonWidget recreateButton;
    protected TextFieldWidget searchBox;
    private WorldListWidget levelList;

    public SelectWorldScreen(Screen parent) {
        super(Text.translatable("selectWorld.title"));
        this.parent = parent;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public void tick() {
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, this.searchBox, Text.translatable("selectWorld.search"));
        this.searchBox.setChangedListener(search -> this.levelList.setSearch((String)search));
        this.levelList = new WorldListWidget(this, this.client, this.width, this.height, 48, this.height - 64, 36, this.searchBox.getText(), this.levelList);
        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.levelList);
        this.selectButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.select"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::play)).dimensions(this.width / 2 - 154, this.height - 52, 150, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.create"), button -> CreateWorldScreen.create(this.client, this)).dimensions(this.width / 2 + 4, this.height - 52, 150, 20).build());
        this.editButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.edit"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::edit)).dimensions(this.width / 2 - 154, this.height - 28, 72, 20).build());
        this.deleteButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.delete"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::deleteIfConfirmed)).dimensions(this.width / 2 - 76, this.height - 28, 72, 20).build());
        this.recreateButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectWorld.recreate"), button -> this.levelList.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::recreate)).dimensions(this.width / 2 + 4, this.height - 28, 72, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(this.width / 2 + 82, this.height - 28, 72, 20).build());
        this.worldSelected(false, false);
        this.setInitialFocus(this.searchBox);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.levelList.render(context, mouseX, mouseY, delta);
        this.searchBox.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    public void worldSelected(boolean buttonsActive, boolean deleteButtonActive) {
        this.selectButton.active = buttonsActive;
        this.editButton.active = buttonsActive;
        this.recreateButton.active = buttonsActive;
        this.deleteButton.active = deleteButtonActive;
    }

    @Override
    public void removed() {
        if (this.levelList != null) {
            this.levelList.children().forEach(WorldListWidget.Entry::close);
        }
    }

    private /* synthetic */ void method_35739(ButtonWidget button) {
        try {
            WorldListWidget.WorldEntry worldEntry;
            WorldListWidget.Entry entry;
            String string = "DEBUG world";
            if (!this.levelList.children().isEmpty() && (entry = (WorldListWidget.Entry)this.levelList.children().get(0)) instanceof WorldListWidget.WorldEntry && (worldEntry = (WorldListWidget.WorldEntry)entry).getLevelDisplayName().equals("DEBUG world")) {
                worldEntry.delete();
            }
            LevelInfo levelInfo = new LevelInfo("DEBUG world", GameMode.SPECTATOR, false, Difficulty.NORMAL, true, new GameRules(), DataConfiguration.SAFE_MODE);
            String string2 = PathUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), "DEBUG world", "");
            this.client.createIntegratedServerLoader().createAndStart(string2, levelInfo, DEBUG_GENERATOR_OPTIONS, WorldPresets::createDemoOptions);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to recreate the debug world", (Throwable)iOException);
        }
    }
}

