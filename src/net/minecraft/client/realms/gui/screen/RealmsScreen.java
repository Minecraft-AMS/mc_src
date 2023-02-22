/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.util.NarratorManager;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreen
extends Screen {
    public RealmsScreen() {
        super(NarratorManager.EMPTY);
    }

    protected static int row(int index) {
        return 40 + index * 13;
    }

    @Override
    public void tick() {
        for (ClickableWidget clickableWidget : this.buttons) {
            if (!(clickableWidget instanceof TickableElement)) continue;
            ((TickableElement)((Object)clickableWidget)).tick();
        }
    }

    public void narrateLabels() {
        List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
        Realms.narrateNow(list);
    }
}
