/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2i
 *  org.joml.Vector2ic
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.joml.Vector2i;
import org.joml.Vector2ic;

@Environment(value=EnvType.CLIENT)
public class HoveredTooltipPositioner
implements TooltipPositioner {
    public static final TooltipPositioner INSTANCE = new HoveredTooltipPositioner();

    private HoveredTooltipPositioner() {
    }

    @Override
    public Vector2ic getPosition(Screen screen, int x, int y, int width, int height) {
        Vector2i vector2i = new Vector2i(x, y).add(12, -12);
        this.preventOverflow(screen, vector2i, width, height);
        return vector2i;
    }

    private void preventOverflow(Screen screen, Vector2i pos, int width, int height) {
        int i;
        if (pos.x + width > screen.width) {
            pos.x = Math.max(pos.x - 24 - width, 4);
        }
        if (pos.y + (i = height + 3) > screen.height) {
            pos.y = screen.height - i;
        }
    }
}

