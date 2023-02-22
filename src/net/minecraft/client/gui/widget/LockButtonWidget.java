/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class LockButtonWidget
extends ButtonWidget {
    private boolean locked;

    public LockButtonWidget(int x, int y, ButtonWidget.PressAction action) {
        super(x, y, 20, 20, new TranslatableText("narrator.button.difficulty_lock"), action);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return ScreenTexts.joinSentences(super.getNarrationMessage(), this.isLocked() ? new TranslatableText("narrator.button.difficulty_lock.locked") : new TranslatableText("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ButtonWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        IconLocation iconLocation = !this.active ? (this.locked ? IconLocation.LOCKED_DISABLED : IconLocation.UNLOCKED_DISABLED) : (this.isHovered() ? (this.locked ? IconLocation.LOCKED_HOVER : IconLocation.UNLOCKED_HOVER) : (this.locked ? IconLocation.LOCKED : IconLocation.UNLOCKED));
        this.drawTexture(matrices, this.x, this.y, iconLocation.getU(), iconLocation.getV(), this.width, this.height);
    }

    @Environment(value=EnvType.CLIENT)
    static final class IconLocation
    extends Enum<IconLocation> {
        public static final /* enum */ IconLocation LOCKED = new IconLocation(0, 146);
        public static final /* enum */ IconLocation LOCKED_HOVER = new IconLocation(0, 166);
        public static final /* enum */ IconLocation LOCKED_DISABLED = new IconLocation(0, 186);
        public static final /* enum */ IconLocation UNLOCKED = new IconLocation(20, 146);
        public static final /* enum */ IconLocation UNLOCKED_HOVER = new IconLocation(20, 166);
        public static final /* enum */ IconLocation UNLOCKED_DISABLED = new IconLocation(20, 186);
        private final int u;
        private final int v;
        private static final /* synthetic */ IconLocation[] field_2136;

        public static IconLocation[] values() {
            return (IconLocation[])field_2136.clone();
        }

        public static IconLocation valueOf(String string) {
            return Enum.valueOf(IconLocation.class, string);
        }

        private IconLocation(int j, int k) {
            this.u = j;
            this.v = k;
        }

        public int getU() {
            return this.u;
        }

        public int getV() {
            return this.v;
        }

        private static /* synthetic */ IconLocation[] method_36870() {
            return new IconLocation[]{LOCKED, LOCKED_HOVER, LOCKED_DISABLED, UNLOCKED, UNLOCKED_HOVER, UNLOCKED_DISABLED};
        }

        static {
            field_2136 = IconLocation.method_36870();
        }
    }
}

