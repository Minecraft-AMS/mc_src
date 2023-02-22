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
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class EntryListWidget<E extends Entry<E>>
extends AbstractParentElement
implements Drawable,
Selectable {
    protected final MinecraftClient client;
    protected final int itemHeight;
    private final List<E> children = new Entries();
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    @Nullable
    private E selected;
    private boolean renderBackground = true;
    private boolean renderHorizontalShadows = true;
    @Nullable
    private E hoveredEntry;

    public EntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        this.client = client;
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
    public E getSelectedOrNull() {
        return this.selected;
    }

    public void setSelected(@Nullable E entry) {
        this.selected = entry;
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void setRenderHorizontalShadows(boolean renderHorizontalShadows) {
        this.renderHorizontalShadows = renderHorizontalShadows;
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

    protected void addEntryToTop(E entry) {
        double d = (double)this.getMaxScroll() - this.getScrollAmount();
        this.children.add(0, entry);
        this.setScrollAmount((double)this.getMaxScroll() - d);
    }

    protected boolean removeEntryWithoutScrolling(E entry) {
        double d = (double)this.getMaxScroll() - this.getScrollAmount();
        boolean bl = this.removeEntry(entry);
        this.setScrollAmount((double)this.getMaxScroll() - d);
        return bl;
    }

    protected int getEntryCount() {
        return this.children().size();
    }

    protected boolean isSelectedEntry(int index) {
        return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.left + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
        int n = m / this.itemHeight;
        if (x < (double)this.getScrollbarPositionX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount()) {
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
        return this.getEntryCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int x, int y) {
    }

    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
    }

    protected void renderBackground(MatrixStack matrices) {
    }

    protected void renderDecorations(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int o;
        int n;
        int m;
        this.renderBackground(matrices);
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        Object v0 = this.hoveredEntry;
        if (this.renderBackground) {
            RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float f = 32.0f;
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.left, this.bottom, 0.0).texture((float)this.left / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
            bufferBuilder.vertex(this.right, this.bottom, 0.0).texture((float)this.right / 32.0f, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
            bufferBuilder.vertex(this.right, this.top, 0.0).texture((float)this.right / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
            bufferBuilder.vertex(this.left, this.top, 0.0).texture((float)this.left / 32.0f, (float)(this.top + (int)this.getScrollAmount()) / 32.0f).color(32, 32, 32, 255).next();
            tessellator.draw();
        }
        int k = this.getRowLeft();
        int l = this.top + 4 - (int)this.getScrollAmount();
        if (this.renderHeader) {
            this.renderHeader(matrices, k, l, tessellator);
        }
        this.renderList(matrices, mouseX, mouseY, delta);
        if (this.renderHorizontalShadows) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(519);
            float g = 32.0f;
            m = -100;
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.left, this.top, -100.0).texture(0.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.top, -100.0).texture((float)this.width / 32.0f, (float)this.top / 32.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, 0.0, -100.0).texture((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left, 0.0, -100.0).texture(0.0f, 0.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left, this.height, -100.0).texture(0.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.height, -100.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left + this.width, this.bottom, -100.0).texture((float)this.width / 32.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
            bufferBuilder.vertex(this.left, this.bottom, -100.0).texture(0.0f, (float)this.bottom / 32.0f).color(64, 64, 64, 255).next();
            tessellator.draw();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            n = 4;
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(this.left, this.top + 4, 0.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.right, this.top + 4, 0.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.right, this.top, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.left, this.top, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.left, this.bottom, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.right, this.bottom, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.right, this.bottom - 4, 0.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.left, this.bottom - 4, 0.0).color(0, 0, 0, 0).next();
            tessellator.draw();
        }
        if ((o = this.getMaxScroll()) > 0) {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
            n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
            if (n < this.top) {
                n = this.top;
            }
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(i, this.bottom, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.bottom, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(j, this.top, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(i, this.top, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(i, n + m, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, n + m, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j, n, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, n, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i, n + m - 1, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, n + m - 1, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1, n, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(i, n, 0.0).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
        this.renderDecorations(matrices, mouseX, mouseY);
        RenderSystem.enableTexture();
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
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0.0, (double)this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
    }

    public int method_35721() {
        return (int)this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
    }

    protected int getScrollbarPositionX() {
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * (double)this.itemHeight / 2.0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 264) {
            this.moveSelection(MoveDirection.DOWN);
            return true;
        }
        if (keyCode == 265) {
            this.moveSelection(MoveDirection.UP);
            return true;
        }
        return false;
    }

    protected void moveSelection(MoveDirection direction) {
        this.moveSelectionIf(direction, entry -> true);
    }

    protected void ensureSelectedEntryVisible() {
        E entry = this.getSelectedOrNull();
        if (entry != null) {
            this.setSelected(entry);
            this.ensureVisible(entry);
        }
    }

    protected boolean moveSelectionIf(MoveDirection direction, Predicate<E> predicate) {
        int i;
        int n = i = direction == MoveDirection.UP ? -1 : 1;
        if (!this.children().isEmpty()) {
            int k;
            int j = this.children().indexOf(this.getSelectedOrNull());
            while (j != (k = MathHelper.clamp(j + i, 0, this.getEntryCount() - 1))) {
                Entry entry = (Entry)this.children().get(k);
                if (predicate.test(entry)) {
                    this.setSelected(entry);
                    this.ensureVisible(entry);
                    return true;
                }
                j = k;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
    }

    protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getRowLeft();
        int j = this.getRowWidth();
        int k = this.itemHeight - 4;
        int l = this.getEntryCount();
        for (int m = 0; m < l; ++m) {
            int n = this.getRowTop(m);
            int o = this.getRowBottom(m);
            if (o < this.top || n > this.bottom) continue;
            this.renderEntry(matrices, mouseX, mouseY, delta, m, i, n, j, k);
        }
    }

    protected void renderEntry(MatrixStack matrices, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
        E entry = this.getEntry(index);
        if (this.renderSelection && this.isSelectedEntry(index)) {
            int i = this.isFocused() ? -1 : -8355712;
            this.drawSelectionHighlight(matrices, y, entryWidth, entryHeight, i, -16777216);
        }
        ((Entry)entry).render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
    }

    protected void drawSelectionHighlight(MatrixStack matrices, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        int i = this.left + (this.width - entryWidth) / 2;
        int j = this.left + (this.width + entryWidth) / 2;
        EntryListWidget.fill(matrices, i, y - 2, j, y + entryHeight + 2, borderColor);
        EntryListWidget.fill(matrices, i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
    }

    public int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int index) {
        return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    private int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    protected boolean isFocused() {
        return false;
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        if (this.hoveredEntry != null) {
            return Selectable.SelectionType.HOVERED;
        }
        return Selectable.SelectionType.NONE;
    }

    @Nullable
    protected E remove(int index) {
        Entry entry = (Entry)this.children.get(index);
        if (this.removeEntry((Entry)this.children.get(index))) {
            return (E)entry;
        }
        return null;
    }

    protected boolean removeEntry(E entry) {
        boolean bl = this.children.remove(entry);
        if (bl && entry == this.getSelectedOrNull()) {
            this.setSelected(null);
        }
        return bl;
    }

    @Nullable
    protected E getHoveredEntry() {
        return this.hoveredEntry;
    }

    void setEntryParentList(Entry<E> entry) {
        entry.parentList = this;
    }

    protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
        int i;
        List<E> list = this.children();
        if (list.size() > 1 && (i = list.indexOf(entry)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.list", i + 1, list.size()));
        }
    }

    @Override
    @Nullable
    public /* synthetic */ Element getFocused() {
        return this.getFocused();
    }

    @Environment(value=EnvType.CLIENT)
    class Entries
    extends AbstractList<E> {
        private final List<E> entries = Lists.newArrayList();

        Entries() {
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
            EntryListWidget.this.setEntryParentList(entry);
            return entry2;
        }

        @Override
        public void add(int i, E entry) {
            this.entries.add(i, entry);
            EntryListWidget.this.setEntryParentList(entry);
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
        EntryListWidget<E> parentList;

        public abstract void render(MatrixStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class MoveDirection
    extends Enum<MoveDirection> {
        public static final /* enum */ MoveDirection UP = new MoveDirection();
        public static final /* enum */ MoveDirection DOWN = new MoveDirection();
        private static final /* synthetic */ MoveDirection[] field_25663;

        public static MoveDirection[] values() {
            return (MoveDirection[])field_25663.clone();
        }

        public static MoveDirection valueOf(String string) {
            return Enum.valueOf(MoveDirection.class, string);
        }

        private static /* synthetic */ MoveDirection[] method_36869() {
            return new MoveDirection[]{UP, DOWN};
        }

        static {
            field_25663 = MoveDirection.method_36869();
        }
    }
}

