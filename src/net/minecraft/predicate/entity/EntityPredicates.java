/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;
import org.jetbrains.annotations.Nullable;

public final class EntityPredicates {
    public static final Predicate<Entity> VALID_ENTITY = Entity::isAlive;
    public static final Predicate<Entity> VALID_LIVING_ENTITY = entity -> entity.isAlive() && entity instanceof LivingEntity;
    public static final Predicate<Entity> NOT_MOUNTED = entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle();
    public static final Predicate<Entity> VALID_INVENTORIES = entity -> entity instanceof Inventory && entity.isAlive();
    public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = entity -> !(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
    public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();
    public static final Predicate<Entity> CAN_COLLIDE = EXCEPT_SPECTATOR.and(Entity::isCollidable);

    private EntityPredicates() {
    }

    public static Predicate<Entity> maxDistance(double x, double y, double z, double max) {
        double d = max * max;
        return entity -> entity != null && entity.squaredDistanceTo(x, y, z) <= d;
    }

    public static Predicate<Entity> canBePushedBy(Entity entity2) {
        AbstractTeam.CollisionRule collisionRule;
        AbstractTeam abstractTeam = entity2.getScoreboardTeam();
        AbstractTeam.CollisionRule collisionRule2 = collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.getCollisionRule();
        if (collisionRule == AbstractTeam.CollisionRule.NEVER) {
            return Predicates.alwaysFalse();
        }
        return EXCEPT_SPECTATOR.and(entity -> {
            boolean bl;
            AbstractTeam.CollisionRule collisionRule2;
            if (!entity.isPushable()) {
                return false;
            }
            if (!(!entity2.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).isMainPlayer())) {
                return false;
            }
            AbstractTeam abstractTeam2 = entity.getScoreboardTeam();
            AbstractTeam.CollisionRule collisionRule3 = collisionRule2 = abstractTeam2 == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam2.getCollisionRule();
            if (collisionRule2 == AbstractTeam.CollisionRule.NEVER) {
                return false;
            }
            boolean bl2 = bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam2);
            if ((collisionRule == AbstractTeam.CollisionRule.PUSH_OWN_TEAM || collisionRule2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM) && bl) {
                return false;
            }
            return collisionRule != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS || bl;
        });
    }

    public static Predicate<Entity> rides(Entity entity) {
        return entity2 -> {
            while (entity2.hasVehicle()) {
                if ((entity2 = entity2.getVehicle()) != entity) continue;
                return false;
            }
            return true;
        };
    }

    public static class Equipable
    implements Predicate<Entity> {
        private final ItemStack stack;

        public Equipable(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public boolean test(@Nullable Entity entity) {
            if (!entity.isAlive()) {
                return false;
            }
            if (!(entity instanceof LivingEntity)) {
                return false;
            }
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.canEquip(this.stack);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object context) {
            return this.test((Entity)context);
        }
    }
}

