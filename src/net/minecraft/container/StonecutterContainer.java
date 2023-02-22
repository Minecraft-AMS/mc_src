/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer
extends Container {
    static final ImmutableList<Item> INGREDIENTS = ImmutableList.of((Object)Items.STONE, (Object)Items.SANDSTONE, (Object)Items.RED_SANDSTONE, (Object)Items.QUARTZ_BLOCK, (Object)Items.COBBLESTONE, (Object)Items.STONE_BRICKS, (Object)Items.BRICKS, (Object)Items.NETHER_BRICKS, (Object)Items.RED_NETHER_BRICKS, (Object)Items.PURPUR_BLOCK, (Object)Items.PRISMARINE, (Object)Items.PRISMARINE_BRICKS, (Object[])new Item[]{Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE});
    private final BlockContext context;
    private final Property selectedRecipe = Property.create();
    private final World world;
    private List<StonecuttingRecipe> availableRecipes = Lists.newArrayList();
    private ItemStack inputStack = ItemStack.EMPTY;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener = () -> {};
    public final Inventory inventory = new BasicInventory(1){

        @Override
        public void markDirty() {
            super.markDirty();
            StonecutterContainer.this.onContentChanged(this);
            StonecutterContainer.this.contentsChangedListener.run();
        }
    };
    private final CraftingResultInventory field_19173 = new CraftingResultInventory();

    public StonecutterContainer(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, BlockContext.EMPTY);
    }

    public StonecutterContainer(int syncId, PlayerInventory playerInventory, final BlockContext blockContext) {
        super(ContainerType.STONECUTTER, syncId);
        int i;
        this.context = blockContext;
        this.world = playerInventory.player.world;
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.field_19173, 1, 143, 33){

            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                ItemStack itemStack = StonecutterContainer.this.inputSlot.takeStack(1);
                if (!itemStack.isEmpty()) {
                    StonecutterContainer.this.populateResult();
                }
                stack.getItem().onCraft(stack, player.world, player);
                blockContext.run((world, blockPos) -> {
                    long l = world.getTime();
                    if (StonecutterContainer.this.lastTakeTime != l) {
                        world.playSound(null, (BlockPos)blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        StonecutterContainer.this.lastTakeTime = l;
                    }
                });
                return super.onTakeItem(player, stack);
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
        this.addProperty(this.selectedRecipe);
    }

    @Environment(value=EnvType.CLIENT)
    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    @Environment(value=EnvType.CLIENT)
    public List<StonecuttingRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return StonecutterContainer.canUse(this.context, player, Blocks.STONECUTTER);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            this.selectedRecipe.set(id);
            this.populateResult();
        }
        return true;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        if (itemStack.getItem() != this.inputStack.getItem()) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory inventory, ItemStack itemStack) {
        this.availableRecipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.setStack(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, inventory, this.world);
        }
    }

    private void populateResult() {
        if (!this.availableRecipes.isEmpty()) {
            StonecuttingRecipe stonecuttingRecipe = this.availableRecipes.get(this.selectedRecipe.get());
            this.outputSlot.setStack(stonecuttingRecipe.craft(this.inventory));
        } else {
            this.outputSlot.setStack(ItemStack.EMPTY);
        }
        this.sendContentUpdates();
    }

    @Override
    public ContainerType<?> getType() {
        return ContainerType.STONECUTTER;
    }

    @Environment(value=EnvType.CLIENT)
    public void setContentsChangedListener(Runnable runnable) {
        this.contentsChangedListener = runnable;
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
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (invSlot == 1) {
                item.onCraft(itemStack2, player.world, player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onStackChanged(itemStack2, itemStack);
            } else if (invSlot == 0 ? !this.insertItem(itemStack2, 2, 38, false) : (INGREDIENTS.contains((Object)item) ? !this.insertItem(itemStack2, 0, 1, false) : (invSlot >= 2 && invSlot < 29 ? !this.insertItem(itemStack2, 29, 38, false) : invSlot >= 29 && invSlot < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.field_19173.removeInvStack(1);
        this.context.run((world, blockPos) -> this.dropInventory(player, playerEntity.world, this.inventory));
    }
}
