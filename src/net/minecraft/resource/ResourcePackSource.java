/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
    public static final ResourcePackSource field_25347 = ResourcePackSource.method_29485();
    public static final ResourcePackSource PACK_SOURCE_BUILTIN = ResourcePackSource.method_29486("pack.source.builtin");
    public static final ResourcePackSource PACK_SOURCE_WORLD = ResourcePackSource.method_29486("pack.source.world");
    public static final ResourcePackSource PACK_SOURCE_SERVER = ResourcePackSource.method_29486("pack.source.server");

    public Text decorate(Text var1);

    public static ResourcePackSource method_29485() {
        return text -> text;
    }

    public static ResourcePackSource method_29486(String string) {
        TranslatableText text = new TranslatableText(string);
        return text2 -> new TranslatableText("pack.nameAndSource", text2, text).formatted(Formatting.GRAY);
    }
}

