/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class AbstractItemTagProvider
extends ValueLookupTagProvider<Item> {
    private final Function<TagKey<Block>, TagBuilder> blockTags = blockTagProvider::getTagBuilder;

    public AbstractItemTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, AbstractTagProvider<Block> blockTagProvider) {
        super(output, RegistryKeys.ITEM, registryLookupFuture, item -> item.getRegistryEntry().registryKey());
    }

    protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        TagBuilder tagBuilder = this.getTagBuilder(itemTag);
        TagBuilder tagBuilder2 = this.blockTags.apply(blockTag);
        tagBuilder2.build().forEach(tagBuilder::add);
    }
}

