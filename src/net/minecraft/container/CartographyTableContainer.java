/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.container;

import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class CartographyTableContainer
extends Container {
    private final BlockContext context;
    private boolean currentlyTakingItem;
    public final Inventory inventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            CartographyTableContainer.this.onContentChanged(this);
            super.markDirty();
        }
    };
    private final CraftingResultInventory resultSlot = new CraftingResultInventory(){

        @Override
        public void markDirty() {
            CartographyTableContainer.this.onContentChanged(this);
            super.markDirty();
        }
    };

    public CartographyTableContainer(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, BlockContext.EMPTY);
    }

    public CartographyTableContainer(int syncId, PlayerInventory inventory, final BlockContext context) {
        super(ContainerType.CARTOGRAPHY, syncId);
        int i;
        this.context = context;
        this.addSlot(new Slot(this.inventory, 0, 15, 15){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == Items.FILLED_MAP;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 15, 52){

            @Override
            public boolean canInsert(ItemStack stack) {
                Item item = stack.getItem();
                return item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE;
            }
        });
        this.addSlot(new Slot(this.resultSlot, 2, 145, 39){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack itemStack = super.takeStack(amount);
                ItemStack itemStack2 = context.run((world, blockPos) -> {
                    ItemStack itemStack2;
                    if (!CartographyTableContainer.this.currentlyTakingItem && CartographyTableContainer.this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE && (itemStack2 = FilledMapItem.copyMap(world, CartographyTableContainer.this.inventory.getInvStack(0))) != null) {
                        itemStack2.setCount(1);
                        return itemStack2;
                    }
                    return itemStack;
                }).orElse(itemStack);
                CartographyTableContainer.this.inventory.takeInvStack(0, 1);
                CartographyTableContainer.this.inventory.takeInvStack(1, 1);
                return itemStack2;
            }

            @Override
            protected void onCrafted(ItemStack stack, int amount) {
                this.takeStack(amount);
                super.onCrafted(stack, amount);
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.getItem().onCraft(stack, player.world, player);
                context.run((world, blockPos) -> world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f));
                return super.onTakeItem(player, stack);
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return CartographyTableContainer.canUse(this.context, player, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inventory.getInvStack(0);
        ItemStack itemStack2 = this.inventory.getInvStack(1);
        ItemStack itemStack3 = this.resultSlot.getInvStack(2);
        if (!itemStack3.isEmpty() && (itemStack.isEmpty() || itemStack2.isEmpty())) {
            this.resultSlot.removeInvStack(2);
        } else if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
            this.updateResult(itemStack, itemStack2, itemStack3);
        }
    }

    private void updateResult(ItemStack map, ItemStack item, ItemStack oldResult) {
        this.context.run((world, blockPos) -> {
            ItemStack itemStack4;
            Item item = item.getItem();
            MapState mapState = FilledMapItem.getMapState(map, world);
            if (mapState == null) {
                return;
            }
            if (item == Items.PAPER && !mapState.locked && mapState.scale < 4) {
                itemStack4 = map.copy();
                itemStack4.setCount(1);
                itemStack4.getOrCreateTag().putInt("map_scale_direction", 1);
                this.sendContentUpdates();
            } else if (item == Items.GLASS_PANE && !mapState.locked) {
                itemStack4 = map.copy();
                itemStack4.setCount(1);
                this.sendContentUpdates();
            } else if (item == Items.MAP) {
                itemStack4 = map.copy();
                itemStack4.setCount(2);
                this.sendContentUpdates();
            } else {
                this.resultSlot.removeInvStack(2);
                this.sendContentUpdates();
                return;
            }
            if (!ItemStack.areEqualIgnoreDamage(itemStack4, oldResult)) {
                this.resultSlot.setInvStack(2, itemStack4);
                this.sendContentUpdates();
            }
        });
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return false;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2;
            ItemStack itemStack3 = itemStack2 = slot.getStack();
            Item item = itemStack3.getItem();
            itemStack = itemStack3.copy();
            if (invSlot == 2) {
                if (this.inventory.getInvStack(1).getItem() == Items.GLASS_PANE) {
                    itemStack3 = this.context.run((world, blockPos) -> {
                        ItemStack itemStack2 = FilledMapItem.copyMap(world, this.inventory.getInvStack(0));
                        if (itemStack2 != null) {
                            itemStack2.setCount(1);
                            return itemStack2;
                        }
                        return itemStack2;
                    }).orElse(itemStack3);
                }
                item.onCraft(itemStack3, player.world, player);
                if (!this.insertItem(itemStack3, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack3, itemStack);
            } else if (invSlot == 1 || invSlot == 0 ? !this.insertItem(itemStack3, 3, 39, false) : (item == Items.FILLED_MAP ? !this.insertItem(itemStack3, 0, 1, false) : (item == Items.PAPER || item == Items.MAP || item == Items.GLASS_PANE ? !this.insertItem(itemStack3, 1, 2, false) : (invSlot >= 3 && invSlot < 30 ? !this.insertItem(itemStack3, 30, 39, false) : invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack3, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if (itemStack3.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack3.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            this.currentlyTakingItem = true;
            slot.onTakeItem(player, itemStack3);
            this.currentlyTakingItem = false;
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.resultSlot.removeInvStack(2);
        this.context.run((world, blockPos) -> this.dropInventory(player, playerEntity.world, this.inventory));
    }
}
