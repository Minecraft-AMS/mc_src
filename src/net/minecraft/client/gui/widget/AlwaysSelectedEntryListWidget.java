/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    private static final Text SELECTION_USAGE_TEXT = new TranslatableText("narration.selection.usage");
    private boolean inFocus;

    public AlwaysSelectedEntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        if (!this.inFocus && this.getEntryCount() == 0) {
            return false;
        }
        boolean bl = this.inFocus = !this.inFocus;
        if (this.inFocus && this.getSelectedOrNull() == null && this.getEntryCount() > 0) {
            this.moveSelection(EntryListWidget.MoveDirection.DOWN);
        } else if (this.inFocus && this.getSelectedOrNull() != null) {
            this.ensureSelectedEntryVisible();
        }
        return this.inFocus;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        Entry entry = (Entry)this.getHoveredEntry();
        if (entry != null) {
            this.appendNarrations(builder.nextMessage(), entry);
            entry.appendNarrations(builder);
        } else {
            Entry entry2 = (Entry)this.getSelectedOrNull();
            if (entry2 != null) {
                this.appendNarrations(builder.nextMessage(), entry2);
                entry2.appendNarrations(builder);
            }
        }
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    extends EntryListWidget.Entry<E>
    implements Narratable {
        @Override
        public boolean changeFocus(boolean lookForwards) {
            return false;
        }

        public abstract Text getNarration();

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            builder.put(NarrationPart.TITLE, this.getNarration());
        }
    }
}

