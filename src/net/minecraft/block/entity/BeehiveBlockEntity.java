/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BeehiveBlockEntity
extends BlockEntity
implements Tickable {
    private final List<Bee> bees = Lists.newArrayList();
    @Nullable
    private BlockPos flowerPos = null;

    public BeehiveBlockEntity() {
        super(BlockEntityType.BEEHIVE);
    }

    @Override
    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }
        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            if (!(this.world.getBlockState(blockPos).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNoBees() {
        return this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState beeState) {
        List<Entity> list = this.tryReleaseBee(state, beeState);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof BeeEntity)) continue;
                BeeEntity beeEntity = (BeeEntity)entity;
                if (!(player.getPos().squaredDistanceTo(entity.getPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    beeEntity.setBeeAttacker(player);
                    continue;
                }
                beeEntity.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState state, BeeState beeState) {
        ArrayList list = Lists.newArrayList();
        this.bees.removeIf(bee -> this.releaseBee(state, ((Bee)bee).entityData, list, beeState));
        return list;
    }

    public void tryEnterHive(Entity entity, boolean hasNectar) {
        this.tryEnterHive(entity, hasNectar, 0);
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
    }

    protected void sendDebugData() {
        DebugInfoSender.sendBeehiveDebugData(this);
    }

    public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
        if (this.bees.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.removeAllPassengers();
        CompoundTag compoundTag = new CompoundTag();
        entity.saveToTag(compoundTag);
        this.bees.add(new Bee(compoundTag, ticksInHive, hasNectar ? 2400 : 600));
        if (this.world != null) {
            BeeEntity beeEntity;
            if (entity instanceof BeeEntity && (beeEntity = (BeeEntity)entity).hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = beeEntity.getFlowerPos();
            }
            BlockPos blockPos = this.getPos();
            this.world.playSound(null, (double)blockPos.getX(), (double)blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        entity.remove();
    }

    private boolean releaseBee(BlockState state, CompoundTag compoundTag, @Nullable List<Entity> list, BeeState beeState) {
        boolean bl;
        BlockPos blockPos = this.getPos();
        if ((this.world.isNight() || this.world.isRaining()) && beeState != BeeState.EMERGENCY) {
            return false;
        }
        compoundTag.remove("Passengers");
        compoundTag.remove("Leash");
        compoundTag.removeUuid("UUID");
        Direction direction = state.get(BeehiveBlock.FACING);
        BlockPos blockPos2 = blockPos.offset(direction);
        boolean bl2 = bl = !this.world.getBlockState(blockPos2).getCollisionShape(this.world, blockPos2).isEmpty();
        if (bl && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Entity entity2 = EntityType.loadEntityWithPassengers(compoundTag, this.world, entity -> entity);
        if (entity2 != null) {
            float f = entity2.getWidth();
            double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
            double e = (double)blockPos.getX() + 0.5 + d * (double)direction.getOffsetX();
            double g = (double)blockPos.getY() + 0.5 - (double)(entity2.getHeight() / 2.0f);
            double h = (double)blockPos.getZ() + 0.5 + d * (double)direction.getOffsetZ();
            entity2.refreshPositionAndAngles(e, g, h, entity2.yaw, entity2.pitch);
            if (!entity2.getType().isTaggedWith(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return false;
            }
            if (entity2 instanceof BeeEntity) {
                BeeEntity beeEntity = (BeeEntity)entity2;
                if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.random.nextFloat() < 0.9f) {
                    beeEntity.setFlowerPos(this.flowerPos);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    beeEntity.onHoneyDelivered();
                    if (state.getBlock().matches(BlockTags.BEEHIVES) && (i = BeehiveBlockEntity.getHoneyLevel(state)) < 5) {
                        int j;
                        int n = j = this.world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        this.world.setBlockState(this.getPos(), (BlockState)state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                beeEntity.resetPollinationTicks();
                if (list != null) {
                    list.add(beeEntity);
                }
            }
            BlockPos blockPos3 = this.getPos();
            this.world.playSound(null, (double)blockPos3.getX(), (double)blockPos3.getY(), blockPos3.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return this.world.spawnEntity(entity2);
        }
        return false;
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private void tickBees() {
        Iterator<Bee> iterator = this.bees.iterator();
        BlockState blockState = this.getCachedState();
        while (iterator.hasNext()) {
            Bee bee = iterator.next();
            if (bee.ticksInHive > bee.minOccupationTIcks) {
                CompoundTag compoundTag;
                BeeState beeState = (compoundTag = bee.entityData).getBoolean("HasNectar") ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
                if (!this.releaseBee(blockState, compoundTag, null, beeState)) continue;
                iterator.remove();
                continue;
            }
            bee.ticksInHive++;
        }
    }

    @Override
    public void tick() {
        if (this.world.isClient) {
            return;
        }
        this.tickBees();
        BlockPos blockPos = this.getPos();
        if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005) {
            double d = (double)blockPos.getX() + 0.5;
            double e = blockPos.getY();
            double f = (double)blockPos.getZ() + 0.5;
            this.world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        this.sendDebugData();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.bees.clear();
        ListTag listTag = tag.getList("Bees", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Bee bee = new Bee(compoundTag.getCompound("EntityData"), compoundTag.getInt("TicksInHive"), compoundTag.getInt("MinOccupationTicks"));
            this.bees.add(bee);
        }
        this.flowerPos = null;
        if (tag.contains("FlowerPos")) {
            this.flowerPos = NbtHelper.toBlockPos(tag.getCompound("FlowerPos"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Bees", this.getBees());
        if (this.hasFlowerPos()) {
            tag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
        }
        return tag;
    }

    public ListTag getBees() {
        ListTag listTag = new ListTag();
        for (Bee bee : this.bees) {
            bee.entityData.removeUuid("UUID");
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("EntityData", bee.entityData);
            compoundTag.putInt("TicksInHive", bee.ticksInHive);
            compoundTag.putInt("MinOccupationTicks", bee.minOccupationTIcks);
            listTag.add(compoundTag);
        }
        return listTag;
    }

    static class Bee {
        private final CompoundTag entityData;
        private int ticksInHive;
        private final int minOccupationTIcks;

        private Bee(CompoundTag entityData, int ticksInHive, int minOccupationTicks) {
            entityData.removeUuid("UUID");
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTIcks = minOccupationTicks;
        }
    }

    public static enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

    }
}

