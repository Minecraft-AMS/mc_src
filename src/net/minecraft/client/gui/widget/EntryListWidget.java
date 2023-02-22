/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntryListWidget<E extends Entry<E>>
extends AbstractParentElement
implements Drawable {
    protected static final int DRAG_OUTSIDE = -2;
    protected final MinecraftClient minecraft;
    protected final int itemHeight;
    private final List<E> children = new Entries();
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically = true;
    protected int yDrag = -2;
    private double scrollAmount;
    protected boolean renderSelection = true;
    protected boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    private E selected;

    public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this.minecraft = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.itemHeight = itemHeight;
        this.left = 0;
        this.right = width;
    }

    public void setRenderSelection(boolean renderSelection) {
        this.renderSelection = renderSelection;
    }

    protected void setRenderHeader(boolean renderHeader, int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = headerHeight;
        if (!renderHeader) {
            this.headerHeight = 0;
        }
    }

    public int getRowWidth() {
        return 220;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
    }

    @Nullable
    public E getFocused() {
        return (E)((Entry)super.getFocused());
    }

    public final List<E> children() {
        return this.children;
    }

    protected final void clearEntries() {
        this.children.clear();
    }

    protected void replaceEntries(Collection<E> newEntries) {
        this.children.clear();
        this.children.addAll(newEntries);
    }

    protected E getEntry(int index) {
        return (E)((Entry)this.children().get(index));
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean isSelectedItem(int index) {
        return Objects.equals(this.getSelected(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (x < (double)this.getScrollbarPosition() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getItemCount()) {
            return (E)((Entry)this.children().get(n));
        }
        return null;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }

    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int x, int y) {
    }

    protected void renderHeader(int x, int y, Tessellator tessellator) {
    }

    protected void renderBackground() {
    }

    protected void renderDecorations(int mouseX, int mouseY) {
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        int i = this.getScrollbarPosition();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
        tessellator.draw();
        int k = this.getRowLeft();
        int l = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(k, l, tessellator);
        }
        this.renderList(k, l, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        this.renderHoleBackground(0, this.top, 255, 255);
        this.renderHoleBackground(this.bottom, this.height, 255, 255);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(7425);
        RenderSystem.disableTexture();
        int m = 4;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.top + 4, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top + 4, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
        tessellator.draw();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom - 4, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.left, this.bottom - 4, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 0).next();
        tessellator.draw();
        int n = this.getMaxScroll();
        if (n > 0) {
            int o = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            o = MathHelper.clamp(o, 32, this.bottom - this.top - 8);
            int p = (int)this.getScrollAmount() * (this.bottom - this.top - o) / n + this.top;
            if (p < this.top) {
                p = this.top;
            }
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, this.bottom, 0.0).texture(0.0f, 1.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.bottom, 0.0).texture(1.0f, 1.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.top, 0.0).texture(1.0f, 0.0f).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(i, this.top, 0.0).texture(0.0f, 0.0f).color(0, 0, 0, 255).next();
            tessellator.draw();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, p + o, 0.0).texture(0.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, p + o, 0.0).texture(1.0f, 1.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, p, 0.0).texture(1.0f, 0.0f).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, p, 0.0).texture(0.0f, 0.0f).color(128, 128, 128, 255).next();
            tessellator.draw();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(i, p + o - 1, 0.0).texture(0.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, p + o - 1, 0.0).texture(1.0f, 1.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, p, 0.0).texture(1.0f, 0.0f).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(i, p, 0.0).texture(0.0f, 0.0f).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
        this.renderDecorations(mouseX, mouseY);
        RenderSystem.enableTexture();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    protected void centerScrollOn(E entry) {
        this.setScrollAmount(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - (this.bottom - this.top) / 2);
    }

    protected void ensureVisible(E entry) {
        int k;
        int i = this.getRowTop(this.children().indexOf(entry));
        int j = i - this.top - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }
        if ((k = this.bottom - i - this.itemHeight - this.itemHeight) < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double)amount);
        this.yDrag = -2;
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
    }

    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public int getScrollBottom() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double d, double e, int i) {
        this.scrolling = i == 0 && d >= (double)this.getScrollbarPosition() && d < (double)(this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }
        E entry = this.getEntryAtPosition(mouseX, mouseY);
        if (entry != null) {
            if (entry.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused((Element)entry);
                this.setDragging(true);
                return true;
            }
        } else if (button == 0) {
            this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
            return true;
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null) {
            this.getFocused().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (button != 0 || !this.scrolling) {
            return false;
        }
        if (mouseY < (double)this.top) {
            this.setScrollAmount(0.0);
        } else if (mouseY > (double)this.bottom) {
            this.setScrollAmount(this.getMaxScroll());
        } else {
            double d = Math.max(1, this.getMaxScroll());
            int i = this.bottom - this.top;
            int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
            double e = Math.max(1.0, d / (double)(i - j));
            this.setScrollAmount(this.getScrollAmount() + deltaY * e);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 264) {
            this.moveSelection(1);
            return true;
        }
        if (keyCode == 265) {
            this.moveSelection(-1);
            return true;
        }
        return false;
    }

    protected void moveSelection(int amount) {
        if (!this.children().isEmpty()) {
            int i = this.children().indexOf(this.getSelected());
            int j = MathHelper.clamp(i + amount, 0, this.getItemCount() - 1);
            Entry entry = (Entry)this.children().get(j);
            this.setSelected(entry);
            this.ensureVisible(entry);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
    }

    protected void renderList(int x, int y, int mouseX, int mouseY, float delta) {
        int i = this.getItemCount();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        for (int j = 0; j < i; ++j) {
            int p;
            int k = this.getRowTop(j);
            int l = this.getRowBottom(j);
            if (l < this.top || k > this.bottom) continue;
            int m = y + j * this.itemHeight + this.headerHeight;
            int n = this.itemHeight - 4;
            E entry = this.getEntry(j);
            int o = this.getRowWidth();
            if (this.renderSelection && this.isSelectedItem(j)) {
                p = this.left + this.width / 2 - o / 2;
                int q = this.left + this.width / 2 + o / 2;
                RenderSystem.disableTexture();
                float f = this.isFocused() ? 1.0f : 0.5f;
                RenderSystem.color4f(f, f, f, 1.0f);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex(p, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m + n + 2, 0.0).next();
                bufferBuilder.vertex(q, m - 2, 0.0).next();
                bufferBuilder.vertex(p, m - 2, 0.0).next();
                tessellator.draw();
                RenderSystem.color4f(0.0f, 0.0f, 0.0f, 1.0f);
                bufferBuilder.begin(7, VertexFormats.POSITION);
                bufferBuilder.vertex(p + 1, m + n + 1, 0.0).next();
                bufferBuilder.vertex(q - 1, m + n + 1, 0.0).next();
                bufferBuilder.vertex(q - 1, m - 1, 0.0).next();
                bufferBuilder.vertex(p + 1, m - 1, 0.0).next();
                tessellator.draw();
                RenderSystem.enableTexture();
            }
            p = this.getRowLeft();
            ((Entry)entry).render(j, k, p, o, n, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPosition(mouseX, mouseY), entry), delta);
        }
    }

    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    protected int getRowTop(int index) {
        return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    private int getRowBottom(int i) {
        return this.getRowTop(i) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    protected void renderHoleBackground(int top, int bottom, int alphaTop, int alphaBottom) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(DrawableHelper.BACKGROUND_LOCATION);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, bottom, 0.0).texture(0.0f, (float)bottom / 32.0f).color(64, 64, 64, alphaBottom).next();
        bufferBuilder.vertex(this.left + this.width, bottom, 0.0).texture((float)this.width / 32.0f, (float)bottom / 32.0f).color(64, 64, 64, alphaBottom).next();
        bufferBuilder.vertex(this.left + this.width, top, 0.0).texture((float)this.width / 32.0f, (float)top / 32.0f).color(64, 64, 64, alphaTop).next();
        bufferBuilder.vertex(this.left, top, 0.0).texture(0.0f, (float)top / 32.0f).color(64, 64, 64, alphaTop).next();
        tessellator.draw();
    }

    protected E remove(int index) {
        Entry entry = (Entry)this.children.get(index);
        if (this.removeEntry((Entry)this.children.get(index))) {
            return (E)entry;
        }
        return null;
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelected()) {
            this.setSelected(null);
        }
        return bl;
    }

    @Environment(value=EnvType.CLIENT)
    class Entries
    extends AbstractList<E> {
        private final List<E> entries = Lists.newArrayList();

        private Entries() {
        }

        @Override
        public E get(int i) {
            return (Entry)this.entries.get(i);
        }

        @Override
        public int size() {
            return this.entries.size();
        }

        @Override
        public E set(int i, E entry) {
            Entry entry2 = (Entry)this.entries.set(i, entry);
            ((Entry)entry).list = EntryListWidget.this;
            return entry2;
        }

        @Override
        public void add(int i, E entry) {
            this.entries.add(i, entry);
            ((Entry)entry).list = EntryListWidget.this;
        }

        @Override
        public E remove(int i) {
            return (Entry)this.entries.remove(i);
        }

        @Override
        public /* synthetic */ Object remove(int index) {
            return this.remove(index);
        }

        @Override
        public /* synthetic */ void add(int index, Object entry) {
            this.add(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object set(int index, Object entry) {
            return this.set(index, (E)((Entry)entry));
        }

        @Override
        public /* synthetic */ Object get(int index) {
            return this.get(index);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    implements Element {
        @Deprecated
        EntryListWidget<E> list;

        public abstract void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9);

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
        }
    }
}

