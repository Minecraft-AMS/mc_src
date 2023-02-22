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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class VibrationListener
implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    @Nullable
    protected Vibration vibration;
    protected float distance;
    protected int delay;

    public static Codec<VibrationListener> createCodec(Callback callback) {
        return RecordCodecBuilder.create(instance -> instance.group((App)PositionSource.CODEC.fieldOf("source").forGetter(listener -> listener.positionSource), (App)Codecs.NONNEGATIVE_INT.fieldOf("range").forGetter(listener -> listener.range), (App)Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).fieldOf("event_distance").orElse((Object)Float.valueOf(0.0f)).forGetter(listener -> Float.valueOf(listener.distance)), (App)Codecs.NONNEGATIVE_INT.fieldOf("event_delay").orElse((Object)0).forGetter(listener -> listener.delay)).apply((Applicative)instance, (positionSource, range, vibration, distance, delay) -> new VibrationListener((PositionSource)positionSource, (int)range, callback, vibration.orElse(null), distance.floatValue(), (int)delay)));
    }

    public VibrationListener(PositionSource positionSource, int range, Callback callback, @Nullable Vibration vibration, float distance, int delay) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.vibration = vibration;
        this.distance = distance;
        this.delay = delay;
    }

    public void tick(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (this.vibration != null) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.callback.accept(serverWorld, this, new BlockPos(this.vibration.pos), this.vibration.gameEvent, this.vibration.getEntity(serverWorld).orElse(null), this.vibration.getOwner(serverWorld).orElse(null), this.distance);
                    this.vibration = null;
                }
            }
        }
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(ServerWorld world, GameEvent.Message event) {
        GameEvent.Emitter emitter;
        if (this.vibration != null) {
            return false;
        }
        GameEvent gameEvent = event.getEvent();
        if (!this.callback.canAccept(gameEvent, emitter = event.getEmitter())) {
            return false;
        }
        Optional<Vec3d> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        Vec3d vec3d = event.getEmitterPos();
        Vec3d vec3d2 = optional.get();
        if (!this.callback.accepts(world, this, new BlockPos(vec3d), gameEvent, emitter)) {
            return false;
        }
        if (VibrationListener.isOccluded(world, vec3d, vec3d2)) {
            return false;
        }
        this.listen(world, gameEvent, emitter, vec3d, vec3d2);
        return true;
    }

    private void listen(ServerWorld world, GameEvent gameEvent, GameEvent.Emitter emitter, Vec3d start, Vec3d end) {
        this.distance = (float)start.distanceTo(end);
        this.vibration = new Vibration(gameEvent, this.distance, start, emitter.sourceEntity());
        this.delay = MathHelper.floor(this.distance);
        world.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
        this.callback.onListen();
    }

    private static boolean isOccluded(World world, Vec3d start, Vec3d end) {
        Vec3d vec3d = new Vec3d((double)MathHelper.floor(start.x) + 0.5, (double)MathHelper.floor(start.y) + 0.5, (double)MathHelper.floor(start.z) + 0.5);
        Vec3d vec3d2 = new Vec3d((double)MathHelper.floor(end.x) + 0.5, (double)MathHelper.floor(end.y) + 0.5, (double)MathHelper.floor(end.z) + 0.5);
        for (Direction direction : Direction.values()) {
            Vec3d vec3d3 = vec3d.withBias(direction, 1.0E-5f);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK) continue;
            return false;
        }
        return true;
    }

    public static interface Callback {
        default public TagKey<GameEvent> getTag() {
            return GameEventTags.VIBRATIONS;
        }

        default public boolean triggersAvoidCriterion() {
            return false;
        }

        default public boolean canAccept(GameEvent gameEvent, GameEvent.Emitter emitter) {
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            }
            Entity entity = emitter.sourceEntity();
            if (entity != null) {
                if (entity.isSpectator()) {
                    return false;
                }
                if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                    if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                        Criteria.AVOID_VIBRATION.trigger(serverPlayerEntity);
                    }
                    return false;
                }
                if (entity.occludeVibrationSignals()) {
                    return false;
                }
            }
            if (emitter.affectedState() != null) {
                return !emitter.affectedState().isIn(BlockTags.DAMPENS_VIBRATIONS);
            }
            return true;
        }

        public boolean accepts(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Emitter var5);

        public void accept(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7);

        default public void onListen() {
        }
    }

    public static final class Vibration
    extends Record {
        final GameEvent gameEvent;
        private final float distance;
        final Vec3d pos;
        @Nullable
        private final UUID uuid;
        @Nullable
        private final UUID projectileOwnerUuid;
        @Nullable
        private final Entity entity;
        public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.GAME_EVENT.getCodec().fieldOf("game_event").forGetter(Vibration::gameEvent), (App)Codec.floatRange((float)0.0f, (float)Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance), (App)Vec3d.CODEC.fieldOf("pos").forGetter(Vibration::pos), (App)Codecs.UUID.optionalFieldOf("source").forGetter(vibration -> Optional.ofNullable(vibration.uuid())), (App)Codecs.UUID.optionalFieldOf("projectile_owner").forGetter(vibration -> Optional.ofNullable(vibration.projectileOwnerUuid()))).apply((Applicative)instance, (event, distance, pos, uuid, projectileOwnerUuid) -> new Vibration((GameEvent)event, distance.floatValue(), (Vec3d)pos, uuid.orElse(null), projectileOwnerUuid.orElse(null))));

        public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
            this(gameEvent, distance, pos, uuid, projectileOwnerUuid, null);
        }

        public Vibration(GameEvent gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
            this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), Vibration.getOwnerUuid(entity), entity);
        }

        public Vibration(GameEvent gameEvent, float f, Vec3d vec3d, @Nullable UUID uUID, @Nullable UUID uUID2, @Nullable Entity entity) {
            this.gameEvent = gameEvent;
            this.distance = f;
            this.pos = vec3d;
            this.uuid = uUID;
            this.projectileOwnerUuid = uUID2;
            this.entity = entity;
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

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Vibration.class, "gameEvent;distance;pos;uuid;projectileOwnerUuid;entity", "gameEvent", "distance", "pos", "uuid", "projectileOwnerUuid", "entity"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Vibration.class, "gameEvent;distance;pos;uuid;projectileOwnerUuid;entity", "gameEvent", "distance", "pos", "uuid", "projectileOwnerUuid", "entity"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Vibration.class, "gameEvent;distance;pos;uuid;projectileOwnerUuid;entity", "gameEvent", "distance", "pos", "uuid", "projectileOwnerUuid", "entity"}, this, object);
        }

        public GameEvent gameEvent() {
            return this.gameEvent;
        }

        public float distance() {
            return this.distance;
        }

        public Vec3d pos() {
            return this.pos;
        }

        @Nullable
        public UUID uuid() {
            return this.uuid;
        }

        @Nullable
        public UUID projectileOwnerUuid() {
            return this.projectileOwnerUuid;
        }

        @Nullable
        public Entity entity() {
            return this.entity;
        }
    }
}

