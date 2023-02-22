/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.recipe;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class InputSlotFiller<C extends Inventory>
implements RecipeGridAligner<Integer> {
    protected static final Logger LOGGER = LogManager.getLogger();
    protected final RecipeMatcher matcher = new RecipeMatcher();
    protected PlayerInventory inventory;
    protected AbstractRecipeScreenHandler<C> handler;

    public InputSlotFiller(AbstractRecipeScreenHandler<C> abstractRecipeScreenHandler) {
        this.handler = abstractRecipeScreenHandler;
    }

    public void fillInputSlots(ServerPlayerEntity entity, @Nullable Recipe<C> recipe, boolean craftAll) {
        if (recipe == null || !entity.getRecipeBook().contains(recipe)) {
            return;
        }
        this.inventory = entity.inventory;
        if (!this.canReturnInputs() && !entity.isCreative()) {
            return;
        }
        this.matcher.clear();
        entity.inventory.populateRecipeFinder(this.matcher);
        this.handler.populateRecipeFinder(this.matcher);
        if (this.matcher.match(recipe, null)) {
            this.fillInputSlots(recipe, craftAll);
        } else {
            this.returnInputs();
            entity.networkHandler.sendPacket(new CraftFailedResponseS2CPacket(entity.currentScreenHandler.syncId, recipe));
        }
        entity.inventory.markDirty();
    }

    protected void returnInputs() {
        for (int i = 0; i < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; ++i) {
            if (i == this.handler.getCraftingResultSlotIndex() && (this.handler instanceof CraftingScreenHandler || this.handler instanceof PlayerScreenHandler)) continue;
            this.returnSlot(i);
        }
        this.handler.clearCraftingSlots();
    }

    protected void returnSlot(int i) {
        ItemStack itemStack = this.handler.getSlot(i).getStack();
        if (itemStack.isEmpty()) {
            return;
        }
        while (itemStack.getCount() > 0) {
            int j = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
            if (j == -1) {
                j = this.inventory.getEmptySlot();
            }
            ItemStack itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            if (!this.inventory.insertStack(j, itemStack2)) {
                LOGGER.error("Can't find any space for item in the inventory");
            }
            this.handler.getSlot(i).takeStack(1);
        }
    }

    protected void fillInputSlots(Recipe<C> recipe, boolean craftAll) {
        IntArrayList intList;
        int j;
        boolean bl = this.handler.matches(recipe);
        int i = this.matcher.countCrafts(recipe, null);
        if (bl) {
            for (j = 0; j < this.handler.getCraftingHeight() * this.handler.getCraftingWidth() + 1; ++j) {
                ItemStack itemStack;
                if (j == this.handler.getCraftingResultSlotIndex() || (itemStack = this.handler.getSlot(j).getStack()).isEmpty() || Math.min(i, itemStack.getMaxCount()) >= itemStack.getCount() + 1) continue;
                return;
            }
        }
        if (this.matcher.match(recipe, (IntList)(intList = new IntArrayList()), j = this.getAmountToFill(craftAll, i, bl))) {
            int k = j;
            IntListIterator intListIterator = intList.iterator();
            while (intListIterator.hasNext()) {
                int l = (Integer)intListIterator.next();
                int m = RecipeMatcher.getStackFromId(l).getMaxCount();
                if (m >= k) continue;
                k = m;
            }
            j = k;
            if (this.matcher.match(recipe, (IntList)intList, j)) {
                this.returnInputs();
                this.alignRecipeToGrid(this.handler.getCraftingWidth(), this.handler.getCraftingHeight(), this.handler.getCraftingResultSlotIndex(), recipe, intList.iterator(), j);
            }
        }
    }

    @Override
    public void acceptAlignedInput(Iterator<Integer> inputs, int slot, int amount, int gridX, int gridY) {
        Slot slot2 = this.handler.getSlot(slot);
        ItemStack itemStack = RecipeMatcher.getStackFromId(inputs.next());
        if (!itemStack.isEmpty()) {
            for (int i = 0; i < amount; ++i) {
                this.fillInputSlot(slot2, itemStack);
            }
        }
    }

    protected int getAmountToFill(boolean craftAll, int limit, boolean recipeInCraftingSlots) {
        int i = 1;
        if (craftAll) {
            i = limit;
        } else if (recipeInCraftingSlots) {
            i = 64;
            for (int j = 0; j < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; ++j) {
                ItemStack itemStack;
                if (j == this.handler.getCraftingResultSlotIndex() || (itemStack = this.handler.getSlot(j).getStack()).isEmpty() || i <= itemStack.getCount()) continue;
                i = itemStack.getCount();
            }
            if (i < 64) {
                ++i;
            }
        }
        return i;
    }

    protected void fillInputSlot(Slot slot, ItemStack stack) {
        int i = this.inventory.method_7371(stack);
        if (i == -1) {
            return;
        }
        ItemStack itemStack = this.inventory.getStack(i).copy();
        if (itemStack.isEmpty()) {
            return;
        }
        if (itemStack.getCount() > 1) {
            this.inventory.removeStack(i, 1);
        } else {
            this.inventory.removeStack(i);
        }
        itemStack.setCount(1);
        if (slot.getStack().isEmpty()) {
            slot.setStack(itemStack);
        } else {
            slot.getStack().increment(1);
        }
    }

    private boolean canReturnInputs() {
        ArrayList list = Lists.newArrayList();
        int i = this.getFreeInventorySlots();
        for (int j = 0; j < this.handler.getCraftingWidth() * this.handler.getCraftingHeight() + 1; ++j) {
            ItemStack itemStack;
            if (j == this.handler.getCraftingResultSlotIndex() || (itemStack = this.handler.getSlot(j).getStack().copy()).isEmpty()) continue;
            int k = this.inventory.getOccupiedSlotWithRoomForStack(itemStack);
            if (k == -1 && list.size() <= i) {
                for (ItemStack itemStack2 : list) {
                    if (!itemStack2.isItemEqualIgnoreDamage(itemStack) || itemStack2.getCount() == itemStack2.getMaxCount() || itemStack2.getCount() + itemStack.getCount() > itemStack2.getMaxCount()) continue;
                    itemStack2.increment(itemStack.getCount());
                    itemStack.setCount(0);
                    break;
                }
                if (itemStack.isEmpty()) continue;
                if (list.size() < i) {
                    list.add(itemStack);
                    continue;
                }
                return false;
            }
            if (k != -1) continue;
            return false;
        }
        return true;
    }

    private int getFreeInventorySlots() {
        int i = 0;
        for (ItemStack itemStack : this.inventory.main) {
            if (!itemStack.isEmpty()) continue;
            ++i;
        }
        return i;
    }
}

