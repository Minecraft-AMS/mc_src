/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tag;

import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.TagManager;

public class ServerTagManagerHolder {
    private static volatile TagManager tagManager = RequiredTagListRegistry.createBuiltinTagManager();

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static void setTagManager(TagManager tagManager) {
        ServerTagManagerHolder.tagManager = tagManager;
    }
}

