/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.test;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.test.PositionedException;
import net.minecraft.test.TimedTaskRunner;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TestContext {
    private final GameTestState test;
    private boolean hasFinalClause;

    public TestContext(GameTestState test) {
        this.test = test;
    }

    public ServerWorld getWorld() {
        return this.test.getWorld();
    }

    public BlockState getBlockState(BlockPos pos) {
        return this.getWorld().getBlockState(this.getAbsolutePos(pos));
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.getWorld().getBlockEntity(this.getAbsolutePos(pos));
    }

    public void killAllEntities() {
        Box box = this.getTestBox();
        List<Entity> list = this.getWorld().getEntitiesByClass(Entity.class, box.expand(1.0), entity -> !(entity instanceof PlayerEntity));
        list.forEach(Entity::kill);
    }

    public ItemEntity spawnItem(Item item, float x, float y, float z) {
        ServerWorld serverWorld = this.getWorld();
        Vec3d vec3d = this.getAbsolute(new Vec3d(x, y, z));
        ItemEntity itemEntity = new ItemEntity(serverWorld, vec3d.x, vec3d.y, vec3d.z, new ItemStack(item, 1));
        itemEntity.setVelocity(0.0, 0.0, 0.0);
        serverWorld.spawnEntity(itemEntity);
        return itemEntity;
    }

    public <E extends Entity> E spawnEntity(EntityType<E> type, BlockPos pos) {
        return this.spawnEntity(type, Vec3d.ofBottomCenter(pos));
    }

    public <E extends Entity> E spawnEntity(EntityType<E> type, Vec3d pos) {
        ServerWorld serverWorld = this.getWorld();
        E entity = type.create(serverWorld);
        if (entity instanceof MobEntity) {
            ((MobEntity)entity).setPersistent();
        }
        Vec3d vec3d = this.getAbsolute(pos);
        ((Entity)entity).refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, ((Entity)entity).getYaw(), ((Entity)entity).getPitch());
        serverWorld.spawnEntity((Entity)entity);
        return entity;
    }

    public <E extends Entity> E spawnEntity(EntityType<E> type, int x, int y, int z) {
        return this.spawnEntity(type, new BlockPos(x, y, z));
    }

    public <E extends Entity> E spawnEntity(EntityType<E> type, float x, float y, float z) {
        return this.spawnEntity(type, new Vec3d(x, y, z));
    }

    public <E extends MobEntity> E spawnMob(EntityType<E> type, BlockPos pos) {
        MobEntity mobEntity = (MobEntity)this.spawnEntity(type, pos);
        mobEntity.clearGoalsAndTasks();
        return (E)mobEntity;
    }

    public <E extends MobEntity> E spawnMob(EntityType<E> type, int x, int y, int z) {
        return this.spawnMob(type, new BlockPos(x, y, z));
    }

    public <E extends MobEntity> E spawnMob(EntityType<E> type, Vec3d pos) {
        MobEntity mobEntity = (MobEntity)this.spawnEntity(type, pos);
        mobEntity.clearGoalsAndTasks();
        return (E)mobEntity;
    }

    public <E extends MobEntity> E spawnMob(EntityType<E> type, float x, float y, float z) {
        return this.spawnMob(type, new Vec3d(x, y, z));
    }

    public TimedTaskRunner startMovingTowards(MobEntity entity, BlockPos pos, float speed) {
        return this.createTimedTaskRunner().expectMinDurationAndRun(2, () -> {
            Path path = entity.getNavigation().findPathTo(this.getAbsolutePos(pos), 0);
            entity.getNavigation().startMovingAlong(path, speed);
        });
    }

    public void pushButton(int x, int y, int z) {
        this.pushButton(new BlockPos(x, y, z));
    }

    public void pushButton(BlockPos pos) {
        this.checkBlockState(pos, state -> state.isIn(BlockTags.BUTTONS), () -> "Expected button");
        BlockPos blockPos = this.getAbsolutePos(pos);
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        AbstractButtonBlock abstractButtonBlock = (AbstractButtonBlock)blockState.getBlock();
        abstractButtonBlock.powerOn(blockState, this.getWorld(), blockPos);
    }

    public void useBlock(BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        blockState.onUse(this.getWorld(), this.createMockPlayer(), Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockPos), Direction.NORTH, blockPos, true));
    }

    public LivingEntity drown(LivingEntity entity) {
        entity.setAir(0);
        entity.setHealth(0.25f);
        return entity;
    }

    public PlayerEntity createMockPlayer() {
        return new PlayerEntity(this.getWorld(), BlockPos.ORIGIN, 0.0f, new GameProfile(UUID.randomUUID(), "test-mock-player")){

            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
    }

    public void toggleLever(int x, int y, int z) {
        this.toggleLever(new BlockPos(x, y, z));
    }

    public void toggleLever(BlockPos pos) {
        this.expectBlock(Blocks.LEVER, pos);
        BlockPos blockPos = this.getAbsolutePos(pos);
        BlockState blockState = this.getWorld().getBlockState(blockPos);
        LeverBlock leverBlock = (LeverBlock)blockState.getBlock();
        leverBlock.togglePower(blockState, this.getWorld(), blockPos);
    }

    public void putAndRemoveRedstoneBlock(BlockPos pos, long delay) {
        this.setBlockState(pos, Blocks.REDSTONE_BLOCK);
        this.waitAndRun(delay, () -> this.setBlockState(pos, Blocks.AIR));
    }

    public void removeBlock(BlockPos pos) {
        this.getWorld().breakBlock(this.getAbsolutePos(pos), false, null);
    }

    public void setBlockState(int x, int y, int z, Block block) {
        this.setBlockState(new BlockPos(x, y, z), block);
    }

    public void setBlockState(int x, int y, int z, BlockState state) {
        this.setBlockState(new BlockPos(x, y, z), state);
    }

    public void setBlockState(BlockPos pos, Block block) {
        this.setBlockState(pos, block.getDefaultState());
    }

    public void setBlockState(BlockPos pos, BlockState state) {
        this.getWorld().setBlockState(this.getAbsolutePos(pos), state, 3);
    }

    public void useNightTime() {
        this.setTime(13000);
    }

    public void setTime(int timeOfDay) {
        this.getWorld().setTimeOfDay(timeOfDay);
    }

    public void expectBlock(Block block, int x, int y, int z) {
        this.expectBlock(block, new BlockPos(x, y, z));
    }

    public void expectBlock(Block block, BlockPos pos) {
        BlockState blockState = this.getBlockState(pos);
        this.checkBlock(pos, (Block block1) -> blockState.isOf(block), "Expected " + block.getName().getString() + ", got " + blockState.getBlock().getName().getString());
    }

    public void dontExpectBlock(Block block, int x, int y, int z) {
        this.dontExpectBlock(block, new BlockPos(x, y, z));
    }

    public void dontExpectBlock(Block block, BlockPos pos) {
        this.checkBlock(pos, (Block block1) -> !this.getBlockState(pos).isOf(block), "Did not expect " + block.getName().getString());
    }

    public void expectBlockAtEnd(Block block, int x, int y, int z) {
        this.expectBlockAtEnd(block, new BlockPos(x, y, z));
    }

    public void expectBlockAtEnd(Block block, BlockPos pos) {
        this.addInstantFinalTask(() -> this.expectBlock(block, pos));
    }

    public void checkBlock(BlockPos pos, Predicate<Block> predicate, String errorMessage) {
        this.checkBlock(pos, predicate, () -> errorMessage);
    }

    public void checkBlock(BlockPos pos, Predicate<Block> predicate, Supplier<String> errorMessageSupplier) {
        this.checkBlockState(pos, state -> predicate.test(state.getBlock()), errorMessageSupplier);
    }

    public <T extends Comparable<T>> void expectBlockProperty(BlockPos pos, Property<T> property, T value) {
        this.checkBlockState(pos, state -> state.contains(property) && state.get(property).equals(value), () -> "Expected property " + property.getName() + " to be " + value);
    }

    public <T extends Comparable<T>> void checkBlockProperty(BlockPos pos, Property<T> property, Predicate<T> predicate, String errorMessage) {
        this.checkBlockState(pos, state -> predicate.test(state.get(property)), () -> errorMessage);
    }

    public void checkBlockState(BlockPos pos, Predicate<BlockState> predicate, Supplier<String> errorMessageSupplier) {
        BlockState blockState = this.getBlockState(pos);
        if (!predicate.test(blockState)) {
            throw new PositionedException(errorMessageSupplier.get(), this.getAbsolutePos(pos), pos, this.test.getTick());
        }
    }

    public void expectEntity(EntityType<?> type) {
        List<Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
        if (list.isEmpty()) {
            throw new GameTestException("Expected " + type.getUntranslatedName() + " to exist");
        }
    }

    public void expectEntityAt(EntityType<?> type, int x, int y, int z) {
        this.expectEntityAt(type, new BlockPos(x, y, z));
    }

    public void expectEntityAt(EntityType<?> type, BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
        }
    }

    public void expectEntityAround(EntityType<?> type, BlockPos pos, double radius) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos).expand(radius), Entity::isAlive);
        if (list.isEmpty()) {
            throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
        }
    }

    public void expectEntityAt(Entity entity, int x, int y, int z) {
        this.expectEntityAt(entity, new BlockPos(x, y, z));
    }

    public void expectEntityAt(Entity entity, BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<Entity> list = this.getWorld().getEntitiesByType(entity.getType(), new Box(blockPos), Entity::isAlive);
        list.stream().filter(e -> e == entity).findFirst().orElseThrow(() -> new PositionedException("Expected " + entity.getType().getUntranslatedName(), blockPos, pos, this.test.getTick()));
    }

    public void expectItemsAt(Item item, BlockPos pos, double radius, int amount) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<ItemEntity> list = this.getWorld().getEntitiesByType(EntityType.ITEM, new Box(blockPos).expand(radius), Entity::isAlive);
        int i = 0;
        for (Entity entity : list) {
            ItemEntity itemEntity = (ItemEntity)entity;
            if (!itemEntity.getStack().getItem().equals(item)) continue;
            i += itemEntity.getStack().getCount();
        }
        if (i != amount) {
            throw new PositionedException("Expected " + amount + " " + item.getName().getString() + " items to exist (found " + i + ")", blockPos, pos, this.test.getTick());
        }
    }

    public void expectItemAt(Item item, BlockPos pos, double radius) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<ItemEntity> list = this.getWorld().getEntitiesByType(EntityType.ITEM, new Box(blockPos).expand(radius), Entity::isAlive);
        for (Entity entity : list) {
            ItemEntity itemEntity = (ItemEntity)entity;
            if (!itemEntity.getStack().getItem().equals(item)) continue;
            return;
        }
        throw new PositionedException("Expected " + item.getName().getString() + " item", blockPos, pos, this.test.getTick());
    }

    public void dontExpectEntity(EntityType<?> type) {
        List<Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), Entity::isAlive);
        if (!list.isEmpty()) {
            throw new GameTestException("Did not expect " + type.getUntranslatedName() + " to exist");
        }
    }

    public void dontExpectEntityAt(EntityType<?> type, int x, int y, int z) {
        this.dontExpectEntityAt(type, new BlockPos(x, y, z));
    }

    public void dontExpectEntityAt(EntityType<?> type, BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
        if (!list.isEmpty()) {
            throw new PositionedException("Did not expect " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
        }
    }

    public void expectEntityToTouch(EntityType<?> type, double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        Vec3d vec3d2 = this.getAbsolute(vec3d);
        Predicate<Entity> predicate = entity -> entity.getBoundingBox().intersects(vec3d2, vec3d2);
        List<Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
        if (list.isEmpty()) {
            throw new GameTestException("Expected " + type.getUntranslatedName() + " to touch " + vec3d2 + " (relative " + vec3d + ")");
        }
    }

    public void dontExpectEntityToTouch(EntityType<?> type, double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        Vec3d vec3d2 = this.getAbsolute(vec3d);
        Predicate<Entity> predicate = entity -> !entity.getBoundingBox().intersects(vec3d2, vec3d2);
        List<Entity> list = this.getWorld().getEntitiesByType(type, this.getTestBox(), predicate);
        if (list.isEmpty()) {
            throw new GameTestException("Did not expect " + type.getUntranslatedName() + " to touch " + vec3d2 + " (relative " + vec3d + ")");
        }
    }

    public <E extends Entity, T> void expectEntityWithData(BlockPos pos, EntityType<E> type, Function<? super E, T> entityDataGetter, @Nullable T data) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        List<Entity> list = this.getWorld().getEntitiesByType(type, new Box(blockPos), Entity::isAlive);
        if (list.isEmpty()) {
            throw new PositionedException("Expected " + type.getUntranslatedName(), blockPos, pos, this.test.getTick());
        }
        for (Entity entity : list) {
            T object = entityDataGetter.apply(entity);
            if (!(object == null ? data != null : !object.equals(data))) continue;
            throw new GameTestException("Expected entity data to be: " + data + ", but was: " + object);
        }
    }

    public void expectEmptyContainer(BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
        if (blockEntity instanceof LockableContainerBlockEntity && !((LockableContainerBlockEntity)blockEntity).isEmpty()) {
            throw new GameTestException("Container should be empty");
        }
    }

    public void expectContainerWith(BlockPos pos, Item item) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
        if (blockEntity instanceof LockableContainerBlockEntity && ((LockableContainerBlockEntity)blockEntity).count(item) != 1) {
            throw new GameTestException("Container should contain: " + item);
        }
    }

    public void expectSameStates(BlockBox checkedBlockBox, BlockPos correctStatePos) {
        BlockPos.stream(checkedBlockBox).forEach(checkedPos -> {
            BlockPos blockPos2 = correctStatePos.add(checkedPos.getX() - checkedBlockBox.getMinX(), checkedPos.getY() - checkedBlockBox.getMinY(), checkedPos.getZ() - checkedBlockBox.getMinZ());
            this.expectSameStates((BlockPos)checkedPos, blockPos2);
        });
    }

    public void expectSameStates(BlockPos checkedPos, BlockPos correctStatePos) {
        BlockState blockState2;
        BlockState blockState = this.getBlockState(checkedPos);
        if (blockState != (blockState2 = this.getBlockState(correctStatePos))) {
            this.throwPositionedException("Incorrect state. Expected " + blockState2 + ", got " + blockState, checkedPos);
        }
    }

    public void expectContainerWith(long delay, BlockPos pos, Item item) {
        this.runAtTick(delay, () -> this.expectContainerWith(pos, item));
    }

    public void expectEmptyContainer(long delay, BlockPos pos) {
        this.runAtTick(delay, () -> this.expectEmptyContainer(pos));
    }

    public <E extends Entity, T> void expectEntityWithDataEnd(BlockPos pos, EntityType<E> type, Function<E, T> entityDataGetter, T data) {
        this.addInstantFinalTask(() -> this.expectEntityWithData(pos, type, entityDataGetter, data));
    }

    public <E extends Entity> void testEntity(E entity, Predicate<E> predicate, String testName) {
        if (!predicate.test(entity)) {
            throw new GameTestException("Entity " + entity + " failed " + testName + " test");
        }
    }

    public <E extends Entity, T> void testEntityProperty(E entity, Function<E, T> propertyGetter, String propertyName, T expectedValue) {
        T object = propertyGetter.apply(entity);
        if (!object.equals(expectedValue)) {
            throw new GameTestException("Entity " + entity + " value " + propertyName + "=" + object + " is not equal to expected " + expectedValue);
        }
    }

    public void expectEntityAtEnd(EntityType<?> type, int x, int y, int z) {
        this.expectEntityAtEnd(type, new BlockPos(x, y, z));
    }

    public void expectEntityAtEnd(EntityType<?> type, BlockPos pos) {
        this.addInstantFinalTask(() -> this.expectEntityAt(type, pos));
    }

    public void dontExpectEntityAtEnd(EntityType<?> type, int x, int y, int z) {
        this.dontExpectEntityAtEnd(type, new BlockPos(x, y, z));
    }

    public void dontExpectEntityAtEnd(EntityType<?> type, BlockPos pos) {
        this.addInstantFinalTask(() -> this.dontExpectEntityAt(type, pos));
    }

    public void complete() {
        this.test.completeIfSuccessful();
    }

    private void markFinalCause() {
        if (this.hasFinalClause) {
            throw new IllegalStateException("This test already has final clause");
        }
        this.hasFinalClause = true;
    }

    public void addFinalTask(Runnable runnable) {
        this.markFinalCause();
        this.test.createTimedTaskRunner().createAndAdd(0L, runnable).completeIfSuccessful();
    }

    public void addInstantFinalTask(Runnable runnable) {
        this.markFinalCause();
        this.test.createTimedTaskRunner().createAndAdd(runnable).completeIfSuccessful();
    }

    public void addFinalTaskWithDuration(int duration, Runnable runnable) {
        this.markFinalCause();
        this.test.createTimedTaskRunner().createAndAdd(duration, runnable).completeIfSuccessful();
    }

    public void runAtTick(long tick, Runnable runnable) {
        this.test.runAtTick(tick, runnable);
    }

    public void waitAndRun(long ticks, Runnable runnable) {
        this.runAtTick(this.test.getTick() + ticks, runnable);
    }

    public void forceRandomTick(BlockPos pos) {
        BlockPos blockPos = this.getAbsolutePos(pos);
        ServerWorld serverWorld = this.getWorld();
        serverWorld.getBlockState(blockPos).randomTick(serverWorld, blockPos, serverWorld.random);
    }

    public void throwPositionedException(String message, BlockPos pos) {
        throw new PositionedException(message, this.getAbsolutePos(pos), pos, this.getTick());
    }

    public void throwPositionedException(String message, Entity entity) {
        throw new PositionedException(message, entity.getBlockPos(), this.getRelativePos(entity.getBlockPos()), this.getTick());
    }

    public void throwGameTestException(String message) {
        throw new GameTestException(message);
    }

    public void addTask(Runnable task) {
        this.test.createTimedTaskRunner().createAndAdd(task).fail(() -> new GameTestException("Fail conditions met"));
    }

    public void runAtEveryTick(Runnable task) {
        LongStream.range(this.test.getTick(), this.test.getTicksLeft()).forEach(tick -> this.test.runAtTick(tick, task::run));
    }

    public TimedTaskRunner createTimedTaskRunner() {
        return this.test.createTimedTaskRunner();
    }

    public BlockPos getAbsolutePos(BlockPos pos) {
        BlockPos blockPos = this.test.getPos();
        BlockPos blockPos2 = blockPos.add(pos);
        return Structure.transformAround(blockPos2, BlockMirror.NONE, this.test.getRotation(), blockPos);
    }

    public BlockPos getRelativePos(BlockPos pos) {
        BlockPos blockPos = this.test.getPos();
        BlockRotation blockRotation = this.test.getRotation().rotate(BlockRotation.CLOCKWISE_180);
        BlockPos blockPos2 = Structure.transformAround(pos, BlockMirror.NONE, blockRotation, blockPos);
        return blockPos2.subtract(blockPos);
    }

    public Vec3d getAbsolute(Vec3d pos) {
        Vec3d vec3d = Vec3d.of(this.test.getPos());
        return Structure.transformAround(vec3d.add(pos), BlockMirror.NONE, this.test.getRotation(), this.test.getPos());
    }

    public long getTick() {
        return this.test.getTick();
    }

    private Box getTestBox() {
        return this.test.getBoundingBox();
    }

    private Box getRelativeTestBox() {
        Box box = this.test.getBoundingBox();
        return box.offset(BlockPos.ORIGIN.subtract(this.getAbsolutePos(BlockPos.ORIGIN)));
    }

    public void forEachRelativePos(Consumer<BlockPos> posConsumer) {
        Box box = this.getRelativeTestBox();
        BlockPos.Mutable.stream(box.offset(0.0, 1.0, 0.0)).forEach(posConsumer);
    }

    public void forEachRemainingTick(Runnable runnable) {
        LongStream.range(this.test.getTick(), this.test.getTicksLeft()).forEach(tick -> this.test.runAtTick(tick, runnable::run));
    }
}

