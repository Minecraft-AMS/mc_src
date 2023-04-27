/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
final class AdvancementTabType
extends Enum<AdvancementTabType> {
    public static final /* enum */ AdvancementTabType ABOVE = new AdvancementTabType(0, 0, 28, 32, 8);
    public static final /* enum */ AdvancementTabType BELOW = new AdvancementTabType(84, 0, 28, 32, 8);
    public static final /* enum */ AdvancementTabType LEFT = new AdvancementTabType(0, 64, 32, 28, 5);
    public static final /* enum */ AdvancementTabType RIGHT = new AdvancementTabType(96, 64, 32, 28, 5);
    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final int tabCount;
    private static final /* synthetic */ AdvancementTabType[] field_2676;

    public static AdvancementTabType[] values() {
        return (AdvancementTabType[])field_2676.clone();
    }

    public static AdvancementTabType valueOf(String string) {
        return Enum.valueOf(AdvancementTabType.class, string);
    }

    private AdvancementTabType(int u, int v, int width, int height, int tabCount) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.tabCount = tabCount;
    }

    public int getTabCount() {
        return this.tabCount;
    }

    public void drawBackground(DrawContext context, int x, int y, boolean selected, int index) {
        int i = this.u;
        if (index > 0) {
            i += this.width;
        }
        if (index == this.tabCount - 1) {
            i += this.width;
        }
        int j = selected ? this.v + this.height : this.v;
        context.drawTexture(AdvancementsScreen.TABS_TEXTURE, x + this.getTabX(index), y + this.getTabY(index), i, j, this.width, this.height);
    }

    public void drawIcon(DrawContext context, int x, int y, int index, ItemStack stack) {
        int i = x + this.getTabX(index);
        int j = y + this.getTabY(index);
        switch (this) {
            case ABOVE: {
                i += 6;
                j += 9;
                break;
            }
            case BELOW: {
                i += 6;
                j += 6;
                break;
            }
            case LEFT: {
                i += 10;
                j += 5;
                break;
            }
            case RIGHT: {
                i += 6;
                j += 5;
            }
        }
        context.drawItemWithoutEntity(stack, i, j);
    }

    public int getTabX(int index) {
        switch (this) {
            case ABOVE: {
                return (this.width + 4) * index;
            }
            case BELOW: {
                return (this.width + 4) * index;
            }
            case LEFT: {
                return -this.width + 4;
            }
            case RIGHT: {
                return 248;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
    }

    public int getTabY(int index) {
        switch (this) {
            case ABOVE: {
                return -this.height + 4;
            }
            case BELOW: {
                return 136;
            }
            case LEFT: {
                return this.height * index;
            }
            case RIGHT: {
                return this.height * index;
            }
        }
        throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
    }

    public boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
        int i = screenX + this.getTabX(index);
        int j = screenY + this.getTabY(index);
        return mouseX > (double)i && mouseX < (double)(i + this.width) && mouseY > (double)j && mouseY < (double)(j + this.height);
    }

    private static /* synthetic */ AdvancementTabType[] method_36883() {
        return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
    }

    static {
        field_2676 = AdvancementTabType.method_36883();
    }
}

