/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class EditBoxWidget
extends ScrollableWidget {
    private static final int CURSOR_PADDING = 1;
    private static final int CURSOR_COLOR = -3092272;
    private static final String UNDERSCORE = "_";
    private static final int FOCUSED_BOX_TEXT_COLOR = -2039584;
    private static final int UNFOCUSED_BOX_TEXT_COLOR = -857677600;
    private final TextRenderer textRenderer;
    private final Text placeholder;
    private final EditBox editBox;
    private int tick;

    public EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.placeholder = placeholder;
        this.editBox = new EditBox(textRenderer, width - this.getPaddingDoubled());
        this.editBox.setCursorChangeListener(this::onCursorChange);
    }

    public void setMaxLength(int maxLength) {
        this.editBox.setMaxLength(maxLength);
    }

    public void setChangeListener(Consumer<String> changeListener) {
        this.editBox.setChangeListener(changeListener);
    }

    public void setText(String text) {
        this.editBox.setText(text);
    }

    public String getText() {
        return this.editBox.getText();
    }

    public void tick() {
        ++this.tick;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable("gui.narrate.editBox", this.getMessage(), this.getText()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
            this.editBox.setSelecting(Screen.hasShiftDown());
            this.moveCursor(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
            this.editBox.setSelecting(true);
            this.moveCursor(mouseX, mouseY);
            this.editBox.setSelecting(Screen.hasShiftDown());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.handleSpecialKey(keyCode);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!(this.visible && this.isFocused() && SharedConstants.isValidChar(chr))) {
            return false;
        }
        this.editBox.replaceSelection(Character.toString(chr));
        return true;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        String string = this.editBox.getText();
        if (string.isEmpty() && !this.isFocused()) {
            context.drawTextWrapped(this.textRenderer, this.placeholder, this.getX() + this.getPadding(), this.getY() + this.getPadding(), this.width - this.getPaddingDoubled(), -857677600);
            return;
        }
        int i = this.editBox.getCursor();
        boolean bl = this.isFocused() && this.tick / 6 % 2 == 0;
        boolean bl2 = i < string.length();
        int j = 0;
        int k = 0;
        int l = this.getY() + this.getPadding();
        for (EditBox.Substring substring : this.editBox.getLines()) {
            boolean bl3 = this.isVisible(l, l + this.textRenderer.fontHeight);
            if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex()) {
                if (bl3) {
                    j = context.drawTextWithShadow(this.textRenderer, string.substring(substring.beginIndex(), i), this.getX() + this.getPadding(), l, -2039584) - 1;
                    context.fill(j, l - 1, j + 1, l + 1 + this.textRenderer.fontHeight, -3092272);
                    context.drawTextWithShadow(this.textRenderer, string.substring(i, substring.endIndex()), j, l, -2039584);
                }
            } else {
                if (bl3) {
                    j = context.drawTextWithShadow(this.textRenderer, string.substring(substring.beginIndex(), substring.endIndex()), this.getX() + this.getPadding(), l, -2039584) - 1;
                }
                k = l;
            }
            l += this.textRenderer.fontHeight;
        }
        if (bl && !bl2 && this.isVisible(k, k + this.textRenderer.fontHeight)) {
            context.drawTextWithShadow(this.textRenderer, UNDERSCORE, j, k, -3092272);
        }
        if (this.editBox.hasSelection()) {
            EditBox.Substring substring2 = this.editBox.getSelection();
            int m = this.getX() + this.getPadding();
            l = this.getY() + this.getPadding();
            for (EditBox.Substring substring3 : this.editBox.getLines()) {
                if (substring2.beginIndex() > substring3.endIndex()) {
                    l += this.textRenderer.fontHeight;
                    continue;
                }
                if (substring3.beginIndex() > substring2.endIndex()) break;
                if (this.isVisible(l, l + this.textRenderer.fontHeight)) {
                    int n = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                    int o = substring2.endIndex() > substring3.endIndex() ? this.width - this.getPadding() : this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                    this.drawSelection(context, m + n, l, m + o, l + this.textRenderer.fontHeight);
                }
                l += this.textRenderer.fontHeight;
            }
        }
    }

    @Override
    protected void renderOverlay(DrawContext context) {
        super.renderOverlay(context);
        if (this.editBox.hasMaxLength()) {
            int i = this.editBox.getMaxLength();
            MutableText text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
            context.drawTextWithShadow(this.textRenderer, text, this.getX() + this.width - this.textRenderer.getWidth(text), this.getY() + this.height + 4, 0xA0A0A0);
        }
    }

    @Override
    public int getContentsHeight() {
        return this.textRenderer.fontHeight * this.editBox.getLineCount();
    }

    @Override
    protected boolean overflows() {
        return (double)this.editBox.getLineCount() > this.getMaxLinesWithoutOverflow();
    }

    @Override
    protected double getDeltaYPerScroll() {
        return (double)this.textRenderer.fontHeight / 2.0;
    }

    private void drawSelection(DrawContext context, int left, int top, int right, int bottom) {
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        context.fill(left, top, right, bottom, -16776961);
        RenderSystem.disableColorLogicOp();
    }

    private void onCursorChange() {
        double d = this.getScrollY();
        EditBox.Substring substring = this.editBox.getLine((int)(d / (double)this.textRenderer.fontHeight));
        if (this.editBox.getCursor() <= substring.beginIndex()) {
            d = this.editBox.getCurrentLineIndex() * this.textRenderer.fontHeight;
        } else {
            EditBox.Substring substring2 = this.editBox.getLine((int)((d + (double)this.height) / (double)this.textRenderer.fontHeight) - 1);
            if (this.editBox.getCursor() > substring2.endIndex()) {
                d = this.editBox.getCurrentLineIndex() * this.textRenderer.fontHeight - this.height + this.textRenderer.fontHeight + this.getPaddingDoubled();
            }
        }
        this.setScrollY(d);
    }

    private double getMaxLinesWithoutOverflow() {
        return (double)(this.height - this.getPaddingDoubled()) / (double)this.textRenderer.fontHeight;
    }

    private void moveCursor(double mouseX, double mouseY) {
        double d = mouseX - (double)this.getX() - (double)this.getPadding();
        double e = mouseY - (double)this.getY() - (double)this.getPadding() + this.getScrollY();
        this.editBox.moveCursor(d, e);
    }
}

