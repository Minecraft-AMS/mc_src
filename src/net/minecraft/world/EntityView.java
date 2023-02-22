/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public interface EntityView {
    public List<Entity> getEntities(@Nullable Entity var1, Box var2, @Nullable Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(Class<? extends T> var1, Box var2, @Nullable Predicate<? super T> var3);

    default public <T extends Entity> List<T> method_21729(Class<? extends T> class_, Box box, @Nullable Predicate<? super T> predicate) {
        return this.getEntities(class_, box, predicate);
    }

    public List<? extends PlayerEntity> getPlayers();

    default public List<Entity> getEntities(@Nullable Entity except, Box box) {
        return this.getEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public boolean intersectsEntities(@Nullable Entity except, VoxelShape shape) {
        if (shape.isEmpty()) {
            return true;
        }
        return this.getEntities(except, shape.getBoundingBox()).stream().filter(e -> !e.removed && e.inanimate && (except == null || !e.isConnectedThroughVehicle(except))).noneMatch(entity -> VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND));
    }

    default public <T extends Entity> List<T> getNonSpectatingEntities(Class<? extends T> entityClass, Box box) {
        return this.getEntities(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public <T extends Entity> List<T> method_21728(Class<? extends T> class_, Box box) {
        return this.method_21729(class_, box, EntityPredicates.EXCEPT_SPECTATOR);
    }

    default public Stream<VoxelShape> method_20743(@Nullable Entity entity, Box box, Set<Entity> set) {
        if (box.getAverageSideLength() < 1.0E-7) {
            return Stream.empty();
        }
        Box box2 = box.expand(1.0E-7);
        return this.getEntities(entity, box2).stream().filter(e -> !set.contains(e)).filter(e -> entity == null || !entity.isConnectedThroughVehicle((Entity)e)).flatMap(e -> Stream.of(e.getCollisionBox(), entity == null ? null : entity.getHardCollisionBox((Entity)e))).filter(Objects::nonNull).filter(box2::intersects).map(VoxelShapes::cuboid);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, @Nullable Predicate<Entity> targetPredicate) {
        double d = -1.0;
        PlayerEntity playerEntity = null;
        for (PlayerEntity playerEntity2 : this.getPlayers()) {
            if (targetPredicate != null && !targetPredicate.test(playerEntity2)) continue;
            double e = playerEntity2.squaredDistanceTo(x, y, z);
            if (!(maxDistance < 0.0) && !(e < maxDistance * maxDistance) || d != -1.0 && !(e < d)) continue;
            d = e;
            playerEntity = playerEntity2;
        }
        return playerEntity;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
        return this.getClosestPlayer(entity.x, entity.y, entity.z, maxDistance, false);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
        Predicate<Entity> predicate = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
        return this.getClosestPlayer(x, y, z, maxDistance, predicate);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(double x, double z, double maxDistance) {
        double d = -1.0;
        PlayerEntity playerEntity = null;
        for (PlayerEntity playerEntity2 : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity2)) continue;
            double e = playerEntity2.squaredDistanceTo(x, playerEntity2.y, z);
            if (!(maxDistance < 0.0) && !(e < maxDistance * maxDistance) || d != -1.0 && !(e < d)) continue;
            d = e;
            playerEntity = playerEntity2;
        }
        return playerEntity;
    }

    default public boolean isPlayerInRange(double x, double y, double z, double range) {
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity) || !EntityPredicates.VALID_ENTITY_LIVING.test(playerEntity)) continue;
            double d = playerEntity.squaredDistanceTo(x, y, z);
            if (!(range < 0.0) && !(d < range * range)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.x, entity.y, entity.z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
    }

    @Nullable
    default public PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
        return this.getClosestEntity(this.getPlayers(), targetPredicate, null, x, y, z);
    }

    @Nullable
    default public <T extends LivingEntity> T method_21726(Class<? extends T> class_, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f, Box box) {
        return this.getClosestEntity(this.getEntities(class_, box, null), targetPredicate, livingEntity, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T method_21727(Class<? extends T> class_, TargetPredicate targetPredicate, @Nullable LivingEntity livingEntity, double d, double e, double f, Box box) {
        return this.getClosestEntity(this.method_21729(class_, box, null), targetPredicate, livingEntity, d, e, f);
    }

    @Nullable
    default public <T extends LivingEntity> T getClosestEntity(List<? extends T> entityList, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z) {
        double d = -1.0;
        LivingEntity livingEntity = null;
        for (LivingEntity livingEntity2 : entityList) {
            if (!targetPredicate.test(entity, livingEntity2)) continue;
            double e = livingEntity2.squaredDistanceTo(x, y, z);
            if (d != -1.0 && !(e < d)) continue;
            d = e;
            livingEntity = livingEntity2;
        }
        return (T)livingEntity;
    }

    default public List<PlayerEntity> getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
        ArrayList list = Lists.newArrayList();
        for (PlayerEntity playerEntity : this.getPlayers()) {
            if (!box.contains(playerEntity.x, playerEntity.y, playerEntity.z) || !targetPredicate.test(entity, playerEntity)) continue;
            list.add(playerEntity);
        }
        return list;
    }

    default public <T extends LivingEntity> List<T> getTargets(Class<? extends T> entityClass, TargetPredicate targetPredicate, LivingEntity targettingEntity, Box box) {
        List<T> list = this.getEntities(entityClass, box, null);
        ArrayList list2 = Lists.newArrayList();
        for (LivingEntity livingEntity : list) {
            if (!targetPredicate.test(targettingEntity, livingEntity)) continue;
            list2.add(livingEntity);
        }
        return list2;
    }

    @Nullable
    default public PlayerEntity getPlayerByUuid(UUID uuid) {
        for (int i = 0; i < this.getPlayers().size(); ++i) {
            PlayerEntity playerEntity = this.getPlayers().get(i);
            if (!uuid.equals(playerEntity.getUuid())) continue;
            return playerEntity;
        }
        return null;
    }
}

