/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientWorld
extends World {
    private final List<Entity> globalEntities = Lists.newArrayList();
    private final Int2ObjectMap<Entity> regularEntities = new Int2ObjectOpenHashMap();
    private final ClientPlayNetworkHandler netHandler;
    private final WorldRenderer worldRenderer;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<AbstractClientPlayerEntity> players = Lists.newArrayList();
    private int ticksUntilCaveAmbientSound = this.random.nextInt(12000);
    private Scoreboard scoreboard = new Scoreboard();
    private final Map<String, MapState> mapStates = Maps.newHashMap();

    public ClientWorld(ClientPlayNetworkHandler clientPlayNetworkHandler, LevelInfo levelInfo, DimensionType dimensionType, int chunkLoadDistance, Profiler profiler, WorldRenderer worldRenderer) {
        super(new LevelProperties(levelInfo, "MpServer"), dimensionType, (world, dimension) -> new ClientChunkManager((ClientWorld)world, chunkLoadDistance), profiler, true);
        this.netHandler = clientPlayNetworkHandler;
        this.worldRenderer = worldRenderer;
        this.setSpawnPos(new BlockPos(8, 64, 8));
        this.calculateAmbientDarkness();
        this.initWeatherGradients();
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.chunkManager.tick(booleanSupplier);
        this.tickCaveAmbientSound();
        this.getProfiler().pop();
    }

    public Iterable<Entity> getEntities() {
        return Iterables.concat((Iterable)this.regularEntities.values(), this.globalEntities);
    }

    public void tickEntities() {
        Profiler profiler = this.getProfiler();
        profiler.push("entities");
        profiler.push("global");
        for (int i = 0; i < this.globalEntities.size(); ++i) {
            Entity entity2 = this.globalEntities.get(i);
            this.tickEntity(entity -> {
                ++entity.age;
                entity.tick();
            }, entity2);
            if (!entity2.removed) continue;
            this.globalEntities.remove(i--);
        }
        profiler.swap("regular");
        ObjectIterator objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity entity2 = (Entity)entry.getValue();
            if (entity2.hasVehicle()) continue;
            profiler.push("tick");
            if (!entity2.removed) {
                this.tickEntity(this::tickEntity, entity2);
            }
            profiler.pop();
            profiler.push("remove");
            if (entity2.removed) {
                objectIterator.remove();
                this.finishRemovingEntity(entity2);
            }
            profiler.pop();
        }
        profiler.pop();
        this.tickBlockEntities();
        profiler.pop();
    }

    public void tickEntity(Entity entity) {
        if (!(entity instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(entity)) {
            return;
        }
        entity.lastRenderX = entity.x;
        entity.lastRenderY = entity.y;
        entity.lastRenderZ = entity.z;
        entity.prevYaw = entity.yaw;
        entity.prevPitch = entity.pitch;
        if (entity.updateNeeded || entity.isSpectator()) {
            ++entity.age;
            this.getProfiler().push(() -> Registry.ENTITY_TYPE.getId(entity.getType()).toString());
            entity.tick();
            this.getProfiler().pop();
        }
        this.checkChunk(entity);
        if (entity.updateNeeded) {
            for (Entity entity2 : entity.getPassengerList()) {
                this.tickPassenger(entity, entity2);
            }
        }
    }

    public void tickPassenger(Entity entity, Entity passenger) {
        if (passenger.removed || passenger.getVehicle() != entity) {
            passenger.stopRiding();
            return;
        }
        if (!(passenger instanceof PlayerEntity) && !this.getChunkManager().shouldTickEntity(passenger)) {
            return;
        }
        passenger.lastRenderX = passenger.x;
        passenger.lastRenderY = passenger.y;
        passenger.lastRenderZ = passenger.z;
        passenger.prevYaw = passenger.yaw;
        passenger.prevPitch = passenger.pitch;
        if (passenger.updateNeeded) {
            ++passenger.age;
            passenger.tickRiding();
        }
        this.checkChunk(passenger);
        if (passenger.updateNeeded) {
            for (Entity entity2 : passenger.getPassengerList()) {
                this.tickPassenger(passenger, entity2);
            }
        }
    }

    public void checkChunk(Entity entity) {
        this.getProfiler().push("chunkCheck");
        int i = MathHelper.floor(entity.x / 16.0);
        int j = MathHelper.floor(entity.y / 16.0);
        int k = MathHelper.floor(entity.z / 16.0);
        if (!entity.updateNeeded || entity.chunkX != i || entity.chunkY != j || entity.chunkZ != k) {
            if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ)) {
                this.getChunk(entity.chunkX, entity.chunkZ).remove(entity, entity.chunkY);
            }
            if (entity.teleportRequested() || this.isChunkLoaded(i, k)) {
                this.getChunk(i, k).addEntity(entity);
            } else {
                entity.updateNeeded = false;
            }
        }
        this.getProfiler().pop();
    }

    public void unloadBlockEntities(WorldChunk chunk) {
        this.unloadedBlockEntities.addAll(chunk.getBlockEntities().values());
        this.chunkManager.getLightingProvider().setLightEnabled(chunk.getPos(), false);
    }

    @Override
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return true;
    }

    private void tickCaveAmbientSound() {
        BlockState blockState;
        if (this.client.player == null) {
            return;
        }
        if (this.ticksUntilCaveAmbientSound > 0) {
            --this.ticksUntilCaveAmbientSound;
            return;
        }
        BlockPos blockPos = new BlockPos(this.client.player);
        BlockPos blockPos2 = blockPos.add(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
        double d = blockPos.getSquaredDistance(blockPos2);
        if (d >= 4.0 && d <= 256.0 && (blockState = this.getBlockState(blockPos2)).isAir() && this.getLightLevel(blockPos2, 0) <= this.random.nextInt(8) && this.getLightLevel(LightType.SKY, blockPos2) <= 0) {
            this.playSound((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, SoundEvents.AMBIENT_CAVE, SoundCategory.AMBIENT, 0.7f, 0.8f + this.random.nextFloat() * 0.2f, false);
            this.ticksUntilCaveAmbientSound = this.random.nextInt(12000) + 6000;
        }
    }

    public int getRegularEntityCount() {
        return this.regularEntities.size();
    }

    public void addLightning(LightningEntity lightning) {
        this.globalEntities.add(lightning);
    }

    public void addPlayer(int id, AbstractClientPlayerEntity player) {
        this.addEntityPrivate(id, player);
        this.players.add(player);
    }

    public void addEntity(int id, Entity entity) {
        this.addEntityPrivate(id, entity);
    }

    private void addEntityPrivate(int id, Entity entity) {
        this.removeEntity(id);
        this.regularEntities.put(id, (Object)entity);
        this.getChunkManager().getChunk(MathHelper.floor(entity.x / 16.0), MathHelper.floor(entity.z / 16.0), ChunkStatus.FULL, true).addEntity(entity);
    }

    public void removeEntity(int i) {
        Entity entity = (Entity)this.regularEntities.remove(i);
        if (entity != null) {
            entity.remove();
            this.finishRemovingEntity(entity);
        }
    }

    private void finishRemovingEntity(Entity entity) {
        entity.detach();
        if (entity.updateNeeded) {
            this.getChunk(entity.chunkX, entity.chunkZ).remove(entity);
        }
        this.players.remove(entity);
    }

    public void addEntitiesToChunk(WorldChunk chunk) {
        for (Int2ObjectMap.Entry entry : this.regularEntities.int2ObjectEntrySet()) {
            Entity entity = (Entity)entry.getValue();
            int i = MathHelper.floor(entity.x / 16.0);
            int j = MathHelper.floor(entity.z / 16.0);
            if (i != chunk.getPos().x || j != chunk.getPos().z) continue;
            chunk.addEntity(entity);
        }
    }

    @Override
    @Nullable
    public Entity getEntityById(int id) {
        return (Entity)this.regularEntities.get(id);
    }

    public void setBlockStateWithoutNeighborUpdates(BlockPos blockPos, BlockState blockState) {
        this.setBlockState(blockPos, blockState, 19);
    }

    @Override
    public void disconnect() {
        this.netHandler.getConnection().disconnect(new TranslatableText("multiplayer.status.quitting", new Object[0]));
    }

    public void doRandomBlockDisplayTicks(int xCenter, int yCenter, int i) {
        int j = 32;
        Random random = new Random();
        ItemStack itemStack = this.client.player.getMainHandStack();
        boolean bl = this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE && !itemStack.isEmpty() && itemStack.getItem() == Blocks.BARRIER.asItem();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k < 667; ++k) {
            this.randomBlockDisplayTick(xCenter, yCenter, i, 16, random, bl, mutable);
            this.randomBlockDisplayTick(xCenter, yCenter, i, 32, random, bl, mutable);
        }
    }

    public void randomBlockDisplayTick(int xCenter, int yCenter, int zCenter, int radius, Random random, boolean spawnBarrierParticles, BlockPos.Mutable pos) {
        int i = xCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        int j = yCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        int k = zCenter + this.random.nextInt(radius) - this.random.nextInt(radius);
        pos.set(i, j, k);
        BlockState blockState = this.getBlockState(pos);
        blockState.getBlock().randomDisplayTick(blockState, this, pos, random);
        FluidState fluidState = this.getFluidState(pos);
        if (!fluidState.isEmpty()) {
            fluidState.randomDisplayTick(this, pos, random);
            ParticleEffect particleEffect = fluidState.getParticle();
            if (particleEffect != null && this.random.nextInt(10) == 0) {
                boolean bl = blockState.isSideSolidFullSquare(this, pos, Direction.DOWN);
                BlockPos blockPos = pos.down();
                this.addParticle(blockPos, this.getBlockState(blockPos), particleEffect, bl);
            }
        }
        if (spawnBarrierParticles && blockState.getBlock() == Blocks.BARRIER) {
            this.addParticle(ParticleTypes.BARRIER, (float)i + 0.5f, (float)j + 0.5f, (float)k + 0.5f, 0.0, 0.0, 0.0);
        }
    }

    private void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean bl) {
        if (!state.getFluidState().isEmpty()) {
            return;
        }
        VoxelShape voxelShape = state.getCollisionShape(this, pos);
        double d = voxelShape.getMaximum(Direction.Axis.Y);
        if (d < 1.0) {
            if (bl) {
                this.addParticle(pos.getX(), pos.getX() + 1, pos.getZ(), pos.getZ() + 1, (double)(pos.getY() + 1) - 0.05, parameters);
            }
        } else if (!state.matches(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMinimum(Direction.Axis.Y);
            if (e > 0.0) {
                this.addParticle(pos, parameters, voxelShape, (double)pos.getY() + e - 0.05);
            } else {
                BlockPos blockPos = pos.down();
                BlockState blockState = this.getBlockState(blockPos);
                VoxelShape voxelShape2 = blockState.getCollisionShape(this, blockPos);
                double f = voxelShape2.getMaximum(Direction.Axis.Y);
                if (f < 1.0 && blockState.getFluidState().isEmpty()) {
                    this.addParticle(pos, parameters, voxelShape, (double)pos.getY() - 0.05);
                }
            }
        }
    }

    private void addParticle(BlockPos pos, ParticleEffect parameters, VoxelShape shape, double y) {
        this.addParticle((double)pos.getX() + shape.getMinimum(Direction.Axis.X), (double)pos.getX() + shape.getMaximum(Direction.Axis.X), (double)pos.getZ() + shape.getMinimum(Direction.Axis.Z), (double)pos.getZ() + shape.getMaximum(Direction.Axis.Z), y, parameters);
    }

    private void addParticle(double minX, double maxX, double minZ, double maxZ, double y, ParticleEffect parameters) {
        this.addParticle(parameters, MathHelper.lerp(this.random.nextDouble(), minX, maxX), y, MathHelper.lerp(this.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
    }

    public void finishRemovingEntities() {
        ObjectIterator objectIterator = this.regularEntities.int2ObjectEntrySet().iterator();
        while (objectIterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
            Entity entity = (Entity)entry.getValue();
            if (!entity.removed) continue;
            objectIterator.remove();
            this.finishRemovingEntity(entity);
        }
    }

    @Override
    public CrashReportSection addDetailsToCrashReport(CrashReport report) {
        CrashReportSection crashReportSection = super.addDetailsToCrashReport(report);
        crashReportSection.add("Server brand", () -> this.client.player.getServerBrand());
        crashReportSection.add("Server type", () -> this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server");
        return crashReportSection;
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        if (player == this.client.player) {
            this.playSound(x, y, z, sound, category, volume, pitch, false);
        }
    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity playerEntity, Entity entity, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        if (playerEntity == this.client.player) {
            this.client.getSoundManager().play(new EntityTrackingSoundInstance(soundEvent, soundCategory, entity));
        }
    }

    public void playSound(BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        this.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, sound, category, volume, pitch, useDistance);
    }

    @Override
    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory soundCategory, float f, float g, boolean bl) {
        double d = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
        PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(sound, soundCategory, f, g, (float)x, (float)y, (float)z);
        if (bl && d > 100.0) {
            double e = Math.sqrt(d) / 40.0;
            this.client.getSoundManager().play(positionedSoundInstance, (int)(e * 20.0));
        } else {
            this.client.getSoundManager().play(positionedSoundInstance);
        }
    }

    @Override
    public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable CompoundTag tag) {
        this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, tag));
    }

    @Override
    public void sendPacket(Packet<?> packet) {
        this.netHandler.sendPacket(packet);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.netHandler.getRecipeManager();
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void setTimeOfDay(long time) {
        if (time < 0L) {
            time = -time;
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
        } else {
            this.getGameRules().get(GameRules.DO_DAYLIGHT_CYCLE).set(true, null);
        }
        super.setTimeOfDay(time);
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return DummyClientTickScheduler.get();
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return DummyClientTickScheduler.get();
    }

    @Override
    public ClientChunkManager getChunkManager() {
        return (ClientChunkManager)super.getChunkManager();
    }

    @Override
    @Nullable
    public MapState getMapState(String id) {
        return this.mapStates.get(id);
    }

    @Override
    public void putMapState(MapState mapState) {
        this.mapStates.put(mapState.getId(), mapState);
    }

    @Override
    public int getNextMapId() {
        return 0;
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    @Override
    public RegistryTagManager getTagManager() {
        return this.netHandler.getTagManager();
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.worldRenderer.updateBlock(this, pos, oldState, newState, flags);
    }

    @Override
    public void checkBlockRerender(BlockPos pos, BlockState old, BlockState updated) {
        this.worldRenderer.method_21596(pos, old, updated);
    }

    public void scheduleBlockRenders(int x, int y, int z) {
        this.worldRenderer.scheduleBlockRenders(x, y, z);
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        this.worldRenderer.setBlockBreakingInfo(entityId, pos, progress);
    }

    @Override
    public void playGlobalEvent(int type, BlockPos pos, int data) {
        this.worldRenderer.playGlobalEvent(type, pos, data);
    }

    @Override
    public void playLevelEvent(@Nullable PlayerEntity player, int eventId, BlockPos blockPos, int data) {
        try {
            this.worldRenderer.playLevelEvent(player, eventId, blockPos, data);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Playing level event");
            CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
            crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(blockPos));
            crashReportSection.add("Event source", player);
            crashReportSection.add("Event type", eventId);
            crashReportSection.add("Event data", data);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, false, true, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void addImportantParticle(ParticleEffect parameters, boolean alwaysSpawn, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || alwaysSpawn, true, x, y, z, velocityX, velocityY, velocityZ);
    }

    public List<AbstractClientPlayerEntity> getPlayers() {
        return this.players;
    }

    @Override
    public /* synthetic */ ChunkManager getChunkManager() {
        return this.getChunkManager();
    }
}

