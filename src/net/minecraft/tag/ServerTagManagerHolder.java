/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.tag;

import java.util.stream.Collectors;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;

public class ServerTagManagerHolder {
    private static volatile TagManager tagManager = TagManager.create(TagGroup.create(BlockTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(ItemTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(FluidTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))), TagGroup.create(EntityTypeTags.getRequiredTags().stream().collect(Collectors.toMap(Tag.Identified::getId, identified -> identified))));

    public static TagManager getTagManager() {
        return tagManager;
    }

    public static void setTagManager(TagManager tagManager) {
        ServerTagManagerHolder.tagManager = tagManager;
    }
}

