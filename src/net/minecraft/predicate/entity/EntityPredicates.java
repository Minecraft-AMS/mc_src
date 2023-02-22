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
    public static final Predicate<LivingEntity> VALID_ENTITY_LIVING = LivingEntity::isAlive;
    public static final Predicate<Entity> NOT_MOUNTED = entity -> entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle();
    public static final Predicate<Entity> VALID_INVENTORIES = entity -> entity instanceof Inventory && entity.isAlive();
    public static final Predicate<Entity> EXCEPT_CREATIVE_OR_SPECTATOR = entity -> !(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
    public static final Predicate<Entity> EXCEPT_SPECTATOR = entity -> !entity.isSpectator();

    public static Predicate<Entity> maximumDistance(double x, double d, double e, double f) {
        double g = f * f;
        return entity -> entity != null && entity.squaredDistanceTo(x, d, e) <= g;
    }

    public static Predicate<Entity> canBePushedBy(Entity entity) {
        AbstractTeam.CollisionRule collisionRule;
        AbstractTeam abstractTeam = entity.getScoreboardTeam();
        AbstractTeam.CollisionRule collisionRule2 = collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.getCollisionRule();
        if (collisionRule == AbstractTeam.CollisionRule.NEVER) {
            return Predicates.alwaysFalse();
        }
        return EXCEPT_SPECTATOR.and(entity2 -> {
            boolean bl;
            AbstractTeam.CollisionRule collisionRule2;
            if (!entity2.isPushable()) {
                return false;
            }
            if (!(!entity.world.isClient || entity2 instanceof PlayerEntity && ((PlayerEntity)entity2).isMainPlayer())) {
                return false;
            }
            AbstractTeam abstractTeam2 = entity2.getScoreboardTeam();
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

    public static class CanPickup
    implements Predicate<Entity> {
        private final ItemStack itemstack;

        public CanPickup(ItemStack itemStack) {
            this.itemstack = itemStack;
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
            return livingEntity.canPickUp(this.itemstack);
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object context) {
            return this.test((Entity)context);
        }
    }
}

