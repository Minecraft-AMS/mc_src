/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Clearable;

public class JukeboxBlockEntity
extends BlockEntity
implements Clearable {
    private ItemStack record = ItemStack.EMPTY;

    public JukeboxBlockEntity() {
        super(BlockEntityType.JUKEBOX);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("RecordItem", 10)) {
            this.setRecord(ItemStack.fromTag(tag.getCompound("RecordItem")));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.getRecord().isEmpty()) {
            tag.put("RecordItem", this.getRecord().toTag(new CompoundTag()));
        }
        return tag;
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack itemStack) {
        this.record = itemStack;
        this.markDirty();
    }

    @Override
    public void clear() {
        this.setRecord(ItemStack.EMPTY);
    }
}

