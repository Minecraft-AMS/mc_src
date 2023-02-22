/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.container;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Slot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrindstoneContainer
extends Container {
    private final Inventory resultInventory = new CraftingResultInventory();
    private final Inventory craftingInventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            GrindstoneContainer.this.onContentChanged(this);
        }
    };
    private final BlockContext context;

    public GrindstoneContainer(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }

    public GrindstoneContainer(int syncId, PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.GRINDSTONE, syncId);
        int i;
        this.context = blockContext;
        this.addSlot(new Slot(this.craftingInventory, 0, 49, 19){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.craftingInventory, 1, 49, 40){

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.hasEnchantments();
            }
        });
        this.addSlot(new Slot(this.resultInventory, 2, 129, 34){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                blockContext.run((world, blockPos) -> {
                    int j;
                    for (int i = this.getExperience((World)world); i > 0; i -= j) {
                        j = ExperienceOrbEntity.roundToOrbSize(i);
                        world.spawnEntity(new ExperienceOrbEntity((World)world, blockPos.getX(), (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, j));
                    }
                    world.playLevelEvent(1042, (BlockPos)blockPos, 0);
                });
                GrindstoneContainer.this.craftingInventory.setInvStack(0, ItemStack.EMPTY);
                GrindstoneContainer.this.craftingInventory.setInvStack(1, ItemStack.EMPTY);
                return stack;
            }

            private int getExperience(World world) {
                int i = 0;
                i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(0));
                if ((i += this.getExperience(GrindstoneContainer.this.craftingInventory.getInvStack(1))) > 0) {
                    int j = (int)Math.ceil((double)i / 2.0);
                    return j + world.random.nextInt(j);
                }
                return 0;
            }

            private int getExperience(ItemStack stack) {
                int i = 0;
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
                for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    Integer integer = entry.getValue();
                    if (enchantment.isCursed()) continue;
                    i += enchantment.getMinimumPower(integer);
                }
                return i;
            }
        });
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.craftingInventory) {
            this.updateResult();
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void updateResult() {
        boolean bl2;
        ItemStack itemStack = this.craftingInventory.getInvStack(0);
        ItemStack itemStack2 = this.craftingInventory.getInvStack(1);
        boolean bl = !itemStack.isEmpty() || !itemStack2.isEmpty();
        boolean bl3 = bl2 = !itemStack.isEmpty() && !itemStack2.isEmpty();
        if (bl) {
            ItemStack itemStack3;
            int m;
            boolean bl32;
            boolean bl4 = bl32 = !itemStack.isEmpty() && itemStack.getItem() != Items.ENCHANTED_BOOK && !itemStack.hasEnchantments() || !itemStack2.isEmpty() && itemStack2.getItem() != Items.ENCHANTED_BOOK && !itemStack2.hasEnchantments();
            if (itemStack.getCount() > 1 || itemStack2.getCount() > 1 || !bl2 && bl32) {
                this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                this.sendContentUpdates();
                return;
            }
            int i = 1;
            if (bl2) {
                if (itemStack.getItem() != itemStack2.getItem()) {
                    this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                    this.sendContentUpdates();
                    return;
                }
                Item item = itemStack.getItem();
                int j = item.getMaxDamage() - itemStack.getDamage();
                int k = item.getMaxDamage() - itemStack2.getDamage();
                int l = j + k + item.getMaxDamage() * 5 / 100;
                m = Math.max(item.getMaxDamage() - l, 0);
                itemStack3 = this.transferEnchantments(itemStack, itemStack2);
                if (!itemStack3.isDamageable()) {
                    if (!ItemStack.areEqualIgnoreDamage(itemStack, itemStack2)) {
                        this.resultInventory.setInvStack(0, ItemStack.EMPTY);
                        this.sendContentUpdates();
                        return;
                    }
                    i = 2;
                }
            } else {
                boolean bl42 = !itemStack.isEmpty();
                m = bl42 ? itemStack.getDamage() : itemStack2.getDamage();
                itemStack3 = bl42 ? itemStack : itemStack2;
            }
            this.resultInventory.setInvStack(0, this.grind(itemStack3, m, i));
        } else {
            this.resultInventory.setInvStack(0, ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    private ItemStack transferEnchantments(ItemStack target, ItemStack source) {
        ItemStack itemStack = target.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(source);
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment.isCursed() && EnchantmentHelper.getLevel(enchantment, itemStack) != 0) continue;
            itemStack.addEnchantment(enchantment, entry.getValue());
        }
        return itemStack;
    }

    private ItemStack grind(ItemStack item, int damage, int amount) {
        ItemStack itemStack = item.copy();
        itemStack.removeSubTag("Enchantments");
        itemStack.removeSubTag("StoredEnchantments");
        if (damage > 0) {
            itemStack.setDamage(damage);
        } else {
            itemStack.removeSubTag("Damage");
        }
        itemStack.setCount(amount);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(item).entrySet().stream().filter(entry -> ((Enchantment)entry.getKey()).isCursed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.set(map, itemStack);
        itemStack.setRepairCost(0);
        if (itemStack.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
            itemStack = new ItemStack(Items.BOOK);
            if (item.hasCustomName()) {
                itemStack.setCustomName(item.getName());
            }
        }
        for (int i = 0; i < map.size(); ++i) {
            itemStack.setRepairCost(AnvilContainer.getNextCost(itemStack.getRepairCost()));
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> this.dropInventory(player, (World)world, this.craftingInventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return GrindstoneContainer.canUse(this.context, player, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            ItemStack itemStack3 = this.craftingInventory.getInvStack(0);
            ItemStack itemStack4 = this.craftingInventory.getInvStack(1);
            if (invSlot == 2) {
                if (!this.insertItem(itemStack2, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot == 0 || invSlot == 1 ? !this.insertItem(itemStack2, 3, 39, false) : (itemStack3.isEmpty() || itemStack4.isEmpty() ? !this.insertItem(itemStack2, 0, 2, false) : (invSlot >= 3 && invSlot < 30 ? !this.insertItem(itemStack2, 30, 39, false) : invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack2, 3, 30, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }
}

