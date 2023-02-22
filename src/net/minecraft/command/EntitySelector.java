/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntitySelector {
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final BiConsumer<Vec3d, List<? extends Entity>> ARBITRARY = (pos, entities) -> {};
    private static final TypeFilter<Entity, ?> PASSTHROUGH_FILTER = new TypeFilter<Entity, Entity>(){

        @Override
        public Entity downcast(Entity entity) {
            return entity;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    private final int limit;
    private final boolean includesNonPlayers;
    private final boolean localWorldOnly;
    private final Predicate<Entity> basePredicate;
    private final NumberRange.FloatRange distance;
    private final Function<Vec3d, Vec3d> positionOffset;
    @Nullable
    private final Box box;
    private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
    private final boolean senderOnly;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID uuid;
    private final TypeFilter<Entity, ?> entityFilter;
    private final boolean usesAt;

    public EntitySelector(int count, boolean includesNonPlayers, boolean localWorldOnly, Predicate<Entity> basePredicate, NumberRange.FloatRange distance, Function<Vec3d, Vec3d> positionOffset, @Nullable Box box, BiConsumer<Vec3d, List<? extends Entity>> sorter, boolean senderOnly, @Nullable String playerName, @Nullable UUID uuid, @Nullable EntityType<?> type, boolean usesAt) {
        this.limit = count;
        this.includesNonPlayers = includesNonPlayers;
        this.localWorldOnly = localWorldOnly;
        this.basePredicate = basePredicate;
        this.distance = distance;
        this.positionOffset = positionOffset;
        this.box = box;
        this.sorter = sorter;
        this.senderOnly = senderOnly;
        this.playerName = playerName;
        this.uuid = uuid;
        this.entityFilter = type == null ? PASSTHROUGH_FILTER : type;
        this.usesAt = usesAt;
    }

    public int getLimit() {
        return this.limit;
    }

    public boolean includesNonPlayers() {
        return this.includesNonPlayers;
    }

    public boolean isSenderOnly() {
        return this.senderOnly;
    }

    public boolean isLocalWorldOnly() {
        return this.localWorldOnly;
    }

    public boolean usesAt() {
        return this.usesAt;
    }

    private void checkSourcePermission(ServerCommandSource source) throws CommandSyntaxException {
        if (this.usesAt && !source.hasPermissionLevel(2)) {
            throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
        }
    }

    public Entity getEntity(ServerCommandSource source) throws CommandSyntaxException {
        this.checkSourcePermission(source);
        List<? extends Entity> list = this.getEntities(source);
        if (list.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        if (list.size() > 1) {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<? extends Entity> getEntities(ServerCommandSource source) throws CommandSyntaxException {
        return this.getUnfilteredEntities(source).stream().filter(entity -> entity.getType().isEnabled(source.getEnabledFeatures())).toList();
    }

    private List<? extends Entity> getUnfilteredEntities(ServerCommandSource source) throws CommandSyntaxException {
        this.checkSourcePermission(source);
        if (!this.includesNonPlayers) {
            return this.getPlayers(source);
        }
        if (this.playerName != null) {
            ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        if (this.uuid != null) {
            for (ServerWorld serverWorld : source.getServer().getWorlds()) {
                Entity entity = serverWorld.getEntity(this.uuid);
                if (entity == null) continue;
                return Lists.newArrayList((Object[])new Entity[]{entity});
            }
            return Collections.emptyList();
        }
        Vec3d vec3d = this.positionOffset.apply(source.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
        if (this.senderOnly) {
            if (source.getEntity() != null && predicate.test(source.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[]{source.getEntity()});
            }
            return Collections.emptyList();
        }
        ArrayList list = Lists.newArrayList();
        if (this.isLocalWorldOnly()) {
            this.appendEntitiesFromWorld(list, source.getWorld(), vec3d, predicate);
        } else {
            for (ServerWorld serverWorld2 : source.getServer().getWorlds()) {
                this.appendEntitiesFromWorld(list, serverWorld2, vec3d, predicate);
            }
        }
        return this.getEntities(vec3d, list);
    }

    private void appendEntitiesFromWorld(List<Entity> entities, ServerWorld world, Vec3d pos, Predicate<Entity> predicate) {
        int i = this.getAppendLimit();
        if (entities.size() >= i) {
            return;
        }
        if (this.box != null) {
            world.collectEntitiesByType(this.entityFilter, this.box.offset(pos), predicate, entities, i);
        } else {
            world.collectEntitiesByType(this.entityFilter, predicate, entities, i);
        }
    }

    private int getAppendLimit() {
        return this.sorter == ARBITRARY ? this.limit : Integer.MAX_VALUE;
    }

    public ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
        this.checkSourcePermission(source);
        List<ServerPlayerEntity> list = this.getPlayers(source);
        if (list.size() != 1) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<ServerPlayerEntity> getPlayers(ServerCommandSource source) throws CommandSyntaxException {
        List<Object> list;
        this.checkSourcePermission(source);
        if (this.playerName != null) {
            ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        if (this.uuid != null) {
            ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(this.uuid);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        Vec3d vec3d = this.positionOffset.apply(source.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
        if (this.senderOnly) {
            ServerPlayerEntity serverPlayerEntity2;
            if (source.getEntity() instanceof ServerPlayerEntity && predicate.test(serverPlayerEntity2 = (ServerPlayerEntity)source.getEntity())) {
                return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity2});
            }
            return Collections.emptyList();
        }
        int i = this.getAppendLimit();
        if (this.isLocalWorldOnly()) {
            list = source.getWorld().getPlayers(predicate, i);
        } else {
            list = Lists.newArrayList();
            for (ServerPlayerEntity serverPlayerEntity3 : source.getServer().getPlayerManager().getPlayerList()) {
                if (!predicate.test(serverPlayerEntity3)) continue;
                list.add(serverPlayerEntity3);
                if (list.size() < i) continue;
                return list;
            }
        }
        return this.getEntities(vec3d, list);
    }

    private Predicate<Entity> getPositionPredicate(Vec3d pos) {
        Predicate<Entity> predicate = this.basePredicate;
        if (this.box != null) {
            Box box = this.box.offset(pos);
            predicate = predicate.and(entity -> box.intersects(entity.getBoundingBox()));
        }
        if (!this.distance.isDummy()) {
            predicate = predicate.and(entity -> this.distance.testSqrt(entity.squaredDistanceTo(pos)));
        }
        return predicate;
    }

    private <T extends Entity> List<T> getEntities(Vec3d pos, List<T> entities) {
        if (entities.size() > 1) {
            this.sorter.accept(pos, entities);
        }
        return entities.subList(0, Math.min(this.limit, entities.size()));
    }

    public static Text getNames(List<? extends Entity> entities) {
        return Texts.join(entities, Entity::getDisplayName);
    }
}

