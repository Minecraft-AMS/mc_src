/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class RealmsLongConfirmationScreen
extends RealmsScreen {
    private final Type type;
    private final Text line2;
    private final Text line3;
    protected final BooleanConsumer callback;
    private final boolean yesNoQuestion;

    public RealmsLongConfirmationScreen(BooleanConsumer callback, Type type, Text line2, Text line3, boolean yesNoQuestion) {
        super(NarratorManager.EMPTY);
        this.callback = callback;
        this.type = type;
        this.line2 = line2;
        this.line3 = line3;
        this.yesNoQuestion = yesNoQuestion;
    }

    @Override
    public void init() {
        if (this.yesNoQuestion) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 105, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.YES, button -> this.callback.accept(true)));
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, RealmsLongConfirmationScreen.row(8), 100, 20, ScreenTexts.NO, button -> this.callback.accept(false)));
        } else {
            this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, RealmsLongConfirmationScreen.row(8), 100, 20, new TranslatableText("mco.gui.ok"), button -> this.callback.accept(true)));
        }
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinLines(this.type.text, this.line2, this.line3);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.callback.accept(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsLongConfirmationScreen.drawCenteredText(matrices, this.textRenderer, this.type.text, this.width / 2, RealmsLongConfirmationScreen.row(2), this.type.colorCode);
        RealmsLongConfirmationScreen.drawCenteredText(matrices, this.textRenderer, this.line2, this.width / 2, RealmsLongConfirmationScreen.row(4), 0xFFFFFF);
        RealmsLongConfirmationScreen.drawCenteredText(matrices, this.textRenderer, this.line3, this.width / 2, RealmsLongConfirmationScreen.row(6), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type WARNING = new Type("Warning!", 0xFF0000);
        public static final /* enum */ Type INFO = new Type("Info!", 8226750);
        public final int colorCode;
        public final Text text;
        private static final /* synthetic */ Type[] field_19907;

        public static Type[] values() {
            return (Type[])field_19907.clone();
        }

        public static Type valueOf(String name) {
            return Enum.valueOf(Type.class, name);
        }

        private Type(String text, int colorCode) {
            this.text = new LiteralText(text);
            this.colorCode = colorCode;
        }

        private static /* synthetic */ Type[] method_36854() {
            return new Type[]{WARNING, INFO};
        }

        static {
            field_19907 = Type.method_36854();
        }
    }
}

