/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class MobSpawnerLogic {
    private static final Logger LOGGER = LogManager.getLogger();
    private int spawnDelay = 20;
    private final List<MobSpawnerEntry> spawnPotentials = Lists.newArrayList();
    private MobSpawnerEntry spawnEntry = new MobSpawnerEntry();
    private double field_9161;
    private double field_9159;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity renderedEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    @Nullable
    private Identifier getEntityId() {
        String string = this.spawnEntry.getEntityNbt().getString("id");
        try {
            return ChatUtil.isEmpty(string) ? null : new Identifier(string);
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            BlockPos blockPos = this.getPos();
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", (Object)string, (Object)this.getWorld().getRegistryKey().getValue(), (Object)blockPos.getX(), (Object)blockPos.getY(), (Object)blockPos.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getEntityNbt().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    private boolean isPlayerInRange() {
        BlockPos blockPos = this.getPos();
        return this.getWorld().isPlayerInRange((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void update() {
        if (!this.isPlayerInRange()) {
            this.field_9159 = this.field_9161;
            return;
        }
        World world = this.getWorld();
        BlockPos blockPos = this.getPos();
        if (!(world instanceof ServerWorld)) {
            double d = (double)blockPos.getX() + world.random.nextDouble();
            double e = (double)blockPos.getY() + world.random.nextDouble();
            double f = (double)blockPos.getZ() + world.random.nextDouble();
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.field_9159 = this.field_9161;
            this.field_9161 = (this.field_9161 + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        } else {
            if (this.spawnDelay == -1) {
                this.updateSpawns();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            boolean bl = false;
            for (int i = 0; i < this.spawnCount; ++i) {
                double k;
                NbtCompound nbtCompound = this.spawnEntry.getEntityNbt();
                Optional<EntityType<?>> optional = EntityType.fromNbt(nbtCompound);
                if (!optional.isPresent()) {
                    this.updateSpawns();
                    return;
                }
                NbtList nbtList = nbtCompound.getList("Pos", 6);
                int j = nbtList.size();
                double g = j >= 1 ? nbtList.getDouble(0) : (double)blockPos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
                double h = j >= 2 ? nbtList.getDouble(1) : (double)(blockPos.getY() + world.random.nextInt(3) - 1);
                double d = k = j >= 3 ? nbtList.getDouble(2) : (double)blockPos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
                if (!world.isSpaceEmpty(optional.get().createSimpleBoundingBox(g, h, k))) continue;
                ServerWorld serverWorld = (ServerWorld)world;
                if (!SpawnRestriction.canSpawn(optional.get(), serverWorld, SpawnReason.SPAWNER, new BlockPos(g, h, k), world.getRandom())) continue;
                Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, world, entity -> {
                    entity.refreshPositionAndAngles(g, h, k, entity.yaw, entity.pitch);
                    return entity;
                });
                if (entity2 == null) {
                    this.updateSpawns();
                    return;
                }
                int l = world.getNonSpectatingEntities(entity2.getClass(), new Box(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1).expand(this.spawnRange)).size();
                if (l >= this.maxNearbyEntities) {
                    this.updateSpawns();
                    return;
                }
                entity2.refreshPositionAndAngles(entity2.getX(), entity2.getY(), entity2.getZ(), world.random.nextFloat() * 360.0f, 0.0f);
                if (entity2 instanceof MobEntity) {
                    MobEntity mobEntity = (MobEntity)entity2;
                    if (!mobEntity.canSpawn(world, SpawnReason.SPAWNER) || !mobEntity.canSpawn(world)) continue;
                    if (this.spawnEntry.getEntityNbt().getSize() == 1 && this.spawnEntry.getEntityNbt().contains("id", 8)) {
                        ((MobEntity)entity2).initialize(serverWorld, world.getLocalDifficulty(entity2.getBlockPos()), SpawnReason.SPAWNER, null, null);
                    }
                }
                if (!serverWorld.shouldCreateNewEntityWithPassenger(entity2)) {
                    this.updateSpawns();
                    return;
                }
                world.syncWorldEvent(2004, blockPos, 0);
                if (entity2 instanceof MobEntity) {
                    ((MobEntity)entity2).playSpawnEffects();
                }
                bl = true;
            }
            if (bl) {
                this.updateSpawns();
            }
        }
    }

    private void updateSpawns() {
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        this.sendStatus(1);
    }

    public void fromTag(NbtCompound tag) {
        this.spawnDelay = tag.getShort("Delay");
        this.spawnPotentials.clear();
        if (tag.contains("SpawnPotentials", 9)) {
            NbtList nbtList = tag.getList("SpawnPotentials", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                this.spawnPotentials.add(new MobSpawnerEntry(nbtList.getCompound(i)));
            }
        }
        if (tag.contains("SpawnData", 10)) {
            this.setSpawnEntry(new MobSpawnerEntry(1, tag.getCompound("SpawnData")));
        } else if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        if (tag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = tag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            this.spawnCount = tag.getShort("SpawnCount");
        }
        if (tag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = tag.getShort("RequiredPlayerRange");
        }
        if (tag.contains("SpawnRange", 99)) {
            this.spawnRange = tag.getShort("SpawnRange");
        }
        if (this.getWorld() != null) {
            this.renderedEntity = null;
        }
    }

    public NbtCompound toTag(NbtCompound tag) {
        Identifier identifier = this.getEntityId();
        if (identifier == null) {
            return tag;
        }
        tag.putShort("Delay", (short)this.spawnDelay);
        tag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        tag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        tag.putShort("SpawnCount", (short)this.spawnCount);
        tag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        tag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        tag.putShort("SpawnRange", (short)this.spawnRange);
        tag.put("SpawnData", this.spawnEntry.getEntityNbt().copy());
        NbtList nbtList = new NbtList();
        if (this.spawnPotentials.isEmpty()) {
            nbtList.add(this.spawnEntry.toNbt());
        } else {
            for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
                nbtList.add(mobSpawnerEntry.toNbt());
            }
        }
        tag.put("SpawnPotentials", nbtList);
        return tag;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getRenderedEntity() {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityNbt(), this.getWorld(), Function.identity());
            if (this.spawnEntry.getEntityNbt().getSize() != 1 || !this.spawnEntry.getEntityNbt().contains("id", 8) || this.renderedEntity instanceof MobEntity) {
                // empty if block
            }
        }
        return this.renderedEntity;
    }

    public boolean method_8275(int i) {
        if (i == 1 && this.getWorld().isClient) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        return false;
    }

    public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(int var1);

    public abstract World getWorld();

    public abstract BlockPos getPos();

    @Environment(value=EnvType.CLIENT)
    public double method_8278() {
        return this.field_9161;
    }

    @Environment(value=EnvType.CLIENT)
    public double method_8279() {
        return this.field_9159;
    }
}

