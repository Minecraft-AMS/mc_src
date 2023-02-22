/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.HashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class PillagerEntity
extends IllagerEntity
implements CrossbowUser,
RangedAttackMob {
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final BasicInventory inventory = new BasicInventory(5);

    public PillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super((EntityType<? extends IllagerEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, this, 10.0f));
        this.goalSelector.add(3, new CrossbowAttackGoal<PillagerEntity>(this, 1.0, 8.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35f);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGING, false);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    @Override
    public void setCharging(boolean charging) {
        this.dataTracker.set(CHARGING, charging);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        ListTag listTag = new ListTag();
        for (int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack = this.inventory.getInvStack(i);
            if (itemStack.isEmpty()) continue;
            listTag.add(itemStack.toTag(new CompoundTag()));
        }
        tag.put("Inventory", listTag);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return IllagerEntity.State.CROSSBOW_CHARGE;
        }
        if (this.isHolding(Items.CROSSBOW)) {
            return IllagerEntity.State.CROSSBOW_HOLD;
        }
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        return IllagerEntity.State.NEUTRAL;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        ListTag listTag = tag.getList("Inventory", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            ItemStack itemStack = ItemStack.fromTag(listTag.getCompound(i));
            if (itemStack.isEmpty()) continue;
            this.inventory.add(itemStack);
        }
        this.setCanPickUpLoot(true);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView worldView) {
        Block block = worldView.getBlockState(pos.down()).getBlock();
        if (block == Blocks.GRASS_BLOCK || block == Blocks.SAND) {
            return 10.0f;
        }
        return 0.5f - worldView.getBrightness(pos);
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.initEquipment(difficulty);
        this.updateEnchantments(difficulty);
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        ItemStack itemStack = new ItemStack(Items.CROSSBOW);
        if (this.random.nextInt(300) == 0) {
            HashMap map = Maps.newHashMap();
            map.put(Enchantments.PIERCING, 1);
            EnchantmentHelper.set(map, itemStack);
        }
        this.equipStack(EquipmentSlot.MAINHAND, itemStack);
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    public void attack(LivingEntity target, float f) {
        Hand hand = ProjectileUtil.getHandPossiblyHolding(this, Items.CROSSBOW);
        ItemStack itemStack = this.getStackInHand(hand);
        if (this.isHolding(Items.CROSSBOW)) {
            CrossbowItem.shootAll(this.world, this, hand, itemStack, 1.6f, 14 - this.world.getDifficulty().getId() * 4);
        }
        this.despawnCounter = 0;
    }

    @Override
    public void shoot(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray) {
        Entity entity = (Entity)((Object)projectile);
        double d = target.getX() - this.getX();
        double e = target.getZ() - this.getZ();
        double f = MathHelper.sqrt(d * d + e * e);
        double g = target.getBodyY(0.3333333333333333) - entity.getY() + f * (double)0.2f;
        Vector3f vector3f = this.getProjectileVelocity(new Vec3d(d, g, e), multiShotSpray);
        projectile.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.6f, 14 - this.world.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    private Vector3f getProjectileVelocity(Vec3d vec3d, float f) {
        Vec3d vec3d2 = vec3d.normalize();
        Vec3d vec3d3 = vec3d2.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        if (vec3d3.lengthSquared() <= 1.0E-7) {
            vec3d3 = vec3d2.crossProduct(this.getOppositeRotationVector(1.0f));
        }
        Quaternion quaternion = new Quaternion(new Vector3f(vec3d3), 90.0f, true);
        Vector3f vector3f = new Vector3f(vec3d2);
        vector3f.rotate(quaternion);
        Quaternion quaternion2 = new Quaternion(vector3f, f, true);
        Vector3f vector3f2 = new Vector3f(vec3d2);
        vector3f2.rotate(quaternion2);
        return vector3f2;
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (itemStack.getItem() instanceof BannerItem) {
            super.loot(item);
        } else {
            Item item2 = itemStack.getItem();
            if (this.method_7111(item2)) {
                ItemStack itemStack2 = this.inventory.add(itemStack);
                if (itemStack2.isEmpty()) {
                    item.remove();
                } else {
                    itemStack.setCount(itemStack2.getCount());
                }
            }
        }
    }

    private boolean method_7111(Item item) {
        return this.hasActiveRaid() && item == Items.WHITE_BANNER;
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (super.equip(slot, item)) {
            return true;
        }
        int i = slot - 300;
        if (i >= 0 && i < this.inventory.getInvSize()) {
            this.inventory.setInvStack(i, item);
            return true;
        }
        return false;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
        boolean bl;
        Raid raid = this.getRaid();
        boolean bl2 = bl = this.random.nextFloat() <= raid.getEnchantmentChance();
        if (bl) {
            ItemStack itemStack = new ItemStack(Items.CROSSBOW);
            HashMap map = Maps.newHashMap();
            if (wave > raid.getMaxWaves(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (wave > raid.getMaxWaves(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }
            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.set(map, itemStack);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
        }
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }
}

