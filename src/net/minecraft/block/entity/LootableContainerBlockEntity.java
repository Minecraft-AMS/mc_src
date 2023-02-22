/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public abstract class LootableContainerBlockEntity
extends LockableContainerBlockEntity {
    @Nullable
    protected Identifier lootTableId;
    protected long lootTableSeed;

    protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public static void setLootTable(BlockView world, Random random, BlockPos pos, Identifier id) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LootableContainerBlockEntity) {
            ((LootableContainerBlockEntity)blockEntity).setLootTable(id, random.nextLong());
        }
    }

    protected boolean deserializeLootTable(CompoundTag compoundTag) {
        if (compoundTag.contains("LootTable", 8)) {
            this.lootTableId = new Identifier(compoundTag.getString("LootTable"));
            this.lootTableSeed = compoundTag.getLong("LootTableSeed");
            return true;
        }
        return false;
    }

    protected boolean serializeLootTable(CompoundTag compoundTag) {
        if (this.lootTableId == null) {
            return false;
        }
        compoundTag.putString("LootTable", this.lootTableId.toString());
        if (this.lootTableSeed != 0L) {
            compoundTag.putLong("LootTableSeed", this.lootTableSeed);
        }
        return true;
    }

    public void checkLootInteraction(@Nullable PlayerEntity playerEntity) {
        if (this.lootTableId != null && this.world.getServer() != null) {
            LootTable lootTable = this.world.getServer().getLootManager().getSupplier(this.lootTableId);
            this.lootTableId = null;
            LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).put(LootContextParameters.POSITION, new BlockPos(this.pos)).setRandom(this.lootTableSeed);
            if (playerEntity != null) {
                builder.setLuck(playerEntity.getLuck()).put(LootContextParameters.THIS_ENTITY, playerEntity);
            }
            lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST));
        }
    }

    public void setLootTable(Identifier id, long seed) {
        this.lootTableId = id;
        this.lootTableSeed = seed;
    }

    @Override
    public ItemStack getInvStack(int slot) {
        this.checkLootInteraction(null);
        return this.getInvStackList().get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        this.checkLootInteraction(null);
        ItemStack itemStack = Inventories.splitStack(this.getInvStackList(), slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        this.checkLootInteraction(null);
        return Inventories.removeStack(this.getInvStackList(), slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getCount() > this.getInvMaxStackAmount()) {
            stack.setCount(this.getInvMaxStackAmount());
        }
        this.markDirty();
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return !(player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
    }

    @Override
    public void clear() {
        this.getInvStackList().clear();
    }

    protected abstract DefaultedList<ItemStack> getInvStackList();

    protected abstract void setInvStackList(DefaultedList<ItemStack> var1);

    @Override
    public boolean checkUnlocked(PlayerEntity player) {
        return super.checkUnlocked(player) && (this.lootTableId == null || !player.isSpectator());
    }

    @Override
    @Nullable
    public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            this.checkLootInteraction(playerInventory.player);
            return this.createContainer(syncId, playerInventory);
        }
        return null;
    }
}
