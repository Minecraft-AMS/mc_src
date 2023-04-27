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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class DisconnectedScreen
extends Screen {
    private static final Text TO_MENU_TEXT = Text.translatable("gui.toMenu");
    private static final Text TO_TITLE_TEXT = Text.translatable("gui.toTitle");
    private final Screen parent;
    private final Text reason;
    private final Text buttonLabel;
    private final GridWidget grid = new GridWidget();

    public DisconnectedScreen(Screen parent, Text title, Text reason) {
        this(parent, title, reason, TO_MENU_TEXT);
    }

    public DisconnectedScreen(Screen parent, Text title, Text reason, Text buttonLabel) {
        super(title);
        this.parent = parent;
        this.reason = reason;
        this.buttonLabel = buttonLabel;
    }

    @Override
    protected void init() {
        this.grid.getMainPositioner().alignHorizontalCenter().margin(10);
        GridWidget.Adder adder = this.grid.createAdder(1);
        adder.add(new TextWidget(this.title, this.textRenderer));
        adder.add(new MultilineTextWidget(this.reason, this.textRenderer).setMaxWidth(this.width - 50).setCentered(true));
        ButtonWidget buttonWidget = this.client.isMultiplayerEnabled() ? ButtonWidget.builder(this.buttonLabel, button -> this.client.setScreen(this.parent)).build() : ButtonWidget.builder(TO_TITLE_TEXT, button -> this.client.setScreen(new TitleScreen())).build();
        adder.add(buttonWidget);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
        this.initTabNavigation();
    }

    @Override
    protected void initTabNavigation() {
        SimplePositioningWidget.setPos(this.grid, this.getNavigationFocus());
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.title, this.reason);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
    }
}

