/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(value=EnvType.CLIENT)
public class BundleTooltipComponent
implements TooltipComponent {
    public static final Identifier TEXTURE = new Identifier("textures/gui/container/bundle.png");
    private static final int field_32381 = 4;
    private static final int field_32382 = 1;
    private static final int field_32383 = 128;
    private static final int field_32384 = 18;
    private static final int field_32385 = 20;
    private final DefaultedList<ItemStack> inventory;
    private final int occupancy;

    public BundleTooltipComponent(BundleTooltipData data) {
        this.inventory = data.getInventory();
        this.occupancy = data.getBundleOccupancy();
    }

    @Override
    public int getHeight() {
        return this.getRows() * 20 + 2 + 4;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.getColumns() * 18 + 2;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        int i = this.getColumns();
        int j = this.getRows();
        boolean bl = this.occupancy >= 64;
        int k = 0;
        for (int l = 0; l < j; ++l) {
            for (int m = 0; m < i; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.drawSlot(n, o, k++, bl, textRenderer, matrices, itemRenderer, z);
            }
        }
        this.drawOutline(x, y, i, j, matrices, z);
    }

    private void drawSlot(int x, int y, int index, boolean shouldBlock, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        if (index >= this.inventory.size()) {
            this.draw(matrices, x, y, z, shouldBlock ? Sprite.BLOCKED_SLOT : Sprite.SLOT);
            return;
        }
        ItemStack itemStack = this.inventory.get(index);
        this.draw(matrices, x, y, z, Sprite.SLOT);
        itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1, index);
        itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1);
        if (index == 0) {
            HandledScreen.drawSlotHighlight(matrices, x + 1, y + 1, z);
        }
    }

    private void drawOutline(int x, int y, int columns, int rows, MatrixStack matrices, int z) {
        int i;
        this.draw(matrices, x, y, z, Sprite.BORDER_CORNER_TOP);
        this.draw(matrices, x + columns * 18 + 1, y, z, Sprite.BORDER_CORNER_TOP);
        for (i = 0; i < columns; ++i) {
            this.draw(matrices, x + 1 + i * 18, y, z, Sprite.BORDER_HORIZONTAL_TOP);
            this.draw(matrices, x + 1 + i * 18, y + rows * 20, z, Sprite.BORDER_HORIZONTAL_BOTTOM);
        }
        for (i = 0; i < rows; ++i) {
            this.draw(matrices, x, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
            this.draw(matrices, x + columns * 18 + 1, y + i * 20 + 1, z, Sprite.BORDER_VERTICAL);
        }
        this.draw(matrices, x, y + rows * 20, z, Sprite.BORDER_CORNER_BOTTOM);
        this.draw(matrices, x + columns * 18 + 1, y + rows * 20, z, Sprite.BORDER_CORNER_BOTTOM);
    }

    private void draw(MatrixStack matrices, int x, int y, int z, Sprite sprite) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, z, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
    }

    private int getColumns() {
        return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.inventory.size() + 1.0)));
    }

    private int getRows() {
        return (int)Math.ceil(((double)this.inventory.size() + 1.0) / (double)this.getColumns());
    }

    @Environment(value=EnvType.CLIENT)
    static final class Sprite
    extends Enum<Sprite> {
        public static final /* enum */ Sprite SLOT = new Sprite(0, 0, 18, 20);
        public static final /* enum */ Sprite BLOCKED_SLOT = new Sprite(0, 40, 18, 20);
        public static final /* enum */ Sprite BORDER_VERTICAL = new Sprite(0, 18, 1, 20);
        public static final /* enum */ Sprite BORDER_HORIZONTAL_TOP = new Sprite(0, 20, 18, 1);
        public static final /* enum */ Sprite BORDER_HORIZONTAL_BOTTOM = new Sprite(0, 60, 18, 1);
        public static final /* enum */ Sprite BORDER_CORNER_TOP = new Sprite(0, 20, 1, 1);
        public static final /* enum */ Sprite BORDER_CORNER_BOTTOM = new Sprite(0, 60, 1, 1);
        public final int u;
        public final int v;
        public final int width;
        public final int height;
        private static final /* synthetic */ Sprite[] field_28372;

        public static Sprite[] values() {
            return (Sprite[])field_28372.clone();
        }

        public static Sprite valueOf(String string) {
            return Enum.valueOf(Sprite.class, string);
        }

        private Sprite(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }

        private static /* synthetic */ Sprite[] method_36887() {
            return new Sprite[]{SLOT, BLOCKED_SLOT, BORDER_VERTICAL, BORDER_HORIZONTAL_TOP, BORDER_HORIZONTAL_BOTTOM, BORDER_CORNER_TOP, BORDER_CORNER_BOTTOM};
        }

        static {
            field_28372 = Sprite.method_36887();
        }
    }
}

