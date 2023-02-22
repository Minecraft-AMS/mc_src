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
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntitySelector {
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
    @Nullable
    private final EntityType<?> type;
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
        this.type = type;
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

    private void checkSourcePermission(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        if (this.usesAt && !serverCommandSource.hasPermissionLevel(2)) {
            throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
        }
    }

    public Entity getEntity(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.checkSourcePermission(serverCommandSource);
        List<? extends Entity> list = this.getEntities(serverCommandSource);
        if (list.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        if (list.size() > 1) {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<? extends Entity> getEntities(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.checkSourcePermission(serverCommandSource);
        if (!this.includesNonPlayers) {
            return this.getPlayers(serverCommandSource);
        }
        if (this.playerName != null) {
            ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        if (this.uuid != null) {
            for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
                Entity entity = serverWorld.getEntity(this.uuid);
                if (entity == null) continue;
                return Lists.newArrayList((Object[])new Entity[]{entity});
            }
            return Collections.emptyList();
        }
        Vec3d vec3d = this.positionOffset.apply(serverCommandSource.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
        if (this.senderOnly) {
            if (serverCommandSource.getEntity() != null && predicate.test(serverCommandSource.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[]{serverCommandSource.getEntity()});
            }
            return Collections.emptyList();
        }
        ArrayList list = Lists.newArrayList();
        if (this.isLocalWorldOnly()) {
            this.appendEntitiesFromWorld(list, serverCommandSource.getWorld(), vec3d, predicate);
        } else {
            for (ServerWorld serverWorld2 : serverCommandSource.getMinecraftServer().getWorlds()) {
                this.appendEntitiesFromWorld(list, serverWorld2, vec3d, predicate);
            }
        }
        return this.getEntities(vec3d, list);
    }

    private void appendEntitiesFromWorld(List<Entity> list, ServerWorld serverWorld, Vec3d vec3d, Predicate<Entity> predicate) {
        if (this.box != null) {
            list.addAll(serverWorld.getEntities(this.type, this.box.offset(vec3d), predicate));
        } else {
            list.addAll(serverWorld.getEntities(this.type, predicate));
        }
    }

    public ServerPlayerEntity getPlayer(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        this.checkSourcePermission(serverCommandSource);
        List<ServerPlayerEntity> list = this.getPlayers(serverCommandSource);
        if (list.size() != 1) {
            throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
        }
        return list.get(0);
    }

    public List<ServerPlayerEntity> getPlayers(ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        List<Object> list;
        this.checkSourcePermission(serverCommandSource);
        if (this.playerName != null) {
            ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.playerName);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        if (this.uuid != null) {
            ServerPlayerEntity serverPlayerEntity = serverCommandSource.getMinecraftServer().getPlayerManager().getPlayer(this.uuid);
            if (serverPlayerEntity == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity});
        }
        Vec3d vec3d = this.positionOffset.apply(serverCommandSource.getPosition());
        Predicate<Entity> predicate = this.getPositionPredicate(vec3d);
        if (this.senderOnly) {
            ServerPlayerEntity serverPlayerEntity2;
            if (serverCommandSource.getEntity() instanceof ServerPlayerEntity && predicate.test(serverPlayerEntity2 = (ServerPlayerEntity)serverCommandSource.getEntity())) {
                return Lists.newArrayList((Object[])new ServerPlayerEntity[]{serverPlayerEntity2});
            }
            return Collections.emptyList();
        }
        if (this.isLocalWorldOnly()) {
            list = serverCommandSource.getWorld().getPlayers(predicate::test);
        } else {
            list = Lists.newArrayList();
            for (ServerPlayerEntity serverPlayerEntity3 : serverCommandSource.getMinecraftServer().getPlayerManager().getPlayerList()) {
                if (!predicate.test(serverPlayerEntity3)) continue;
                list.add(serverPlayerEntity3);
            }
        }
        return this.getEntities(vec3d, list);
    }

    private Predicate<Entity> getPositionPredicate(Vec3d vec3d) {
        Predicate<Entity> predicate = this.basePredicate;
        if (this.box != null) {
            Box box = this.box.offset(vec3d);
            predicate = predicate.and(entity -> box.intersects(entity.getBoundingBox()));
        }
        if (!this.distance.isDummy()) {
            predicate = predicate.and(entity -> this.distance.matchesSquared(entity.squaredDistanceTo(vec3d)));
        }
        return predicate;
    }

    private <T extends Entity> List<T> getEntities(Vec3d vec3d, List<T> list) {
        if (list.size() > 1) {
            this.sorter.accept(vec3d, list);
        }
        return list.subList(0, Math.min(this.limit, list.size()));
    }

    public static Text getNames(List<? extends Entity> list) {
        return Texts.join(list, Entity::getDisplayName);
    }
}

