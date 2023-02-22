/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.event;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class EntityPositionSource
implements PositionSource {
    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.INT_STREAM_CODEC.fieldOf("source_entity").forGetter(EntityPositionSource::getUuid), (App)Codec.FLOAT.fieldOf("y_offset").orElse((Object)Float.valueOf(0.0f)).forGetter(entityPositionSource -> Float.valueOf(entityPositionSource.yOffset))).apply((Applicative)instance, (uuid, yOffset) -> new EntityPositionSource((Either<Entity, Either<UUID, Integer>>)Either.right((Object)Either.left((Object)uuid)), yOffset.floatValue())));
    private Either<Entity, Either<UUID, Integer>> source;
    final float yOffset;

    public EntityPositionSource(Entity entity, float yOffset) {
        this((Either<Entity, Either<UUID, Integer>>)Either.left((Object)entity), yOffset);
    }

    EntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
        this.source = source;
        this.yOffset = yOffset;
    }

    @Override
    public Optional<Vec3d> getPos(World world) {
        if (this.source.left().isEmpty()) {
            this.findEntityInWorld(world);
        }
        return this.source.left().map(entity -> entity.getPos().add(0.0, this.yOffset, 0.0));
    }

    private void findEntityInWorld(World world) {
        ((Optional)this.source.map(Optional::of, entityId -> Optional.ofNullable((Entity)entityId.map(uuid -> {
            Entity entity;
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                entity = serverWorld.getEntity((UUID)uuid);
            } else {
                entity = null;
            }
            return entity;
        }, world::getEntityById)))).ifPresent(entity -> {
            this.source = Either.left((Object)entity);
        });
    }

    private UUID getUuid() {
        return (UUID)this.source.map(Entity::getUuid, entityId2 -> (UUID)entityId2.map(Function.identity(), entityId -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    int getEntityId() {
        return (Integer)this.source.map(Entity::getId, entityId -> (Integer)entityId.map(uuid -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type
    implements PositionSourceType<EntityPositionSource> {
        @Override
        public EntityPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new EntityPositionSource((Either<Entity, Either<UUID, Integer>>)Either.right((Object)Either.right((Object)packetByteBuf.readVarInt())), packetByteBuf.readFloat());
        }

        @Override
        public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
            packetByteBuf.writeVarInt(entityPositionSource.getEntityId());
            packetByteBuf.writeFloat(entityPositionSource.yOffset);
        }

        @Override
        public Codec<EntityPositionSource> getCodec() {
            return CODEC;
        }

        @Override
        public /* synthetic */ PositionSource readFromBuf(PacketByteBuf buf) {
            return this.readFromBuf(buf);
        }
    }
}

