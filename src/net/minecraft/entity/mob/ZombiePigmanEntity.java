/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZombiePigmanEntity
extends ZombieEntity {
    private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION).setSerialize(false);
    private int anger;
    private int angrySoundDelay;
    private UUID angerTarget;

    public ZombiePigmanEntity(EntityType<? extends ZombiePigmanEntity> entityType, World world) {
        super((EntityType<? extends ZombieEntity>)entityType, world);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        super.setAttacker(attacker);
        if (attacker != null) {
            this.angerTarget = attacker.getUuid();
        }
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new AvoidZombiesGoal(this));
        this.targetSelector.add(2, new FollowPlayerIfAngryGoal(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(SPAWN_REINFORCEMENTS).setBaseValue(0.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23f);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    protected void mobTick() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        LivingEntity livingEntity = this.getAttacker();
        if (this.isAngry()) {
            LivingEntity livingEntity2;
            if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addModifier(ATTACKING_SPEED_BOOST);
            }
            --this.anger;
            LivingEntity livingEntity3 = livingEntity2 = livingEntity != null ? livingEntity : this.getTarget();
            if (!this.isAngry() && livingEntity2 != null) {
                if (!this.canSee(livingEntity2)) {
                    this.setAttacker(null);
                    this.setTarget(null);
                } else {
                    this.anger = this.method_20806();
                }
            }
        } else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        }
        if (this.angrySoundDelay > 0 && --this.angrySoundDelay == 0) {
            this.playSound(SoundEvents.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 1.8f);
        }
        if (this.isAngry() && this.angerTarget != null && livingEntity == null) {
            PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(playerEntity);
            this.attackingPlayer = playerEntity;
            this.playerHitTimer = this.getLastAttackedTime();
        }
        super.mobTick();
    }

    public static boolean method_20682(EntityType<ZombiePigmanEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getDifficulty() != Difficulty.PEACEFUL;
    }

    @Override
    public boolean canSpawn(CollisionView world) {
        return world.intersectsEntities(this) && !world.intersectsFluid(this.getBoundingBox());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putShort("Anger", (short)this.anger);
        if (this.angerTarget != null) {
            tag.putString("HurtBy", this.angerTarget.toString());
        } else {
            tag.putString("HurtBy", "");
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.anger = tag.getShort("Anger");
        String string = tag.getString("HurtBy");
        if (!string.isEmpty()) {
            this.angerTarget = UUID.fromString(string);
            PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(playerEntity);
            if (playerEntity != null) {
                this.attackingPlayer = playerEntity;
                this.playerHitTimer = this.getLastAttackedTime();
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        Entity entity = source.getAttacker();
        if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative() && this.canSee(entity)) {
            this.method_20804(entity);
        }
        return super.damage(source, amount);
    }

    private boolean method_20804(Entity entity) {
        this.anger = this.method_20806();
        this.angrySoundDelay = this.random.nextInt(40);
        if (entity instanceof LivingEntity) {
            this.setAttacker((LivingEntity)entity);
        }
        return true;
    }

    private int method_20806() {
        return 400 + this.random.nextInt(400);
    }

    private boolean isAngry() {
        return this.anger > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_PIGMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_PIGMAN_DEATH;
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        return false;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isAngryAt(PlayerEntity player) {
        return this.isAngry();
    }

    static class FollowPlayerIfAngryGoal
    extends FollowTargetGoal<PlayerEntity> {
        public FollowPlayerIfAngryGoal(ZombiePigmanEntity pigman) {
            super((MobEntity)pigman, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            return ((ZombiePigmanEntity)this.mob).isAngry() && super.canStart();
        }
    }

    static class AvoidZombiesGoal
    extends RevengeGoal {
        public AvoidZombiesGoal(ZombiePigmanEntity pigman) {
            super(pigman, new Class[0]);
            this.setGroupRevenge(ZombieEntity.class);
        }

        @Override
        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof ZombiePigmanEntity && this.mob.canSee(target) && ((ZombiePigmanEntity)mob).method_20804(target)) {
                mob.setTarget(target);
            }
        }
    }
}
