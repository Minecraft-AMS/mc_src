/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AnimalMateGoal
extends Goal {
    private static final TargetPredicate VALID_MATE_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeInvulnerable().includeTeammates().includeHidden();
    protected final AnimalEntity animal;
    private final Class<? extends AnimalEntity> entityClass;
    protected final World world;
    protected AnimalEntity mate;
    private int timer;
    private final double chance;

    public AnimalMateGoal(AnimalEntity animal, double chance) {
        this(animal, chance, animal.getClass());
    }

    public AnimalMateGoal(AnimalEntity animal, double chance, Class<? extends AnimalEntity> entityClass) {
        this.animal = animal;
        this.world = animal.world;
        this.entityClass = entityClass;
        this.chance = chance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.animal.isInLove()) {
            return false;
        }
        this.mate = this.findMate();
        return this.mate != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
    }

    @Override
    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().lookAt(this.mate, 10.0f, this.animal.getLookPitchSpeed());
        this.animal.getNavigation().startMovingTo(this.mate, this.chance);
        ++this.timer;
        if (this.timer >= 60 && this.animal.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private AnimalEntity findMate() {
        List<? extends AnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.animal, this.animal.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        AnimalEntity animalEntity = null;
        for (AnimalEntity animalEntity2 : list) {
            if (!this.animal.canBreedWith(animalEntity2) || !(this.animal.squaredDistanceTo(animalEntity2) < d)) continue;
            animalEntity = animalEntity2;
            d = this.animal.squaredDistanceTo(animalEntity2);
        }
        return animalEntity;
    }

    protected void breed() {
        PassiveEntity passiveEntity = this.animal.createChild(this.mate);
        if (passiveEntity == null) {
            return;
        }
        ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
        if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
            serverPlayerEntity = this.mate.getLovingPlayer();
        }
        if (serverPlayerEntity != null) {
            serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
            Criterions.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, passiveEntity);
        }
        this.animal.setBreedingAge(6000);
        this.mate.setBreedingAge(6000);
        this.animal.resetLoveTicks();
        this.mate.resetLoveTicks();
        passiveEntity.setBreedingAge(-24000);
        passiveEntity.refreshPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0f, 0.0f);
        this.world.spawnEntity(passiveEntity);
        this.world.sendEntityStatus(this.animal, (byte)18);
        if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, this.animal.getRandom().nextInt(7) + 1));
        }
    }
}

