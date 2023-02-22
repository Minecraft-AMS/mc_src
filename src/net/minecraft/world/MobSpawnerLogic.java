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
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WeightedPicker;
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
    private Entity renderedEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    @Nullable
    private Identifier getEntityId() {
        String string = this.spawnEntry.getEntityTag().getString("id");
        try {
            return ChatUtil.isEmpty(string) ? null : new Identifier(string);
        }
        catch (InvalidIdentifierException invalidIdentifierException) {
            BlockPos blockPos = this.getPos();
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", (Object)string, (Object)this.getWorld().dimension.getType(), (Object)blockPos.getX(), (Object)blockPos.getY(), (Object)blockPos.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> entityType) {
        this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
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
        if (world.isClient) {
            double d = (float)blockPos.getX() + world.random.nextFloat();
            double e = (float)blockPos.getY() + world.random.nextFloat();
            double f = (float)blockPos.getZ() + world.random.nextFloat();
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
                CompoundTag compoundTag = this.spawnEntry.getEntityTag();
                Optional<EntityType<?>> optional = EntityType.fromTag(compoundTag);
                if (!optional.isPresent()) {
                    this.updateSpawns();
                    return;
                }
                ListTag listTag = compoundTag.getList("Pos", 6);
                int j = listTag.size();
                double g = j >= 1 ? listTag.getDouble(0) : (double)blockPos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
                double h = j >= 2 ? listTag.getDouble(1) : (double)(blockPos.getY() + world.random.nextInt(3) - 1);
                double d = k = j >= 3 ? listTag.getDouble(2) : (double)blockPos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
                if (!world.doesNotCollide(optional.get().createSimpleBoundingBox(g, h, k)) || !SpawnRestriction.method_20638(optional.get(), world.getWorld(), SpawnType.SPAWNER, new BlockPos(g, h, k), world.getRandom())) continue;
                Entity entity2 = EntityType.loadEntityWithPassengers(compoundTag, world, entity -> {
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
                entity2.refreshPositionAndAngles(entity2.x, entity2.y, entity2.z, world.random.nextFloat() * 360.0f, 0.0f);
                if (entity2 instanceof MobEntity) {
                    MobEntity mobEntity = (MobEntity)entity2;
                    if (!mobEntity.canSpawn(world, SpawnType.SPAWNER) || !mobEntity.canSpawn(world)) continue;
                    if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8)) {
                        ((MobEntity)entity2).initialize(world, world.getLocalDifficulty(new BlockPos(entity2)), SpawnType.SPAWNER, null, null);
                    }
                }
                this.spawnEntity(entity2);
                world.playLevelEvent(2004, blockPos, 0);
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

    private void spawnEntity(Entity entity) {
        if (!this.getWorld().spawnEntity(entity)) {
            return;
        }
        for (Entity entity2 : entity.getPassengerList()) {
            this.spawnEntity(entity2);
        }
    }

    private void updateSpawns() {
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        this.sendStatus(1);
    }

    public void deserialize(CompoundTag compoundTag) {
        this.spawnDelay = compoundTag.getShort("Delay");
        this.spawnPotentials.clear();
        if (compoundTag.contains("SpawnPotentials", 9)) {
            ListTag listTag = compoundTag.getList("SpawnPotentials", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                this.spawnPotentials.add(new MobSpawnerEntry(listTag.getCompound(i)));
            }
        }
        if (compoundTag.contains("SpawnData", 10)) {
            this.setSpawnEntry(new MobSpawnerEntry(1, compoundTag.getCompound("SpawnData")));
        } else if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        if (compoundTag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
            this.spawnCount = compoundTag.getShort("SpawnCount");
        }
        if (compoundTag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
        }
        if (compoundTag.contains("SpawnRange", 99)) {
            this.spawnRange = compoundTag.getShort("SpawnRange");
        }
        if (this.getWorld() != null) {
            this.renderedEntity = null;
        }
    }

    public CompoundTag serialize(CompoundTag compoundTag) {
        Identifier identifier = this.getEntityId();
        if (identifier == null) {
            return compoundTag;
        }
        compoundTag.putShort("Delay", (short)this.spawnDelay);
        compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        compoundTag.putShort("SpawnCount", (short)this.spawnCount);
        compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        compoundTag.putShort("SpawnRange", (short)this.spawnRange);
        compoundTag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
        ListTag listTag = new ListTag();
        if (this.spawnPotentials.isEmpty()) {
            listTag.add(this.spawnEntry.serialize());
        } else {
            for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
                listTag.add(mobSpawnerEntry.serialize());
            }
        }
        compoundTag.put("SpawnPotentials", listTag);
        return compoundTag;
    }

    @Environment(value=EnvType.CLIENT)
    public Entity getRenderedEntity() {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityTag(), this.getWorld(), Function.identity());
            if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8) && this.renderedEntity instanceof MobEntity) {
                ((MobEntity)this.renderedEntity).initialize(this.getWorld(), this.getWorld().getLocalDifficulty(new BlockPos(this.renderedEntity)), SpawnType.SPAWNER, null, null);
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
