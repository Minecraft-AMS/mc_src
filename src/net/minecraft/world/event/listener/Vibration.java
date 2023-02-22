/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.event.listener;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public record Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
    public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registries.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(Vibration::gameEvent), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance), (App)Vec3d.CODEC.fieldOf("pos").forGetter(Vibration::pos), (App)Uuids.INT_STREAM_CODEC.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())), (App)Uuids.INT_STREAM_CODEC.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))).apply((Applicative)instance, (event, distance, pos, uuid, projectileOwnerUuid) -> new Vibration((GameEvent)event, distance.floatValue(), (Vec3d)pos, uuid.orElse(null), projectileOwnerUuid.orElse(null))));

    public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
        this(gameEvent, distance, pos, uuid, projectileOwnerUuid, null);
    }

    public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
        this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), Vibration.getOwnerUuid(entity), entity);
    }

    @Nullable
    private static UUID getOwnerUuid(@Nullable Entity entity) {
        ProjectileEntity projectileEntity;
        if (entity instanceof ProjectileEntity && (projectileEntity = (ProjectileEntity)entity).getOwner() != null) {
            return projectileEntity.getOwner().getUuid();
        }
        return null;
    }

    public Optional<Entity> getEntity(ServerWorld world) {
        return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(world::getEntity));
    }

    public Optional<Entity> getOwner(ServerWorld world) {
        return this.getEntity(world).filter(entity -> entity instanceof ProjectileEntity).map(entity -> (ProjectileEntity)entity).map(ProjectileEntity::getOwner).or(() -> Optional.ofNullable(this.projectileOwnerUuid).map(world::getEntity));
    }
}

